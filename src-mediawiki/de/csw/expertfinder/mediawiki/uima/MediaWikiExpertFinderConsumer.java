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
package de.csw.expertfinder.mediawiki.uima;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceProcessException;

import com.hp.hpl.jena.ontology.OntClass;

import de.csw.expertfinder.application.RevisionStore;
import de.csw.expertfinder.diff.PositionInsensitiveDiff;
import de.csw.expertfinder.document.Author;
import de.csw.expertfinder.document.Category;
import de.csw.expertfinder.document.Concept;
import de.csw.expertfinder.document.Document;
import de.csw.expertfinder.document.DocumentCategory;
import de.csw.expertfinder.document.PersistableEntity;
import de.csw.expertfinder.document.Revision;
import de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo;
import de.csw.expertfinder.mediawiki.uima.types.markup.ExternalLink;
import de.csw.expertfinder.mediawiki.uima.types.markup.InfoboxFact;
import de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink;
import de.csw.expertfinder.mediawiki.uima.types.markup.MathFormula;
import de.csw.expertfinder.mediawiki.uima.types.markup.Paragraph;
import de.csw.expertfinder.mediawiki.uima.types.markup.Reference;
import de.csw.expertfinder.mediawiki.uima.types.markup.Section;
import de.csw.expertfinder.mediawiki.uima.types.markup.Tag;
import de.csw.expertfinder.ontology.OntologyIndex;
import de.csw.expertfinder.persistence.PersistenceStoreFacade;
import de.csw.expertfinder.uima.types.Noun;
import de.csw.expertfinder.uima.types.OntologyConcept;
import de.csw.expertfinder.uima.types.Sentence;
import de.csw.expertfinder.uima.types.Word;

/**
 * This CAS consumer reads all annotations for one revision of a MediaWiki article,
 * constructs the application specific document structure out of them and persists
 * them if necessary.
 * 
 * Building the application specific document structure includes calculating the
 * differences between this and the previous revision. Only differences (added or
 * deleted content) is stored. Parts that remain unchanged content-wise but receive
 * changes with respect to their position within the document are updated.  
 * 
 * 
 * @author ralph
 *
 */
public class MediaWikiExpertFinderConsumer extends CasConsumer_ImplBase {

	private static final Logger log = Logger.getLogger(MediaWikiExpertFinderConsumer.class);
	
	private static final float[] milestones = new float[10];
	
