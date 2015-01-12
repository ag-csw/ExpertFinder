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
package de.csw.expertfinder.test.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import de.csw.expertfinder.document.Author;
import de.csw.expertfinder.document.Category;
import de.csw.expertfinder.document.Concept;
import de.csw.expertfinder.document.ConceptSimilarity;
import de.csw.expertfinder.document.Document;
import de.csw.expertfinder.document.DocumentCategory;
import de.csw.expertfinder.document.PersistableEntity;
import de.csw.expertfinder.document.Revision;
import de.csw.expertfinder.document.Section;
import de.csw.expertfinder.document.Word;
import de.csw.expertfinder.persistence.PersistenceStoreFacade;

/**
 * Tests the hibernate persistence store
 * 
 * @author ralph
 */
public class TestPersistenceStore {

	private static final Logger log = Logger.getLogger(TestPersistenceStore.class);

	private List<PersistableEntity<? extends Serializable>> objectsToDelete = new ArrayList<PersistableEntity<? extends Serializable>>();

	@SuppressWarnings("deprecation")
	// @BeforeClass
	public void setUp() {
		PersistenceStoreFacade p = PersistenceStoreFacade.get();
		p.beginTransaction();

		Document document1 = new Document(123456789, "Document 1");
		Document document2 = new Document(123456790, "Document 2");

		Author author = new Author("Ralph");

		Revision revision11 = new Revision(11L, document1, author, new Date(2000, 11, 24, 12, 0, 0));
		Revision revision12 = new Revision(12L, document1, author, new Date(2000, 11, 31, 23, 59, 59));

		Revision revision21 = new Revision(21L, document2, author, new Date(2000, 11, 24, 12, 0, 0));
		Revision revision22 = new Revision(22L, document2, author, new Date(2000, 11, 31, 23, 59, 59));

		Category category1 = new Category("Category 1");
		Category category2 = new Category("Category 2");

		DocumentCategory category11 = new DocumentCategory(document1, category1);
		category11.setRevisionCreated(revision12);
		DocumentCategory category12 = new DocumentCategory(document1, category2);
		category12.setRevisionCreated(revision11);
		DocumentCategory category21 = new DocumentCategory(document2, category1);
		category21.setRevisionCreated(revision21);
		DocumentCategory category22 = new DocumentCategory(document2, category2);
		category22.setRevisionCreated(revision21);

		save(author);

		save(document1);
		save(document2);

		save(revision11);
		save(revision12);
		save(revision21);
		save(revision22);

		save(category1);
		save(category2);

		save(category11);
		save(category12);
		save(category21);
		save(category22);

		p.commitChanges();
	}

	/**
	 * 
	 */
	// @Test
	public void testCategories() {
		PersistenceStoreFacade p = PersistenceStoreFacade.get();
		p.beginTransaction();

		try {
			Document d = p.getDocument(123456789L);
			Set<DocumentCategory> categories = d.getCategories();
			for (DocumentCategory documentCategory : categories) {
				System.out.println(documentCategory.getCategory().getName());
			}
		} catch (RuntimeException e) {
			// TestNG cuts of the interesting parts of the stack trace.
			e.printStackTrace();
			throw e;
		}

		p.endTransaction();
	}

	private void save(PersistableEntity<? extends Serializable> entity) {
		PersistenceStoreFacade p = PersistenceStoreFacade.get();
		p.save(entity);
		objectsToDelete.add(entity);
	}

	// @Test
	public void testWordsAndSectionsOfLatestRevision() {
		PersistenceStoreFacade p = PersistenceStoreFacade.get();
		p.beginTransaction();

		Document document = p.getDocument("Boeing 737");
		Revision latestRevision = p.getLatestPersistedRevisionLoadFull(document);
		log.debug(latestRevision.getAuthor());
		for (Section section : latestRevision.getSections()) {
			log.info("Section: " + section.getTitle());
			for (Word word : section.getWords()) {
				log.info(word + ": " + word.getStartPos() + "-" + word.getEndPos());
			}
		}

		p.commitChanges();
	}

	/**
	 * Tests saving of a section and associated concepts (n-m relation)
	 */
	@Test
	public void testSectionConcepts() {
		PersistenceStoreFacade p = PersistenceStoreFacade.get();

		p.beginTransaction();

		Concept c1 = new Concept("http://test.ontology#Test1");
		Concept c2 = new Concept("http://test.ontology#Test2");

		p.save(c1);
		p.save(c2);

		Section s1 = new Section("Test Section1", 0, 0, 0);
		s1.addConcept(c1, 1);
		s1.addConcept(c2, 1);

		p.save(s1);
		p.endTransaction();
		
		p.beginTransaction();

		Section s2 = new Section("Test Section2", 0, 0, 0);
		s2.addConcept(c1, 0.5);
		s2.addConcept(c2, 0.8);

		p.save(s2);

		p.commitChanges();

	}
	
