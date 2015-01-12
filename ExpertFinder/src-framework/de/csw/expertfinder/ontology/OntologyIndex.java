/*******************************************************************************
 * This file is part of the Corporate Semantic Web Project at Freie Universitaet Berlin.
 * 
 * This work has been partially supported by the ``InnoProfile-Corporate Semantic Web" project funded by the German Federal
 * Ministry of Education and Research (BMBF) and the BMBF Innovation Initiative for the New German Laender - Entrepreneurial Regions.
 * 
 * http://www.corporate-semantic-web.de/
 * 
 * Freie Universitaet Berlin
 * Copyright (c) 2007-2013
 * 
 * Institut fuer Informatik
 * Working Group Corporate Semantic Web
 * Koenigin-Luise-Strasse 24-26
 * 14195 Berlin
 * 
 * http://www.mi.fu-berlin.de/en/inf/groups/ag-csw/
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA or see <http://www.gnu.org/licenses/>
 ******************************************************************************/
package de.csw.expertfinder.ontology;

import static de.csw.expertfinder.config.Config.Key.LANGUAGE;
import static de.csw.expertfinder.config.Config.Key.ONTOLOGY_BASE_CONCEPT_URI;
import static de.csw.expertfinder.config.Config.Key.ONTOLOGY_FILE;
import static de.csw.expertfinder.config.Config.Key.ONTOLOGY_INDEX_COMPOUND_TERMS;
import static de.csw.expertfinder.config.Config.Key.ONTOLOGY_SIMILARITY_MEASURE;
import static de.csw.expertfinder.config.Config.Key.STOPWORDS_FILEPATH;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import simpack.accessor.graph.JenaOntologyAccessor;
import simpack.api.IGraphAccessor;
import simpack.api.IGraphNode;
import simpack.api.impl.AbstractSimilarityMeasure;
import simpack.util.graph.GraphNode;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.twmacinta.util.MD5;

import de.csw.expertfinder.application.ApplicationData;
import de.csw.expertfinder.config.Config;
import de.csw.expertfinder.document.Concept;
import de.csw.expertfinder.document.ConceptSimilarity;
import de.csw.expertfinder.persistence.PersistenceStoreFacade;
import edu.northwestern.at.utils.corpuslinguistics.stemmer.DefaultStemmer;
import edu.northwestern.at.utils.corpuslinguistics.stemmer.Stemmer;

/**
 * This class implements an ontology index. Its principal use is to look up
 * concepts based on their labels.
 * 
 * Since labels can consist of more than one word (e.g.
 * "ontology engineering process"), this class provides means of looking up
 * concepts based on phrases rather than single words.
 * 
 * @author rheese
 * @author ralph
 */
public class OntologyIndex {
	
	private static final Logger log = Logger.getLogger(OntologyIndex.class);
	
	// word delimiters
	public static final String DELIMITER_PATTERN_STRING = "[\\s\\p{Punct}\\+\\=]+";

	/** Determines the maximum length of a compound term that can be looked up  */
	private int maxTermSize = 5; // default is 5 but can be changed in config file
	
	private HashMap<String, OntClass> classesByStemmedLabels;
	
	/** character that is used to concatenate two fragments in the context of a prefix index */
	public static final char PREFIX_SEPARATOR = ' ';
	
	private static OntologyIndex instance;
	
	/** Stemmer to get discriminators from a term. By default it is a GermanStemmer */
	private Stemmer stemmer;

	/** Index for mapping labels (in its stemmed version) to concepts of the ontology. */
	// TODO encapsulate in a separate class
	private Map<String, String[]> labelIdx = new HashMap<String, String[]>();
	
	private Map<String, OntClass> allConceptLabels;
	
	/** The set contains all prefixes of the concepts, see {@link #generatePrefixes(String)} */
	// TODO encapsulate in a separate class
	private Set<String> prefixIdx = new HashSet<String>(); 
	
	/** Jena model of the ontology */
	private OntModel model;
	
	private OntClass domainBaseConcept;
	private OntClass problemBaseConcept;
	
	private final HashSet<String> stopwords = new HashSet<String>();
	
	// SimPack OntologyAcessor for similarity calculations 
	private JenaOntologyAccessor ontologyAccessor;
	