	static {
		for (int level = 0; level < 10; ++level) {			
			milestones[level] = (float) (0.5/(Math.pow(2, level)));
		}
	}


	
	/**
	 * 
	 * @see org.apache.uima.collection.base_cpm.CasObjectProcessor#processCas(org.apache.uima.cas.CAS),
	 *      {@link MediaWikiExpertFinderConsumer}
	 */
	public void processCas(CAS cas) throws ResourceProcessException {
		
		JCas jCas;
		try {
			// get the currentVersion Sofa
			jCas = cas.getJCas();
			if ("".equals(jCas.getDocumentText())) {
				log.debug("Skipping empty revision.");
			}
				
		} catch (Exception e) {
			throw new ResourceProcessException(e);
		}
		
		// get the persistence store facade.
		PersistenceStoreFacade persistenceStore = PersistenceStoreFacade.get();
		
		HashMap<String, Concept> sharedConcepts = new HashMap<String, Concept>();
			
		try {
			
			persistenceStore.beginTransaction();


			// get all annotation types 
			// note: although these are the same for all cases (since we
			// do not deal with different subjects), they can only be
			// retrieved through the cas object itself. Unfortunately,
			// this has to be repeated for each cas.
			
			Type articleRevisionInfoType = jCas.getCasType(ArticleRevisionInfo.type);
			Type paragraphType = jCas.getCasType(Paragraph.type);
			Type sectionType = jCas.getCasType(Section.type);
			Type sentenceType = jCas.getCasType(Sentence.type);
			Type wordType = jCas.getCasType(Word.type);
			Type nounType = jCas.getCasType(Noun.type);
			Type ontologyConceptType = jCas.getCasType(OntologyConcept.type);
			Type internalLinkType = jCas.getCasType(InternalLink.type);
			Type externalLinkType = jCas.getCasType(ExternalLink.type);
			Type referenceType = jCas.getCasType(Reference.type);
			
	//		Type commentType = jCas.getCasType(Comment.type);
	//		Feature commentTypeFeature_text = commentType.getFeatureByBaseName("text");
			
			Type mathFormulaType = jCas.getCasType(MathFormula.type);
			Type infoboxFactType = jCas.getCasType(InfoboxFact.type);
			Type tagType = jCas.getCasType(Tag.type);
			
	
			AnnotationIndex index = jCas.getAnnotationIndex();
			FSIterator iter = index.iterator();
			
			
			Revision thisRevision = null;
			de.csw.expertfinder.document.Section currentSection = null;
	//		de.csw.expertfinder.document.Paragraph currentParagraph = null;
			de.csw.expertfinder.document.Sentence currentSentence = null;
			de.csw.expertfinder.document.Word currentWord = null;
			
			int articleId = -1;
			Document document = null;
			
			HashMap<Section, de.csw.expertfinder.document.Section> sections = new HashMap<Section, de.csw.expertfinder.document.Section>();
			
			boolean needsSave = true;
			
			// stores sections that could not be matched to concepts, so that we can later add concepts from the parent section with a
			// similarity weighting.
			int deepestSectionLevel = 0; 
			
			ArrayList<PersistableEntity<? extends Serializable>> objectsToSave = new ArrayList<PersistableEntity<? extends Serializable>>();
			
//			int sectionCount = 0;
						
			while(iter.hasNext()) {
				// But the good news is: The annotations come in ordered by their
				// positions. If there are smaller intervals contained by a larger
				// one (e.g. words inside a sentence), the larger one comes first.
				// So we don't need a structure like an interval tree or such to
				// figure out which interval is contained by which.
				// Nevertheless, this is not true for the whole revision and the first section.
				// This is why we have to keep hold of the first section.
				Annotation annot = (Annotation)iter.next();
				Type type = annot.getType();
				
				// UIMA type denominators are not constants, so we cannot do switch but have to use if...else.
				if (type == articleRevisionInfoType) {
					ArticleRevisionInfo revInfo = (ArticleRevisionInfo)annot;
					// this is a document wide annotation
					if (log.isDebugEnabled()) {
						log.debug("Article: " + revInfo.getArticleId());
						log.debug("Revision: " + revInfo.getRevisionId());
						log.debug("Author: " + revInfo.getAuthorName());
					}
	
					articleId = revInfo.getArticleId();
					String authorName = revInfo.getAuthorName();
	
					Author author = persistenceStore.getAuthor(authorName);
					if (author == null) {
						author = new Author(authorName);
						if (needsSave) objectsToSave.add(author);
					}
					
					document = persistenceStore.getDocument((long)articleId);
					long revisionCount;
					if (document == null) {
						document = new Document((long)articleId, revInfo.getTitle());
						revisionCount = 0;
					} else {
						revisionCount = persistenceStore.getRevisionCount(document);
					}
					
					int revisionId = revInfo.getRevisionId();
					
					thisRevision = persistenceStore.getRevision((long)revisionId);
					if (thisRevision == null) {
						// create new Revision
						thisRevision = new Revision((long)revisionId, document, author, new Date(revInfo.getTimestamp()));
						
						thisRevision.setCount(revisionCount + 1);
						
						// add or delete MediaWiki categories
						StringArray categoryNameArray = revInfo.getCategories();
						
						if (categoryNameArray != null) {
							int length = categoryNameArray.size();
							Set<DocumentCategory> previouslyAddedCategories = document.getCategories();
							HashMap<String, DocumentCategory> previouslyAddedCategoriesByNames = new HashMap<String, DocumentCategory>();
							HashSet<String> alreadyHandledCategories = new HashSet<String>(); // sometimes, category links are duplicated in the same revision. We only handle each one once.
							for (DocumentCategory documentCategory : previouslyAddedCategories) {
								previouslyAddedCategoriesByNames.put(documentCategory.getCategory().getName().toLowerCase(), documentCategory);
							}
							for (int i = 0; i < length; i++) {
								String categoryName = categoryNameArray.get(i).toLowerCase();
								if (alreadyHandledCategories.contains(categoryName))
									continue;
								
								alreadyHandledCategories.add(categoryName);
								DocumentCategory documentCategory = previouslyAddedCategoriesByNames.get(categoryName);
								
								// remove item so that we can later find out which categories have been
								// removed in this revision
								previouslyAddedCategoriesByNames.remove(categoryName);
								
								if (documentCategory == null) {
									// this category has not been added to the document yet
									// look if it is already known in the system
									Category category = persistenceStore.getCategory(categoryName);
									if (category == null) {
										// the category is unknown in the system
										category = new Category(categoryName);
										objectsToSave.add(category);
									}
									documentCategory = new DocumentCategory(document, category);
									documentCategory.setRevisionCreated(thisRevision);
									objectsToSave.add(documentCategory);
									document.addCategory(documentCategory);
								} else {
									// it is possible that this category has been added to this
									// document but has been deleted after. Thus we have to
									// "reactivate" it.
									if (documentCategory.getRevisionDeleted() != null) {
										documentCategory.setRevisionDeleted(null);
										objectsToSave.add(documentCategory);
									}
								}
							}
							
							// those categories that are left have been removed in this revision.
							Collection<DocumentCategory> removedCategories = previouslyAddedCategoriesByNames.values();
							for (DocumentCategory removedCategory : removedCategories) {
								if (removedCategory.getRevisionDeleted() == null) {
									removedCategory.setRevisionDeleted(thisRevision);
								}
							}
						}
					} else {
						// we have already seen this revision.
						needsSave = false;
					}
					
				} else {
					// all other annotations comprise a part of the document
					int begin = annot.getBegin();
					int end = annot.getEnd();
					
	//				We don't look at paragraphs, we look at sections instead.
	//				if (type == paragraphType) {
	//					Paragraph paragraph = (Paragraph)annot;
	//					log.debug("Paragraph: " + begin + "-" + end);
	//					if (currentSection != null && currentParagraph != null)
	//						currentSection.addParagraph(currentParagraph);
	//					currentParagraph = new de.csw.expertfinder.document.Paragraph();
	//				} else
					if (type == sectionType) {
//						sectionCount ++;
						Section section = (Section)annot;
						String sectionTitle = section.getTitle();
						
						if (log.isDebugEnabled())
							log.debug("Section: " + sectionTitle + " (" + begin + "-" + end + ")");
						
						int level = section.getLevel();
						
						if (level > deepestSectionLevel)
							deepestSectionLevel = level;
						
						currentSection = new de.csw.expertfinder.document.Section(section.getTitle(), level, section.getBegin(), section.getEnd());
//						if (thisRevision != null)
//							thisRevision.addSection(currentSection);
						sections.put(section, currentSection);
						de.csw.expertfinder.document.Section parentSection = sections.get(section.getParent());
						if (parentSection != null)
							currentSection.setParentSection(parentSection);
					} else if (type == nounType) {
						Noun noun = (Noun)annot;
						if (log.isDebugEnabled())
							log.debug("noun: " + begin + "-" + end + ": " + noun.getWordStem());
						if (currentSentence != null) {
							de.csw.expertfinder.document.Noun nounEntity = new de.csw.expertfinder.document.Noun(noun.getLemma(), noun.getWordStem(), begin, end);
							currentSentence.addWord(nounEntity);
							nounEntity.setSection(currentSection);
							
							// check for associated concepts
							StringArray conceptAnnotations = noun.getConceptURIs();
							if (conceptAnnotations != null) {
								int size = conceptAnnotations.size();
								for (int i=0; i<size; i++) {
									String conceptUri = conceptAnnotations.get(i);
									Concept concept = persistenceStore.getConcept(conceptUri);
									if (concept == null) {
										concept = new Concept(conceptUri);
										if (needsSave) persistenceStore.save(concept);
									}
								}
							}
						}
					} else if (type == wordType) {
						Word word = (Word)annot;
						if (log.isDebugEnabled())
							log.debug("word: " + begin + "-" + end + ": " + word.getWordStem());
						if (currentSentence != null) {
							de.csw.expertfinder.document.Word wordEntity = new de.csw.expertfinder.document.Word(word.getLemma(), word.getWordStem(), begin, end);
							currentSentence.addWord(wordEntity);
							wordEntity.setSection(currentSection);
						}
					} else if (type == sentenceType) {
						Sentence sentence = (Sentence)annot;
						if (log.isDebugEnabled())
							log.debug("sentence: " + begin + "-" + end);
						
						// we don't use paragraphs (yet), we add the sentence to the section directly
						if (currentSection != null && currentSentence != null && !currentSentence.getWords().isEmpty())
							currentSection.addSentence(currentSentence);
						de.csw.expertfinder.document.Sentence newSentence = new de.csw.expertfinder.document.Sentence();
						newSentence.setContextBefore(currentSentence);
						currentSentence = newSentence;
	//				} else if (type == ontologyConceptType) {
	//					OntologyConcept concept = (OntologyConcept)annot;
	//					if (log.isDebugEnabled())
	//						log.debug("concept: " + begin + "-" + end + ": ");
	//					int len = concept.getConceptURIs().size();
	//					for (int i=0; i<len; i++) {
	//						log.debug("  " + concept.getConceptURIs(i));
	//					}
					} else if (type == internalLinkType) {
						// TODO if time, implement link diff
						InternalLink link = (InternalLink)annot;
						if (log.isDebugEnabled())
							log.debug("link: " + begin + "-" + end + " - " + link.getCoveredText());
					} else if (type == externalLinkType) {
						ExternalLink linkAnnotation = (ExternalLink)annot;
						if (log.isDebugEnabled())
							log.debug("link: " + begin + "-" + end + " - " + linkAnnotation.getCoveredText());
					} else if (type == referenceType) {
						Reference reference = (Reference)annot;
						if (log.isDebugEnabled())
							log.debug("reference: " + begin + "-" + end);
					}
//					else if (type == mathFormulaType) {
//						MathFormula formula = (MathFormula)annot;
//						log.debug("formula: " + begin + "-" + end);
//					} else if (type == infoboxFactType) {
//						InfoboxFact infoboxFact = (InfoboxFact)annot;
//						log.debug("Infobox fact: " + begin + "-" + end + ": " + infoboxFact.getKey() + "=" + infoboxFact.getValue());
//					} else if (type == tagType) {
//						Tag tag = (Tag)annot;
//						log.debug("tag: " + begin + "-" + end + ": " + tag.getText());
//					}
				}
			}
			
			if (log.isDebugEnabled())
				log.debug("End of loop. Revision: " + thisRevision.getId());
			
			// add sections to revision
			Collection<de.csw.expertfinder.document.Section> sectionCollection = sections.values();
			
			for (de.csw.expertfinder.document.Section section : sectionCollection) {
				thisRevision.addSection(section);
			}
			
			// save this revision
			if (needsSave) objectsToSave.add(thisRevision);
			
			// Get the previous revision for comparison
			RevisionStore revisionStore = RevisionStore.get(articleId);
			Revision previousRevision = revisionStore.getLastRevision();

			List<de.csw.expertfinder.document.Word> addedWords;
			
			if (previousRevision != null) {
				
				// handle sections
				HashMap<String, de.csw.expertfinder.document.Section> newSections = makeSectionByTitleMap(thisRevision.getSections()); 
				HashMap<String, de.csw.expertfinder.document.Section> oldSections = makeSectionByTitleMap(previousRevision.getSections()); 

				// unchanged sections = intersection(oldSections, newSections)  
				// deleted sections = oldSections \ newSections  
				// added sections = newSections \ oldSections
				
				// we could use the removeAll and retainAll operations of Set to calculate the intersection and differences,
				// but these modify the sets and we would have to create them again. Thus, it is more efficient to do at least
				// two operations at one time.
								
				// we have to insert new sections in the order of their parent-child hierarchy (parents before children)
				// because otherwise we could get constraint violations in the persistence layer.
				TreeSet<Entry<String, de.csw.expertfinder.document.Section>> sortedOldEntrySet = new TreeSet<Entry<String,de.csw.expertfinder.document.Section>>(sectionsEntryByLevelComparator);
				sortedOldEntrySet.addAll(oldSections.entrySet());

//				HashMap<de.csw.expertfinder.document.Section, de.csw.expertfinder.document.Section> oldToNew = new HashMap<de.csw.expertfinder.document.Section, de.csw.expertfinder.document.Section>();
				
				for (Entry<String, de.csw.expertfinder.document.Section> oldEntry : sortedOldEntrySet) {
					de.csw.expertfinder.document.Section oldSection = oldEntry.getValue();
					de.csw.expertfinder.document.Section newSection = newSections.get(oldEntry.getKey());
					if (newSection == null) {
						// this section has been deleted in this revision
						// we don't delete it from the database but mark it as deleted
						oldSection.setRevisionDeleted(thisRevision);
						// since we have to update this object, we need to set the correct
						// parent section
//						de.csw.expertfinder.document.Section parentSection = oldSection.getParentSection();
//						if (parentSection == null)
//							oldSection.setParentSection(null);
//						else
//							oldSection.setParentSection(oldToNew.get(parentSection));

						if (needsSave) objectsToSave.add(oldSection);
					} else {
						// the old section still exists in the new revision, but probably
						// its position within the text has changed. Thus we overwrite the
						// existing section with the new one
						newSection.setId(oldSection.getId());
						newSection.setRevisionCreated(oldSection.getRevisionCreated());
						newSection.setConcepts(oldSection.getConcepts());
//						oldToNew.put(oldSection, newSection);
						if (needsSave) objectsToSave.add(newSection);
					}
				}
				
				
				// the other way round to determine sections that have been added in this revision
				TreeSet<Entry<String, de.csw.expertfinder.document.Section>> sortedNewEntrySet = new TreeSet<Entry<String,de.csw.expertfinder.document.Section>>(sectionsEntryByLevelComparator);
				sortedNewEntrySet.addAll(newSections.entrySet());

				for (Entry<String, de.csw.expertfinder.document.Section> newEntry : sortedNewEntrySet) {
					de.csw.expertfinder.document.Section newSection = newEntry.getValue();
					de.csw.expertfinder.document.Section oldSection = oldSections.get(newEntry.getKey());
					if (oldSection == null) {
						// this section has been added in this revision
						newSection.setRevisionCreated(thisRevision);
						
						// extract concepts
						extractConcepts(newSection, sharedConcepts);

						if (needsSave) objectsToSave.add(newSection);
					}
				}
				
				PositionInsensitiveDiff diff = new PositionInsensitiveDiff(previousRevision, thisRevision);
	
				List<de.csw.expertfinder.document.Word> deletedWords = diff.getDeletedWords();
				Collections.sort(deletedWords, de.csw.expertfinder.document.Word.POSITION);
				if (log.isDebugEnabled()) {
					log.debug("\nDeleted words:");
					for (de.csw.expertfinder.document.Word word : deletedWords) {
						log.debug(word.getWord() + ": " + word.getStartPos() + "-" + word.getEndPos());
					}
				}
				
				// update positions in unchanged words
				
				Set<Entry<de.csw.expertfinder.document.Word, de.csw.expertfinder.document.Word>> unchangedWords = diff.getUnchangedWords().entrySet();
				for (Entry<de.csw.expertfinder.document.Word, de.csw.expertfinder.document.Word> entry : unchangedWords) {
					de.csw.expertfinder.document.Word oldWord = entry.getKey();
					de.csw.expertfinder.document.Word newWord = entry.getValue();
					newWord.setId(oldWord.getId());
					newWord.setRevisionCreated(oldWord.getRevisionCreated());
					newWord.setConcept(oldWord.getConcept());
					if (needsSave) objectsToSave.add(newWord);
//					if (newWord instanceof de.csw.expertfinder.document.Noun) {
//						de.csw.expertfinder.document.Noun mergedWord = (de.csw.expertfinder.document.Noun)persistenceStore.merge(newWord);
//						log.info(mergedWord);
//					} else {
//						de.csw.expertfinder.document.Word mergedWord = (de.csw.expertfinder.document.Word)persistenceStore.merge(newWord);
//						log.info(mergedWord);
//					}
				}
				
				// mark deleted words as deleted
				for (de.csw.expertfinder.document.Word deletedWord : deletedWords) {
					deletedWord.setRevisionDeleted(thisRevision);
					if (needsSave) objectsToSave.add(deletedWord);
				}
				
				addedWords = diff.getAddedWords();
//				Collections.sort(addedWords, de.csw.expertfinder.document.Word.POSITION);
				if (log.isDebugEnabled()) {
					log.debug("\nAdded words:");
					for (de.csw.expertfinder.document.Word word : addedWords) {
						log.debug(word.getWord() + ": " + word.getStartPos() + "-" + word.getEndPos());
					}
				}
	
			} else {
				// This is the very first revision
				
				// all sections are new
				List<de.csw.expertfinder.document.Section> newSections = thisRevision.getSections();
				TreeSet<de.csw.expertfinder.document.Section> newSectionsSorted = new TreeSet<de.csw.expertfinder.document.Section>(sectionsByLevelComparator);
				newSectionsSorted.addAll(newSections);
				for (de.csw.expertfinder.document.Section section : newSectionsSorted) {
					section.setRevisionCreated(thisRevision);
					
					// extract concepts
					extractConcepts(section, sharedConcepts);

					if (needsSave) objectsToSave.add(section);
				}
				
				// all words are new
				addedWords = thisRevision.getWords();
			}
			
			// find concepts in added words and add them
			extractConcepts(addedWords, sharedConcepts);
						
			// save added words
			for (de.csw.expertfinder.document.Word word : addedWords) {
				word.setRevisionCreated(thisRevision);
				if (needsSave) objectsToSave.add(word);
			}
			
			if (needsSave) {
				for (PersistableEntity<? extends Serializable> objectToSave : objectsToSave) {
					persistenceStore.save(objectToSave);
				}
			}

			persistenceStore.commitChanges();

			revisionStore.setLastRevision(thisRevision);
			
			// free unneeded references to avoid clogging up memory
			if (previousRevision != null)
				previousRevision.setSections(null);

		} catch (Exception e) {
			e.printStackTrace();
			persistenceStore.rollbackChanges();
			log.error(e);
//		} finally {
//			persistenceStore.beginTransaction();
//			persistenceStore.clearCache();
//			persistenceStore.commitChanges();
		}

	}
	