	@Test
	public void testBla() {
		PersistenceStoreFacade p = PersistenceStoreFacade.get();
		p.beginTransaction();
		
		Section s1 = (Section)p.getEntity(1L, Section.class);
		
		p.endTransaction();
		p.beginTransaction();
		
		Section sNew = new Section(s1.getTitle(), s1.getLevel(), s1.getStartPos() + 10, s1.getEndPos() + 10);
		sNew.setId(s1.getId());
		sNew.setConcepts(s1.getConcepts());
		
		p.save(sNew);
		p.commitChanges();
	}

//	@Test
	public void testConceptSimilarities() {
		PersistenceStoreFacade p = PersistenceStoreFacade.get();

		// populate db with test entities
		p.beginTransaction();

		Concept c1 = new Concept("http://www.ag-csw.de/ontologies/test#A");
		Concept c2 = new Concept("http://www.ag-csw.de/ontologies/test#B");
		Concept c3 = new Concept("http://www.ag-csw.de/ontologies/test#C");
		Concept c4 = new Concept("http://www.ag-csw.de/ontologies/test#D");
		Concept c5 = new Concept("http://www.ag-csw.de/ontologies/test#E");

		ConceptSimilarity s12 = new ConceptSimilarity(c1, c2, .02);
		ConceptSimilarity s13 = new ConceptSimilarity(c1, c3, .01);
		ConceptSimilarity s14 = new ConceptSimilarity(c1, c4, .03);
		ConceptSimilarity s15 = new ConceptSimilarity(c1, c5, .04);
		ConceptSimilarity s23 = new ConceptSimilarity(c2, c3, .05);
		ConceptSimilarity s24 = new ConceptSimilarity(c2, c4, .08);
		ConceptSimilarity s25 = new ConceptSimilarity(c2, c5, .08);
		ConceptSimilarity s34 = new ConceptSimilarity(c3, c4, .11);
		ConceptSimilarity s35 = new ConceptSimilarity(c3, c5, .15);
		ConceptSimilarity s45 = new ConceptSimilarity(c4, c5, .15);

		p.save(c1);
		p.save(c2);
		p.save(c3);
		p.save(c4);
		p.save(c5);

		p.save(s12);
		p.save(s13);
		p.save(s14);
		p.save(s15);
		p.save(s23);
		p.save(s24);
		p.save(s25);
		p.save(s34);
		p.save(s35);
		p.save(s45);

		p.commitChanges();

		// test
		p.beginTransaction();

		Double sim13 = p.getConceptSimilarity(c1, c3);
		assert (sim13.equals(s13.getSimilarity()));

		sim13 = p.getConceptSimilarity(c1.getUri(), c3.getUri());
		assert (sim13.equals(s13.getSimilarity()));

		List<ConceptSimilarity> s1 = p.getBestConceptSimilarities(c1, 4);
		assert (s1.size() == 4);
		assert (s1.get(0).getConcept2().equals(c5) && s1.get(0).getSimilarity() == .04);
		assert (s1.get(1).getConcept2().equals(c4) && s1.get(1).getSimilarity() == .03);
		assert (s1.get(2).getConcept2().equals(c2) && s1.get(2).getSimilarity() == .02);
		assert (s1.get(3).getConcept2().equals(c3) && s1.get(3).getSimilarity() == .01);

		List<Concept> s2 = p.getMostSimilarConcepts(c2);
		assert (s2.size() == 2);
		assert (s2.get(0).equals(c4) || s2.get(0).equals(c5));
		assert (!s2.get(0).equals(s2.get(1)));
		assert (s2.get(1).equals(c4) || s2.get(1).equals(c5));

		List<Concept> s3 = p.getMostSimilarConcepts(c5);
		assert (s3.size() == 2);
		assert (s3.get(0).equals(c3) || s3.get(0).equals(c4));
		assert (!s3.get(0).equals(s3.get(1)));
		assert (s3.get(1).equals(c3) || s3.get(1).equals(c4));

		p.endTransaction();

		// cleanup
		p.beginTransaction();

		p.delete(s12);
		p.delete(s13);
		p.delete(s14);
		p.delete(s15);
		p.delete(s23);
		p.delete(s24);
		p.delete(s25);
		p.delete(s34);
		p.delete(s35);
		p.delete(s45);

		p.delete(c1);
		p.delete(c2);
		p.delete(c3);
		p.delete(c4);
		p.delete(c5);

		p.commitChanges();
	}
	
//	@Test
	public void testRevisionCount() {
		PersistenceStoreFacade p = PersistenceStoreFacade.get();
		p.beginTransaction();
		
		Document d = p.getDocument("JFace");
		long i = p.getRevisionCount(d);
		System.out.println(i);
		
		p.endTransaction();
	}

	/**
	 * 
	 */
	// @AfterClass
	@SuppressWarnings("rawtypes")
	public void tearDown() {
		PersistenceStoreFacade p = PersistenceStoreFacade.get();
		p.beginTransaction();

		// delete everything (in the reverse order of insertion
		// in order to avoid constraints violation)

		@SuppressWarnings("unused") // Compiler infers this is never used while it is.
		Class lastEntityClass = null;

		int size = objectsToDelete.size();
		for (int i = size - 1; i != -1; i--) {
			PersistableEntity<? extends Serializable> objectToDelete = objectsToDelete.get(i);
			p.delete(objectToDelete);
			Class thisEntityClass = objectToDelete.getClass();
			// if (lastEntityClass == thisEntityClass) {
			// p.flush();
			// }
			lastEntityClass = thisEntityClass;
		}

		p.commitChanges();
	}

}