	// Constructor for similarity measure (can be configured in the app's property file, has to be instanciated using Reflection
	private Constructor<AbstractSimilarityMeasure> similarityMeasureConstructor;
	
	
	/**
	 * Use {@link #get()} to retrieve an instance. The constructor creates an
	 * instance containing an empty ontology model.
	 */
	@SuppressWarnings("unchecked")
	private OntologyIndex() {
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
		
		try {
			maxTermSize = Config.getIntAppProperty(ONTOLOGY_INDEX_COMPOUND_TERMS);
		} catch (NumberFormatException e) {
			log.warn("The property " + ONTOLOGY_INDEX_COMPOUND_TERMS.getName() + " has not been set. Using default value of " + maxTermSize);
		}
		
		classesByStemmedLabels = new HashMap<String,OntClass>();
		
		stemmer = new DefaultStemmer();
		
		
		// read stop word list
		String stopWordListPath = Config.getAppProperty(STOPWORDS_FILEPATH);
		if (stopWordListPath == null) {
			log.warn("The property " + stopWordListPath + " is not set in the ExpertFinder properties file. Not fatal, but stopwords are not excluded from analysis.");
		} else {
			URL stopWordListURL = OntologyIndex.class.getResource(stopWordListPath);
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(stopWordListURL.openStream()));
				String line = null;
				while ((line = in.readLine()) != null) {
					stopwords.add(line.trim().toLowerCase());
				}
			} catch (IOException e) {
				log.error("Could not load stopword list file " + stopWordListURL, e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						log.warn("Could not load stopword list file " + stopWordListURL, e);
						// not fatal, let's keep on with our lives.
					}
				}
			}
		}
		
		// initialize similarity measure class
		String similarityMetricsClassName = Config.getAppProperty(ONTOLOGY_SIMILARITY_MEASURE);
		try {
			Class<AbstractSimilarityMeasure> similarityMeasureClass = (Class<AbstractSimilarityMeasure>)Class.forName(similarityMetricsClassName);
			similarityMeasureConstructor = similarityMeasureClass.getConstructor(IGraphAccessor.class, IGraphNode.class, IGraphNode.class);
		} catch (Exception e) {
			log.fatal(similarityMetricsClassName + " is not a legal value for the application property " + ONTOLOGY_SIMILARITY_MEASURE.getName() + ". This application cannot function without this value properly set!");
			throw new IllegalStateException(similarityMetricsClassName + " is not a legal value for the application property " + ONTOLOGY_SIMILARITY_MEASURE.getName() + ". This application cannot function without this value properly set!", e);
		}


	}

	/**
	 * @return the only OntologyIndex instance.
	 */
	public static OntologyIndex get() {
		if (instance == null)
			instance = new OntologyIndex();
		return instance;
	}
	
	public int getMaximumCompoundTermSize() {
		return maxTermSize;
	}

	/**
	 * @return the ontology model
	 */
	public OntModel getModel() {
		return model;
	}
	
	/**
	 * Adds all statments of the given model to this model.
	 * @param modelToAdd
	 */
	public void addModel(OntModel modelToAdd) {
		model.add(modelToAdd);
	}
	
	/**
	 * Tests if the given word is a stopword (contained in the stopword list
	 * specified in {@link Config}).
	 * @param word the word to test
	 * @return true if the given word is a stopword, false otherwise.
	 */
	public boolean isStopWord(String word) {
		return stopwords.contains(word.trim().toLowerCase());
	}

	
	/**
	 * Adds the given statment to this model.
	 * @param statement
	 */
	public void addStatement(Statement statement) {
		model.add(statement);
	}
	
	/**
	 * @return the stemmer used in the index
	 */
	public Stemmer getStemmer() {
		return stemmer;
	}
	
	/**
	 * Tries to find an ontology class for a given label. Since labels can consist of compound words, it returns the OntClass with the longest matching subsequence or null, if no subsequence matches.
	 * @param terms
	 * @return
	 */
	public Map<OntClass, Integer> getClassesMatchingTerms(List<String> words) {
		if (words.size() > maxTermSize)
			throw new IllegalArgumentException("The collection of terms passed to getClassMatchingTerms has a size of " + words.size() + ", but the maximum term length has been set to " + maxTermSize);
		
		HashMap<OntClass, Integer> result = new HashMap<OntClass, Integer>();
		StringBuilder stemmedLabelBuf = new StringBuilder();
		int size = words.size();
		for (int i=0; i<size; i++) {
			stemmedLabelBuf.append(stemmer.stem(words.get(i)).toLowerCase());
			stemmedLabelBuf.append(PREFIX_SEPARATOR);
			String stemmedLabel = stemmedLabelBuf.substring(0, stemmedLabelBuf.length() - 1);
			
			OntClass tmpResult = classesByStemmedLabels.get(stemmedLabel);
			if (tmpResult != null) {
				result.put(tmpResult, i);
			}
		}
		
		return result;
		
	}

	/**
	 * Look up <code>term</code> in the ontology and return a list of similar
	 * concepts (URIs) that corresponds to the term. The result is limited to
	 * <code>limit</code> number of concepts. The order is exact matches,
	 * synonyms, children, parents. The list does not contain duplicates. Never
	 * returns <code>null</code>.
	 * 
	 * @param term
	 *            term to be looked up
	 * @param limit
	 *            maximum number of concepts in the result
	 * @return a list of matching concepts URIs
	 */
	public List<String> getSimilarMatches(String term, int limit) {
		List<String> uris = getExactMatches(term);
		// difference between the limit and the current size of the result
		int free;
		
		// check if we reached the limit
		if (uris.size() > limit) {
			uris = uris.subList(0, limit);
			return uris;
		}
			
		
		Set<String> result = new HashSet<String>();
		List<String> tmp;

		result.addAll(uris);
		free = limit - result.size();
		
		// synonyms
		if (free > 0) {
			for (String u : uris) {
				tmp = getSynonyms(u);
				if (tmp.size() > free) {
					result.addAll(tmp.subList(0, free));
					free = 0;
					break;
				} else {
					result.addAll(tmp);
					free = limit - result.size();
				}
			}
		}
		
		// children
		if (free > 0) {
			for (String u : uris) {
				tmp = getChildren(u);
				if (tmp.size() > free) {
					result.addAll(tmp.subList(0, free));
					free = 0;
					break;
				} else {
					result.addAll(tmp);
					free = limit - result.size();
				}
			}
		}
			
		// parents
		if (free > 0) {
			for (String u : uris) {
				tmp = getParents(u);
				if (tmp.size() > free) {
					result.addAll(tmp.subList(0, free));
					free = 0;
					break;
				} else {
					result.addAll(tmp);
					free = limit - result.size();
				}
			}
		}

		return new ArrayList<String>(result);
	}

	/**
	 * Look up <code>term</code> in the ontology and return a list of similar
	 * concepts (URIs) that corresponds to the term. The list does not contain
	 * duplicates. Never returns <code>null</code>.
	 * 
	 * @param term
	 *            term to be looked up
	 * @return a list of matching concepts URIs
	 */
	public List<String> getSimilarMatches(String term) {
		List<String> uris = getExactMatches(term);
		
		Set<String> result = new HashSet<String>();
		result.addAll(uris);
		
		for (String u : uris) {
			result.addAll(getSynonyms(u));
			result.addAll(getChildren(u));
			result.addAll(getParents(u));
		}
		
		return new ArrayList<String>(result);
	}

	/**
	 * Similar to {@link #getSimilarMatches(String)}, but returns the labels
	 * instead of the URIs. The list contains no duplicates.
	 * 
	 * @return labels of the synonyms
	 */
	public List<String> getSimilarMatchLabels(String term) {
		return OntologyUtils.getLabels(getSimilarMatches(term));
	}

	/**
	 * Similar to {@link #getSimilarMatches(String, int)}, but returns the labels
	 * instead of the URIs. The list contains no duplicates.
	 * 
	 * @return labels of the synonyms
	 */
	public List<String> getSimilarMatchLabels(String term, int limit) {
		return OntologyUtils.getLabels(getSimilarMatches(term, limit));
	}

	/**
	 * @param term
	 *            term to be looked up
	 * @return true iff {@link #getFirstExactMatch(String)} does not return
	 *         <code>null</code>.
	 */
	public boolean hasExactMatches(String term) {
		return getFirstExactMatch(term) != null;
	}

	/**
	 * Look up <code>term</code> in the ontology and return a list of concepts
	 * (URIs) that corresponds to the term. It does not search for similar
	 * concepts. Never returns <code>null</code>.
	 * 
	 * @param term
	 *            term to be looked up
	 * @return a list of matching concepts URIs
	 */
	// TODO include a more flexible search using Levenshtein for words with a length > 5
	public List<String> getExactMatches(String term) {
		return getFromLabelIndex(stemmer.stem(term));
	}

	/**
	 * Similar to {@link #getExactMatches(String)}, but returns the labels
	 * instead of the URIs. The list contains no duplicates.
	 * 
	 * @return labels of the synonyms
	 */
	public List<String> getExactMatchLabels(String term) {
		return OntologyUtils.getLabels(getExactMatches(term));
	}

	/**
	 * Similar to {@link #getExactMatches(String)} but does only return the
	 * first match. It returns <code>null</code> if no match can be found.
	 * 
	 * @param term
	 *            term to be looked up
	 * @return first matching concept or <code>null</code>
	 */
	public String getFirstExactMatch(String term) {
		List<String> matches =  getFromLabelIndex(stemmer.stem(term));
		return matches.size() > 0 ? matches.get(0) : null;
	}

	/**
	 * Look up <code>URI</code> in the ontology and return a list of synonyms
	 * (URIs) that corresponds to the term. The matches for term are not
	 * included. The list contains no duplicates. Never returns
	 * <code>null</code>.
	 * 
	 * @param term
	 *            term to be looked up
	 * @return a list of synonym concepts URIs
	 */
	public List<String> getSynonyms(String uri) {
		Resource resource = model.getResource(uri);
		if (resource == null)
			return Collections.emptyList();
		
		Set<String> result = new HashSet<String>();

		// the one way
		StmtIterator stmtIt = resource.listProperties(OWL.equivalentClass);
		while(stmtIt.hasNext()) {
			RDFNode synonym = stmtIt.nextStatement().getObject();
			if (synonym.isResource() && !synonym.isAnon() /*&& !((Resource)synonym).hasLiteral(Jura.invisible, true)*/) {
				result.add(((Resource)synonym).getURI());
			}
		}
		
		// the other way
		ResIterator resIt = model.listResourcesWithProperty(RDFS.subClassOf, resource);
		while(resIt.hasNext()) {
			RDFNode synonym = resIt.nextResource();
			if (synonym.isResource() && !synonym.isAnon() /*&& !((Resource)synonym).hasLiteral(Jura.invisible, true)*/) {
				result.add(((Resource)synonym).getURI());
			}
		}
		
		return new ArrayList<String>(result);
	}

	/**
	 * Similar to {@link #getSynonyms(String)}, but returns the labels
	 * instead of the URIs. The list contains no duplicates.
	 * 
	 * @return labels of the synonyms
	 */
	public List<String> getSynonymLabels(String uri) {
		return OntologyUtils.getLabels(getSynonyms(uri));
	}

	/**
	 * Look up <code>uri</code> in the ontology and return a list of parent
	 * concepts (URIs). Synonyms are not considered. The list contains no
	 * duplicates. Never returns <code>null</code>.
	 * 
	 * @param term
	 *            term to be looked up
	 * @return a list of parent concepts URIs
	 */
	// TODO add all synonyms of the parents to the result
	public List<String> getParents(String uri) {
		Resource resource = model.getResource(uri);
		if (resource == null)
			return Collections.emptyList();
		
		List<String> result = new ArrayList<String>();

		StmtIterator parent = resource.listProperties(RDFS.subClassOf);
		while(parent.hasNext()) {
			RDFNode child = parent.nextStatement().getObject();

			if (child.isResource() && !child.isAnon() /*&& !((Resource)child).hasLiteral(Jura.invisible, true)*/) {
				result.add(((Resource)child).getURI());
			}
		}

		return result;
	}

	/**
	 * Similar to {@link #getParents(String)}, but returns the labels
	 * instead of the URIs. The list contains no duplicates.
	 * 
	 * @return labels of the parents
	 */
	public List<String> getParentLabels(String uri) {
		return OntologyUtils.getLabels(getParents(uri));
	}

	/**
	 * Look up <code>uri</code> in the ontology and return a list of child
	 * concepts (URIs). Synonyms are not considered. The list contains no
	 * duplicates. Never returns <code>null</code>.
	 * 
	 * @param term
	 *            term to be looked up
	 * @return a list of child concepts URIs
	 */
	// TODO add all synonyms of the children to the result
	public List<String> getChildren(String uri) {
		Resource resource = model.getResource(uri);
		if (resource == null)
			return Collections.emptyList();
		
		List<String> result = new ArrayList<String>();

		ResIterator child = model.listResourcesWithProperty(RDFS.subClassOf, resource);
		while(child.hasNext()) {
			Resource parent = child.nextResource();
//			if (!parent.hasLiteral(Jura.invisible, true)) {
				result.add(parent.getURI());
//			}
		}

		return result;
	}

	/**
	 * Similar to {@link #getChildren(String)} but returns the labels
	 * instead of the URIs. The list contains no duplicates.
	 * 
	 * @return labels of the children
	 */
	public List<String> getChildrenLabels(String uri) {
		return OntologyUtils.getLabels(getChildren(uri));
	}

	/**
	 * Load statements from an input stream to the model.
	 * 
	 * @param ontologyURL
	 *            the url to the ontology file
	 */
	public void load(URL ontologyURL) {
		log.info("Loading ontology from " + ontologyURL);
		if (model.isEmpty()) {
			InputStream is;
			try {
				is = ontologyURL.openStream();
				model.read(is, "");
			} catch (IOException e) {
				log.error("Could not read ontology from url " + ontologyURL.toExternalForm());
			}
			
			log.info("Creating concept label index");
			createIndex();
			
			String ontologyFile = Config.getAppProperty(ONTOLOGY_FILE);
			String baseConcept = Config.getAppProperty(ONTOLOGY_BASE_CONCEPT_URI);
			String nsPrefix = model.getNsPrefixURI("");
			
			log.info("Calculating concept distance matrix");
			ontologyAccessor = new JenaOntologyAccessor(
					nsPrefix,
					ontologyURL.toExternalForm(), 
					nsPrefix,
					baseConcept, 
					OntModelSpec.OWL_MEM_RDFS_INF);
			log.info("concept distance matrix done");
			
			
			try {
				String ontologyMD5 = MD5.asHex(MD5.getHash(ontologyURL));
				PersistenceStoreFacade p = PersistenceStoreFacade.get();
				p.beginTransaction();
				String oldMD5 = p.getApplicationData(ApplicationData.ONTOLOGY_MD5);
				if (oldMD5 == null || !ontologyMD5.equals(oldMD5)) {
					log.info("Ontology file has changed since the last application start. Recalculating concept similarites");
					p.saveApplicationData(ApplicationData.ONTOLOGY_MD5, ontologyMD5);
					p.clearConceptSimilarityCache();
				}
				p.endTransaction();
			} catch (IOException e) {
				log.error("Could not calculate MD5 for ontology at " + ontologyURL, e);
			}
		}
	}
	
	/**
	 * Adds a new entry to all indexes, e.g., label index, prefix index. The
	 * labels are retrieved from the URI.
	 */
	protected void createIndex() {
		log.debug("Creating index");
		if (model.size() == 0)
			return;
		
		allConceptLabels = new HashMap<String, OntClass>();
		
		ExtendedIterator iter = model.listClasses();
		while (iter.hasNext()) {
			OntClass ontClass = (OntClass)iter.next();
			
//			if (ontClass.hasLiteral(invisible, true))
//				continue;
			
			if (!ontClass.isAnon()) {
				// get all labels in the appropriate language
				ExtendedIterator labelIter = ontClass.listLabels(Config.getAppProperty(LANGUAGE));
				while (labelIter.hasNext()) {
					String label = ((Literal)labelIter.next()).getString().toLowerCase();
					
					allConceptLabels.put(label, ontClass);
					
					// Split each label into its words
					String[] labelWords = label.split(DELIMITER_PATTERN_STRING);
					int length = Math.min(labelWords.length, maxTermSize); // do not take into account more than maxTermSize words per label
					StringBuilder stemmedLabelBuf = new StringBuilder();
					for (int i=0; i<length; i++) {
						String labelWord = labelWords[i];
						if (stopwords.contains(labelWord)) {
							continue;
						}
						stemmedLabelBuf.append(stemmer.stem(labelWord));
						stemmedLabelBuf.append(PREFIX_SEPARATOR);
					}
					
					String stemmedLabel = stemmedLabelBuf.length() == 0 ? "" : stemmedLabelBuf.substring(0, stemmedLabelBuf.length() - 1);
					
					if (log.isDebugEnabled())
						log.debug(ontClass.getLocalName() + " -> " + stemmedLabel);
					
					if (classesByStemmedLabels.containsKey(stemmedLabelBuf)) {
						OntClass otherOntClass = classesByStemmedLabels.get(stemmedLabelBuf);
						if (!otherOntClass.equals(ontClass)) {
							log.warn("ontological class " + otherOntClass.getURI() + " has the same label as the class " + ontClass.getURI() + ". Ignoring the second one.");
						}
					} else {
						classesByStemmedLabels.put(stemmedLabel, ontClass);
					}
					
				}
				String label = OntologyUtils.getLabel(ontClass.getURI());
				// TODO maybe we should use the GermanAnalyzer at this place to have stop words removed
				addToLabelIndex(stemmer.stem(label), ontClass.getURI());
				addToPrefixIndex(label);
			}
		}
		log.debug("done");
	}

	/**
	 * <p>Returns the similarity of the two given classes, based on the similarity
	 * measure specified in the application property file.</p>
	 * <p>
	 * The value is only calculated one time and stored to a cache in the persistent store. On subsequent calls
	 * to this method with the same classes, the result is retrieved from the cache.
	 * </p>
	 * 
	 * @param c1
	 *            One class.
	 * @param c2
	 *            The other class.
	 * @return the similarity of the two given classes, based on the similarity
	 *         measure specified in the application property file.
	 */
	public double getSimilarity(OntClass c1, OntClass c2) {
		String uri1 = c1.getURI();
		String uri2 = c2.getURI();

		PersistenceStoreFacade p = PersistenceStoreFacade.get();
		p.beginTransaction();
		
		Double sim = p.getConceptSimilarity(uri1, uri2);
		
		if (sim == null) {
			AbstractSimilarityMeasure similarityMeasure;
			try {
				similarityMeasure = similarityMeasureConstructor.newInstance(ontologyAccessor, new GraphNode(uri1), new GraphNode(uri2));
			} catch (Exception e) {
				log.error("Error instanciating similarity measure class.", e);
				p.rollbackChanges();
				return -1;
			}
			
			similarityMeasure.calculate();
			sim = similarityMeasure.getSimilarity();
			
			Concept concept1 = p.getConcept(uri1);
			Concept concept2 = p.getConcept(uri2);
			
			// this should actually not happen. We should only get called with existing concepts.
			if (concept1 == null) {
				log.warn("Concept with URI " + uri1 + " did not exist in the database. ");
				concept1 = new Concept(uri1);
				p.save(concept1);
			}
			if (concept2 == null) {
				log.warn("Concept with URI " + uri2 + " did not exist in the database. "); 
				concept2 = new Concept(uri2);
				p.save(concept2);
			}
			
			ConceptSimilarity conceptSimilarity = new ConceptSimilarity(concept1, concept2, sim);
			p.save(conceptSimilarity);
			
			p.commitChanges();
		} else {
			p.endTransaction();
		}
		
		return sim;
		
	}
	
	/**
	 * Returns all concept labels from the ontology
	 * @return
	 */
	public Set<String> getAllConceptLabels() {
		return allConceptLabels.keySet();
	}
	
	public OntClass getOntClass(String label) {
		return allConceptLabels.get(label);
	}
		
	/**
	 * @param term
	 *            a term (can consist of multiple words)
	 * @return true iff prefix is contained in the index.
	 */
	public boolean isPrefix(String term) {
		return isPrefix(Arrays.asList(explode(term)));
	}

	/**
	 * Tests, if the concatenation of the given fragments are contained in the
	 * prefix index. The order is preserved.
	 * 
	 * @param fragments
	 *            a list of terms
	 * @return true iff there is a prefix consisting of
	 */
	public boolean isPrefix(Collection<String> fragments) {
		List<String> stems = new ArrayList<String>();
		for (String f : fragments) {
			stems.add(stemmer.stem(f));
		}
		return prefixIdx.contains(implode(stems));
	}

	/**
	 * Generates the prefixes of a term. If {@link #explode(String)} returns an
	 * array f1..fn with n > 1 then all terms of the form implode(f1..fi) with
	 * 1<=i<n are prefixes (see {@link #implode(Collection)}.
	 * 
	 * @param term
	 *            a term
	 * @return the prefix corresponding to a term.
	 */
	protected List<String> generatePrefixes(String term) {
		if (term == null)
			throw new NullPointerException("Parameter term must not be null");
		
		// TODO normalization of the term, e.g., remove all punctuation, '-', etc.
		String[] fragments = explode(term);
		if (fragments.length <= 1)
			return Collections.emptyList();
		
		List<String> result = new ArrayList<String>();
		String prefix = stemmer.stem(fragments[0]);
		result.add(prefix);
			
		for (int i = 1; i < fragments.length - 1; i++) {
			prefix = implode(prefix, stemmer.stem(fragments[i]));
			result.add(prefix);
		}
		
		return result;
	}

	/**
	 * Split a string into fragment (at whitespaces) in the context of a prefix
	 * index. Not stemmed.
	 * 
	 * @param term
	 *            a term
	 * @return a list of fragments
	 */
	public String[] explode(String term) {
		return StringUtils.split(term);
	}

	/**
	 * Concatenate two fragments in the context of a prefix index.
	 * 
	 * @param f1
	 *            first fragment
	 * @param f2
	 *            second fragment
	 * @return concatenation
	 */
	protected String implode(String f1, String f2) {
		return f1 + PREFIX_SEPARATOR + f2;
	}

	/**
	 * Concatenate fragments in the context of a prefix index.
	 * 
	 * @param c
	 *            collection of fragments
	 * @return concatenation
	 */
	protected String implode(Collection<String> c) {
		return StringUtils.join(c, PREFIX_SEPARATOR);
	}
	
	/**
	 * Convenience method for handling the array of the label index. Adds an
	 * entry into the index. The key is taken as given.
	 * 
	 * @param key
	 *            a label
	 * @param uri
	 *            the URI of a concept
	 */
	protected void addToLabelIndex(String key, String uri) {
		Set<String> value = new HashSet<String>();
		if (labelIdx.containsKey(key)) {
			value.addAll(Arrays.asList(labelIdx.get(key)));
		}
		value.add(uri);
		String[] s = new String[value.size()];
		value.toArray(s);
		
		labelIdx.put(key, s);
//		log.trace("** Updated index with " + key + " => " + value);
	}
	
	/**
	 * Adds all prefixes of term to the prefix index.
	 * 
	 * @param term
	 *            a label
	 */
	protected void addToPrefixIndex(String term) {
		List<String> prefixes = generatePrefixes(term);
		if (!prefixes.isEmpty()) {
			prefixIdx.addAll(prefixes);
//			log.trace("** Updated prefix with " + prefixes);
		}
	}

	/**
	 * Convenience method for handling the array of the label index.Look up key
	 * in the index and return corresponding URIs. Never returns
	 * <code>null</code>.
	 * 
	 * @param key
	 *            key to be looked up
	 * @return list of corresponding URIs
	 */
	protected List<String> getFromLabelIndex(String key) {
		if (labelIdx.containsKey(key))
			return Arrays.asList(labelIdx.get(key));
		else
			return Collections.emptyList(); 
	}
	
	/**
	 * Clear ontology model, indexes, and all other stuff.
	 */
	public void reset() {
		model.removeAll();
		labelIdx.clear();
		prefixIdx.clear();
	}
	
	public Map<String, String[]> getLabelIndex() {
		return labelIdx;
	}
	
	public Set<String> getPrefixIndex() {
		return prefixIdx;
	}


}