	/**
	 * Takes a list of sections and returns a map containing the same sections as values with their titles as key.
	 * If more than one section with the same title exists, this method tries to disambiguate them by expanding
	 * each title to the full path including all parent section titles.
	 * @param sections
	 * @return
	 */
	private HashMap<String, de.csw.expertfinder.document.Section> makeSectionByTitleMap(List<de.csw.expertfinder.document.Section>  sections) {
		HashMap<String, de.csw.expertfinder.document.Section> result = new HashMap<String, de.csw.expertfinder.document.Section>();
		HashMap<String, List<de.csw.expertfinder.document.Section>> duplicates = new HashMap<String, List<de.csw.expertfinder.document.Section>>(); 

		for (de.csw.expertfinder.document.Section section : sections) {
			// 'escape' all occurences of the @ character, since we will use it below to disambiguate duplicate section titles 
			String title = section.getTitle().replace("@", "@@");
			if (result.containsKey(title)) {
				// In rare cases, there might be different sections with the same name in
				// the same revision.
				de.csw.expertfinder.document.Section duplicate = result.get(title);
				List<de.csw.expertfinder.document.Section> duplicateList = duplicates.get(title);
				if (duplicateList == null) {
					duplicateList = new ArrayList<de.csw.expertfinder.document.Section>();
					duplicateList.add(duplicate);
					duplicates.put(title, duplicateList);
				}
				duplicateList.add(section);
			} else {
				result.put(section.getTitle(), section);
			}
		}

		// Duplicate entries (Sections with the same title) are expanded to their full path within the section hierarchy (by prepending the parent section's
		// title recursively). The only thing that can happen is that some (not so clever) author has added two equally named sections within the same parent
		// section, which makes it impossible to distinguish them. In this case, ‘@‘ characters are appended until no more duplicate exists.
		Set<Entry<String, List<de.csw.expertfinder.document.Section>>> duplicateEntries = duplicates.entrySet();
		HashMap<String, de.csw.expertfinder.document.Section> disambiguatedSections = new HashMap<String, de.csw.expertfinder.document.Section>();
		// go through all sets of sections with duplicate titles
		for (Entry<String, List<de.csw.expertfinder.document.Section>> duplicateEntry : duplicateEntries) {
			
			String title = duplicateEntry.getKey();
			
			if (log.isDebugEnabled())
				log.debug("Found duplicate section with title " + title);

			result.remove(title); // the duplicated section has been added to the result set before. We have to remove it.
			
			List<de.csw.expertfinder.document.Section> duplicateSections = duplicateEntry.getValue();
			for (de.csw.expertfinder.document.Section section : duplicateSections) {
				title = section.getTitle();
				// expand all to their full path (titles separated by '@').
				for (de.csw.expertfinder.document.Section parentSection = section.getParentSection(); parentSection != null; parentSection = parentSection.getParentSection()) {
					title = parentSection.getTitle() + "@" + title;
				}
				
				while (result.containsKey(title)) {
					title += "@"; // if we still have duplicates here, simply append '@' until no more duplicate exists. 
				}
				
				if (log.isDebugEnabled())
					log.debug("Expanding title to " + title);
				result.put(title, section); 
			}

		}
		return result;
	}
	
