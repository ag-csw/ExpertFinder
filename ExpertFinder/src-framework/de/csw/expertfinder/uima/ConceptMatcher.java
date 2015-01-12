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
package de.csw.expertfinder.uima;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;

import de.csw.expertfinder.ontology.OntologyIndex;
import de.csw.expertfinder.uima.types.Noun;

/**
 * This class detects concepts based on the word stems of nouns and
 * their synsets from openthesaurus (in german texts) in a domain
 * ontology that covers the domain of interest according to the particular
 * application of this system.
 * 
 * The concept matching algorithm simply relies on rdf labels. It assumes
 * that terms describing a concept are no longer than three words.
 * 
 * @author ralph
 *
 */
public class ConceptMatcher extends JCasAnnotator_ImplBase {
	
	private static final Logger log = Logger.getLogger(ConceptMatcher.class);

	private final String CONFIG_PARAM_ONTOLOGY_FILE = "ontologyFile";
	
	private final OntologyIndex ontologyIndex = OntologyIndex.get();
	
	/**
	 * @see org.apache.uima.analysis_component.AnalysisComponent_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		String ontologyFilePath = (String)context.getConfigParameterValue(CONFIG_PARAM_ONTOLOGY_FILE);
		try {
			ontologyIndex.load(ConceptMatcher.class.getResource(ontologyFilePath));
		} catch (Exception e) {
			log.error("Could not load ontology from url " + ontologyFilePath, e);
			throw new ResourceInitializationException(e);
		}
	}
	
	/**
	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		// we need the nouns and their word stem annotations here.
		FSIterator nounAnnotIter = cas.getAnnotationIndex(Noun.type).iterator();
		while(nounAnnotIter.hasNext()) {
			Noun noun = (Noun)nounAnnotIter.next();
			List<String> matchingConceptURIs = ontologyIndex.getExactMatches(noun.getWordStem());
			int length = matchingConceptURIs.size();
			if (length != 0) {
//				OntologyConcept concept = new OntologyConcept(cas);
//				concept.setBegin(noun.getBegin());
//				concept.setEnd(noun.getEnd());
				StringArray conceptURIs = new StringArray(cas, length);
				conceptURIs.copyFromArray(matchingConceptURIs.toArray(new String[length]), 0, 0, length);
				noun.setConceptURIs(conceptURIs);
				conceptURIs.addToIndexes();
//				concept.setConceptURIs(conceptURIs);
//				concept.addToIndexes();
			}
		}
	}

}