	/**
	 * A Comparator comparing map entries with String as key and Section as value by the section's level (section with lower level < section with higher level)
	 */
	private static final Comparator<Entry<String,de.csw.expertfinder.document.Section>> sectionsEntryByLevelComparator = new Comparator<Entry<String,de.csw.expertfinder.document.Section>>() {
		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Entry<String, de.csw.expertfinder.document.Section> o1, Entry<String, de.csw.expertfinder.document.Section> o2) {
			int result = ((Integer)o1.getValue().getLevel()).compareTo((Integer)o2.getValue().getLevel());
			if (result != 0)
				return result;
			
			result = ((Integer)o1.getValue().getStartPos()).compareTo((Integer)o2.getValue().getStartPos());
			return result;
		}
	};

	/**
	 * A Comparator comparing sections by their level (section with lower level < section with higher level)
	 */
	private static final Comparator<de.csw.expertfinder.document.Section> sectionsByLevelComparator = new Comparator<de.csw.expertfinder.document.Section>() {
		public int compare(de.csw.expertfinder.document.Section o1, de.csw.expertfinder.document.Section o2) {
			int result = ((Integer)o1.getLevel()).compareTo((Integer)o2.getLevel());
			if (result != 0)
				return result;
			
			return ((Integer)o1.getStartPos()).compareTo((Integer)o2.getStartPos());
		}
	};
	
	/**
	 * Tries to match concepts to sections. Handles a list of sections. If a section cannot be matched to any concept, the section's parent
	 * sections are subsequently tried until a concept can be matched or the top section is reached. Concepts from parent sections are weighted
	 * less than concepts from the original section (using a milestone similarity measure). 
	 * @param section
	 * @param sharedConcepts
	 */
	private void extractConcepts(de.csw.expertfinder.document.Section section, HashMap<String, Concept> sharedConcepts) {
		PersistenceStoreFacade persistenceStore = PersistenceStoreFacade.get();
		List<OntClass> sectionConcepts = extractConcepts(section.getTitle());
		int level = section.getLevel();
		double sim = 1d;
		de.csw.expertfinder.document.Section tmpSection = section;
		
		// if no concept could be found, try to get concepts from the parent section
		while (sectionConcepts.isEmpty()) {
			tmpSection = tmpSection.getParentSection();
			if (tmpSection == null)
				break;
			sectionConcepts = extractConcepts(tmpSection.getTitle());
		}
		
		if (tmpSection == null)
			// no concept for any parent section
			return;
		
		int otherLevel = tmpSection.getLevel();
		if (otherLevel != level) {
			sim = 1 - milestones[otherLevel] + milestones[level];
		}
			
		for (OntClass ontClass : sectionConcepts) {
			String conceptURI = ontClass.getURI();
			Concept concept = persistenceStore.getConcept(conceptURI);
			if (concept == null) {
				concept = sharedConcepts.get(conceptURI);
				if (concept == null) {
					concept = new Concept(conceptURI);
					sharedConcepts.put(conceptURI, concept);
					persistenceStore.save(concept);
				}
			}
			section.addConcept(concept, sim);
		}
	}

	/**
	 * Tries to find matching concepts for each of the words in the given word
	 * list and adds the found concept to the words accordingly.
	 * 
	 * @param words
	 *            list of words
	 * @param sharedConcepts
	 *            the list of concept enties shared throughout the lifecycle of
	 *            this annotation process, such that not persisted concepts are
	 *            only created once, even if found several times.
	 */
	private void extractConcepts(List<de.csw.expertfinder.document.Word> words, HashMap<String, Concept> sharedConcepts) {
		OntologyIndex oi = OntologyIndex.get();
		int maxTermSize = oi.getMaximumCompoundTermSize();
		
		List<OntClass> result = new ArrayList<OntClass>();
		
		LinkedList<de.csw.expertfinder.document.Word> queue = new LinkedList<de.csw.expertfinder.document.Word>();
		
		Iterator<de.csw.expertfinder.document.Word> wordIter = words.iterator();
		// prefill queue
		for(int i=0; i<maxTermSize && wordIter.hasNext(); i++) {
			queue.add(wordIter.next());
		}
		
		while (wordIter.hasNext()) {
			de.csw.expertfinder.document.Word word = wordIter.next();
			
			// entail word in queue
			queue.add(word);
			// and remove head
			queue.remove();
			
			checkAndAddResult(oi, queue, sharedConcepts);
			
		}

		// handle the rest of the queue
		while(queue.size() > 1) {
			queue.remove();
			checkAndAddResult(oi, queue, sharedConcepts);
		}
		
	}

	/**
	 * Matches the given text (each subsequence of the words, with a maximum
	 * length of {@link OntologyIndex#getMaximumCompoundTermSize()}) against
	 * concepts from the ontology index and returns them in a list.
	 * 
	 * @param text
	 *            .
	 * @return
	 */
	private List<OntClass> extractConcepts(String text) {
		OntologyIndex oi = OntologyIndex.get();
		int maxTermSize = oi.getMaximumCompoundTermSize();
		
		List<String> words = Arrays.asList(text.split(OntologyIndex.DELIMITER_PATTERN_STRING));
		
		List<OntClass> result = new ArrayList<OntClass>();
		
		LinkedList<String> queue = new LinkedList<String>();
		
		Iterator<String> wordIter = words.iterator();
		// prefill queue
		for(int i=0; i<maxTermSize && wordIter.hasNext(); i++) {
			queue.add(wordIter.next());
		}
		
		while (wordIter.hasNext()) {
			String word = wordIter.next();
			
			// entail word in queue
			queue.add(word);
			// and remove head
			queue.remove();
			
			checkAndAddResult(oi, queue, result);
			
		}

		// handle the rest of the queue
		while(queue.size() > 1) {
			queue.remove();
			checkAndAddResult(oi, queue, result);
		}
		
		return result;
		
	}

	/**
	 * Gets matching OntClasses from the {@link OntologyIndex} for the word
	 * sequence in the given queue and adds them to the result list.
	 * 
	 * @param oi the OntologyIndex
	 * @param queue a queue containing a sequence of words
	 * @param result the result list
	 */
	private void checkAndAddResult(OntologyIndex oi, LinkedList<String> queue, List<OntClass> result) {
		Map<OntClass, Integer> matchingOntClasses = oi.getClassesMatchingTerms(queue);
		Set<OntClass> keySet = matchingOntClasses.keySet();
		for (OntClass ontClass : keySet) {
			result.add(ontClass);
		}
	}

	/**
	 * Gets matching OntClasses from the {@link OntologyIndex} for the word
	 * sequence in the given queue and adds them to the result list.
	 * 
	 * @param oi
	 *            the OntologyIndex
	 * @param queue
	 *            a queue containing a sequence of words
	 * @param sharedConcepts
	 *            a map of concepts that were newly created during the current
	 *            call of the annotation consuming method.
	 */
	private void checkAndAddResult(OntologyIndex oi, LinkedList<de.csw.expertfinder.document.Word> queue, HashMap<String, Concept> sharedConcepts) {
		ArrayList<String> wordsAsStrings = new ArrayList<String>(queue.size());
		for (de.csw.expertfinder.document.Word word : queue) {
			wordsAsStrings.add(word.getWordStem());
		}
		Map<OntClass, Integer> matchingOntClasses = oi.getClassesMatchingTerms(wordsAsStrings);
		
		if (matchingOntClasses.isEmpty())
			return;
		
		// Get best match (there may be more than one)
		Set<OntClass> keySet = matchingOntClasses.keySet();
		OntClass bestOntClass = null;
		int longestLength = -1;
		for (OntClass ontClass : keySet) {
			int length = matchingOntClasses.get(ontClass);
			if (length > longestLength) {
				bestOntClass = ontClass;
				longestLength = length;
			}
		}
		
		// Add concept, if it already exists, if not create it first.
		String conceptURI = bestOntClass.getURI();
		PersistenceStoreFacade persistenceStore = PersistenceStoreFacade.get();
		Concept concept = persistenceStore.getConcept(conceptURI);
		if (concept == null) {
			concept = sharedConcepts.get(conceptURI);
			if (concept == null) {
				concept = new Concept(conceptURI);
				sharedConcepts.put(conceptURI, concept);
				persistenceStore.save(concept);
			}
		}
		
		// add concept to all words that it spans
		for(int i=0; i<=longestLength; i++) {
			queue.get(i).setConcept(concept);
		}

	}
}
