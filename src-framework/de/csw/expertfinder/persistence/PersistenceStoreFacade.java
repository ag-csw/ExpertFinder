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
package de.csw.expertfinder.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import de.csw.expertfinder.application.ApplicationData;
import de.csw.expertfinder.document.Author;
import de.csw.expertfinder.document.Category;
import de.csw.expertfinder.document.Concept;
import de.csw.expertfinder.document.ConceptSimilarity;
import de.csw.expertfinder.document.Document;
import de.csw.expertfinder.document.PersistableEntity;
import de.csw.expertfinder.document.Revision;
import de.csw.expertfinder.document.Section;
import de.csw.expertfinder.document.Word;
import de.csw.expertfinder.expertise.AuthorContribution;
import de.csw.expertfinder.expertise.AuthorCredibility;
import de.csw.expertfinder.util.Pair;

/**
 * This class implements all application specific persistence related
 * operations and hides the underlying persistence mechanism from the
 * rest of the application.
 *  
 * @author ralph
 */
public class PersistenceStoreFacade {
	
	private static PersistenceStoreFacade instance;
	
	private static final Logger log = Logger.getLogger(PersistenceStoreFacade.class);
	
	/**
	 * Gets the singleton instance of this class.
	 * @return the singleton instance of this class.
	 */
	public static PersistenceStoreFacade get() {
		if (instance == null) {
			instance = new PersistenceStoreFacade();
		}
		return instance;
	}
	
	private SessionFactory sessionFactory;

	/**
	 * private Constructor for {@link PersistenceStoreFacade}
	 */
	private PersistenceStoreFacade() {		
		Configuration conf = new Configuration();
		sessionFactory = conf.configure().buildSessionFactory();
	}
	
	/**
	 * Begins a new transaction.
	 */
	public void beginTransaction() {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
	}

	/**
	 * Commits all changes that have been made during the current transaction to
	 * the persistence layer.
	 */
	public void commitChanges() {
		Session session = sessionFactory.getCurrentSession();
		session.getTransaction().commit();
	}
	
	/**
	 * Ends a read-only transaction.
	 */
	public void endTransaction() {
		// the hibernate session does not distinguish between read only and
		// modifiying transactions. Read-only transactions are ended by calling
		// commit.
		
		commitChanges();
	}
	
	public void rollbackChanges() {
		Session session = sessionFactory.getCurrentSession();
		session.getTransaction().rollback();
	}
	
	/**
	 * Persists the given entity's state to the database.
	 * @param entity
	 */
	public void save(PersistableEntity<? extends Serializable> entity) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(entity);
	}
	
	/**
	 * Deletes the given entity from the database.
	 * @param entity
	 */
	public void delete(PersistableEntity<? extends Serializable> entity) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(entity);
	}

	/**
	 * Merges the given entity's properties with its equivalent (an entity with
	 * the same identifier) stored in the current session and persists it. If no
	 * such object exists in the session, it will be loaded from the persistence
	 * layer.
	 * 
	 * @param entity
	 * @return the merged entity.
	 */
	@SuppressWarnings("unchecked")
	public PersistableEntity<? extends Serializable> merge(PersistableEntity<? extends Serializable> entity) {
		Session session = sessionFactory.getCurrentSession();
		return (PersistableEntity<? extends Serializable>) session.merge(entity);
	}
	
	
	
	/**
	 * Retrieves a document by its title.
	 * @param title the document's title
	 * @return
	 */
	public Document getDocument(String title) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Document.class).add(Restrictions.eq("title", title));
		Document result = (Document)criteria.uniqueResult();
		return result;
	}
	
	/**
	 * Retrieves a document by its title.
	 * @param title the document's title
	 * @return
	 */
	public Document getDocument(Long id) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Document.class).add(Restrictions.eq("id", id));
		Document result = (Document)criteria.uniqueResult();
		return result;
	}
	
	/**
	 * Gets a Revision by id. Sections and words are not loaded.
	 * @return the Revision with the specified id
	 */
	public Revision getRevision(Long id) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Revision.class).add(Restrictions.eq("id", id));
		Revision result = (Revision)criteria.uniqueResult();
		return result;
	}
	
	/**
	 * Gets the latest revision for the given document in the database. Words and sections are not loaded.
	 * @param document
	 * @return
	 */
	public Revision getLatestPersistedRevision(Document document) {
		Session session = sessionFactory.getCurrentSession();
		Query query = 
			session.createQuery("from Revision where id = (select max(id) from Revision r where r.document = :document)").
			setEntity("document", document);
		Revision result = (Revision)query.uniqueResult();
		return result;
	}
	
	
	/**
	 * Gets the latest revision for the document with the given id in the database. Words and sections are not loaded.
	 * @param documentId
	 * @return
	 */
	public Revision getLatestPersistedRevision(Long documentId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = 
			session.createQuery("from Revision where id = (select max(id) from Revision r where r.document.id = :documentId)").
			setLong("documentId", documentId);
		Revision result = (Revision)query.uniqueResult();
		return result;
	}
	
	/**
	 * Returns the number of revisions the given document has.
	 * @param document a document
	 * @return the number of revisions the given document has.
	 */
	public long getRevisionCount(Document document) {
		Session session = sessionFactory.getCurrentSession();
		Query query = 
			session.createQuery("select distinct count(r.id) from Revision r, Document d where r.document = :document group by d.id").
			setEntity("document", document);
		
		Object result = query.uniqueResult();
		
		return result == null ? 0L : (Long)result;
	}

	/**
	 * Gets the latest revision for the given document in the database with words and sections loaded.
	 * @param document
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Revision getLatestPersistedRevisionLoadFull(Document document) {
		Session session = sessionFactory.getCurrentSession();
		
		// get the latest revision
		Revision revision = getLatestPersistedRevision(document);

		// get all current (not deleted) sections for this document (those that exist in the latest revision)
		// Note that sections are not directly connected to one revision. 
		Query query = 
			session.createQuery("select s from Section s, Revision r " + 
					"where s.revisionDeleted is null " + 
					"and s.revisionCreated = r " + 
					"and r.document = :document " + 
					"order by s.startPos").
			setEntity("document", document);
		List<Section> sections = query.list();
		
		revision.setSections(sections);
		
		// get words for each section.
		for (Section section : sections) {
			query =
				session.createQuery("from Word w " + 
						"where w.revisionDeleted is null " + 
						"and w.section = :section " + 
						"order by w.startPos").
				setEntity("section", section);
			List<Word> wordsForSection = query.list();
			section.setWords(wordsForSection);
		}
		
		return revision;
	}
	
	/**
	 * Returns all distinct words as a set of Strings for a given document.
	 * @param document
	 * @return all distinct words as a set of Strings for the given document
	 */
	public List<String> getBagOfWordsForLatestRevision(Document document) {
		Session session = sessionFactory.getCurrentSession();
		SQLQuery q = session.createSQLQuery("select distinct word from word w,  revision r " + 
				"where w.id_revision_created = r.id " + 
				"and w.id_revision_deleted is null " + 
				"and r.id_document = :documentId");
		
		q.setLong("documentId", document.getId());
		q.addScalar("word", Hibernate.STRING);
		
		List<String> result = q.list();
			
		return result;
	}
	
	/**
	 * Returns the author with the given name or null if no such author existst.
	 * @param name
	 * @return
	 */
	public Author getAuthor(String name) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Author.class).add(Restrictions.eq("name", name));
		Author result = (Author)criteria.uniqueResult();
		return result;
	}
	
	/**
	 * Returns the concept with the given URI or null if no such concept exists.
	 * @param uri
	 * @return
	 */
	public Concept getConcept(String uri) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Concept.class).add(Restrictions.eq("uri", uri));
		Concept result = (Concept)criteria.uniqueResult();
		return result;
	}
	
	/**
	 * Returns the section of the given document with the given title.
	 * @param document the document.
	 * @param title the section's title.
	 * @return the section of the given document with the given title, or null if no such section exists.
	 */
	public Section getSection(Document document, String title) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from Section s, Revision r where s.revisionCreated = r and r.document = :document and s.tite = :title").
			setEntity("document", document).
			setEntity("title", title);
		
		Section result = (Section)query.uniqueResult();
		return result;
	}
	
	/**
	 * Retrieves all not deleted sections for the given document.
	 * @param document the document
	 * @return all not deleted sections for the given document.
	 */
	@SuppressWarnings("unchecked")
	public List<Section> getSections(Document document) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from Section s, Revision r where s.revisionCreated = r and s.revisionDeleted = null and r.document = :document").
		setEntity("document", document);
		
		List<Section> result = (List<Section>)query.list();
		return result;
	}
	
	/**
	 * Returns the category with the given name or null if no such category exists.
	 * @param name a category name
	 * @return the category with the given name or null if no such category exists.
	 */
	public Category getCategory(String name) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Category.class).add(Restrictions.eq("name", name));
		Category result = (Category)criteria.uniqueResult();
		return result;
	}

	/**
	 * Retrieves the concepts that are most similar to the given concept. The
	 * number of returned concepts can be limited by the limit parameter.
	 * 
	 * @param concept
	 *            the concept for which the most similar concepts are seeked.
	 * @param limit
	 *            an integer determining the maximum number of concepts
	 *            returned.
	 * @return a list containing the most similar concepts to the given concept,
	 *         along with the respective similarity values, limited by the limit
	 *         parameter.
	 */
	@SuppressWarnings("unchecked")
	public List<ConceptSimilarity> getBestConceptSimilarities(Concept concept, int limit) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from ConceptSimilarity cs where cs.concept1 = :concept or cs.concept2 = :concept order by similarity desc").
		setEntity("concept", concept).
		setMaxResults(limit);
		
		List<ConceptSimilarity> result = (List<ConceptSimilarity>)query.list();
		return result;
	}

	/**
	 * Retrieves all concept similarites from the persistent store.
	 * @return a list containing all concept similarites.
	 */
	@SuppressWarnings("unchecked")
	public List<ConceptSimilarity> getAllConceptSimilarities() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from ConceptSimilarity");
		List<ConceptSimilarity> result = (List<ConceptSimilarity>)query.list();
		return result;
	}

	/**
	 * Returns the concepts that are most similar to the given concept, i.e.
	 * that have the (same) highest similarity value. In contrast to
	 * {@link #getBestConceptSimilarities(Concept, int)}, which returns the n
	 * best matches, this matches looks for the highest similarity value for the
	 * given concept and returns only the concept with this similarity value.
	 * 
	 * @param concept a concept.
	 * @return The concept or concepts with the highest similarity to the given concept.
	 */
	@SuppressWarnings("unchecked")
	public List<Concept> getMostSimilarConcepts(Concept concept) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(
				"from ConceptSimilarity cs " +
				"where (cs.concept1 = :concept or cs.concept2 = :concept) " +
				"and similarity = (select max(similarity) from ConceptSimilarity where concept1 = :concept or concept2 = :concept)").
		setEntity("concept", concept);
		
		List<ConceptSimilarity> queryResult = (List<ConceptSimilarity>)query.list();
		
		ArrayList<Concept> result = new ArrayList<Concept>();
		for (ConceptSimilarity cs : queryResult) {
			Concept concept1 = cs.getConcept1();
			Concept concept2 = cs.getConcept2();
			result.add(concept.equals(concept1) ? concept2 : concept1);
		}
		return result;
	}
	
	/**
	 * All concepts with a minimal similarity
	 * @param concept
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Pair<Concept, Double>> getMostSimilarConcepts(Concept concept, double minSimilarity) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(
				"from ConceptSimilarity cs " +
				"where (cs.concept1 = :concept or cs.concept2 = :concept) " +
				"and similarity > :minSimilarity").
		setEntity("concept", concept).setDouble("minSimilarity", minSimilarity);
		
		List<ConceptSimilarity> queryResult = (List<ConceptSimilarity>)query.list();
		
		ArrayList<Pair<Concept, Double>> result = new ArrayList<Pair<Concept, Double>>();
		for (ConceptSimilarity cs : queryResult) {
			Concept concept1 = cs.getConcept1();
			Concept concept2 = cs.getConcept2();
			result.add(concept.equals(concept1) ? new Pair<Concept, Double>(concept2, cs.getSimilarity()) : new Pair<Concept, Double>(concept1, cs.getSimilarity()));
		}
		return result;
	}
	
	/**
	 * Retrieves the concept similarity value for the given concepts.
	 * @param concept1 a concept.
	 * @param concept2 a second concept.
	 * @return the concept similarity value for the given concepts.
	 */
	public Double getConceptSimilarity(Concept concept1, Concept concept2) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("select cs.similarity from ConceptSimilarity cs where cs.concept1 = :concept1 and cs.concept2 = :concept2 or cs.concept1 = :concept2 and cs.concept2 = :concept1").
		setEntity("concept1", concept1).
		setEntity("concept2", concept2);
		return (Double)query.uniqueResult();
	}
	
	/**
	 * Convenience method. Retrieves the concept similarity value for the
	 * concepts with the given URIs.
	 * 
	 * @see #getConceptSimilarity(Concept, Concept).
	 * @param conceptURI1
	 *            URI of the first concept.
	 * @param conceptURI2
	 *            URI of the second concept.
	 * @return the concept similarity value for the concepts with the given
	 *         URIs.
	 */
	public Double getConceptSimilarity(String conceptURI1, String conceptURI2) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("select cs.similarity from ConceptSimilarity cs " +
				"where cs.concept1.uri = :conceptURI1 and cs.concept2.uri = :conceptURI2 " +
				"or cs.concept1.uri = :conceptURI2 and cs.concept2.uri = :conceptURI1").
		setString("conceptURI1", conceptURI1).
		setString("conceptURI2", conceptURI2);
		return (Double)query.uniqueResult();
	}
	
	/**
	 * 
	 * @param concept
	 * @param author
	 * @return
	 */
	public List<Object[]> getContributionForConcept(Concept concept, Author author) {
		return getContributionForConcept(concept.getId(), author.getId());
	}
	
	/**
	 * 
	 * @param concept
	 * @param author
	 * @return
	 */
	public List<Object[]> getContributionForConcept(long conceptId, long authorId) {
		Session session = sessionFactory.getCurrentSession();
		SQLQuery query = session.createSQLQuery("select  d.id as documentId, rc.count as rcCount, rd.count as rdCount, rd.id_author as deletor " + 
				"from word w " + 
				"join  revision rc " + 
				"on w.id_revision_created = rc.id " + 
				"and rc.id_author = :authorId " + 
				"join document d " + 
				"on rc.id_document = d.id  " + 
				"left outer join revision rd " + 
				"on w.id_revision_deleted = rd.id " + 
				"where w.id_concept = :conceptId");
		query.setLong("authorId", authorId);
		query.setLong("conceptId", conceptId);
		query.addScalar("documentId", Hibernate.LONG);
		query.addScalar("rcCount", Hibernate.LONG);
		query.addScalar("rdCount", Hibernate.LONG);
		query.addScalar("deletor", Hibernate.LONG);
		
		return query.list();
	}
	
	/**
	 * Retrieves all topics the given author has contributed to.
	 * 
	 * @param author
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Concept> getContributedTopics(Author author) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("select distinct c from Concept c, Word w, Revision rc " + 
				"where w.concept = c " +
				"and w.revisionCreated = rc " + 
				"and rc.author = :author " 
				);
		
		query.setEntity("author", author);
		return query.list();
	}

	/**
	 * Retrieves all topics the given author has contributed to.
	 * 
	 * @param author
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Concept> getIndirectContributedTopics(Author author) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("select distinct c " + 
				"from Concept c, Word w, Revision rc, Section s, SectionConcept sc " + 
				"where rc.author = :author " + 
				"and w.revisionCreated = rc " + 
				"and sc.section = w.section " + 
				"and sc.concept = c ");
		
		query.setEntity("author", author);
		return query.list();
	}
	
	/**
	 * Retrieves all authors that have directly contributed to the given
	 * topic.
	 * 
	 * @param topic
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Author> getAuthorsWhoContributedToTopic(Concept topic) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("select distinct a from Author a, Word w, Revision rc " + 
				"where w.concept = :topic " +
				"and w.revisionCreated = rc " + 
				"and rc.author = a " 
				);
		
		query.setEntity("topic", topic);
		return query.list();
	}
	
	/**
	 * Gets cached author contributions by author.
	 * @param author
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AuthorContribution> getCachedAuthorContributions(Author author) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from AuthorContribution ac where ac.author = :author"); 
		query.setEntity("author", author);
		return query.list();
	}
	
	/**
	 * Gets cached author contributions by topic.
	 * @param topic
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AuthorContribution> getCachedAuthorContributions(Concept topic) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from AuthorContribution ac where ac.concept = :topic"); 
		query.setEntity("topic", topic);
		return query.list();
	}
	
	/**
	 * Retrieves all authors that have indirectly contributed to the given
	 * topic.
	 * 
	 * @param topic
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Author> getAuthorsWhoIndirectlyContributedToTopic(Concept topic) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("select distinct a " + 
				"from Author a, Word w, Revision rc, Section s, SectionConcept sc " + 
				"where sc.concept = :topic " + 
				"and w.concept is not null " + 
				"and w.revisionCreated = rc " + 
				"and sc.section = w.section " + 
				"and rc.author = a");
		
		query.setEntity("topic", topic);
		return query.list();
	}

	/**
	 * Gets all contributions by a given author to a given concept by section
	 * (or document, because the top level section spans and has the same title
	 * as the document itself).<br/>
	 * For performance reasons, this method does not return an object graph but
	 * a list of arrays of ids and other numbers (see returns section).
	 * 
	 * @return A list of Object[] arrays. The items in each object array are:<br/>
	 *         0: the document id (Long)<br/>
	 *         1: the section id (Long)<br/>
	 *         2: the section level (Integer)<br/>
	 *         3: the id of the revision where the contribution was added (Long)<br/>
	 *         4: the id of the revision where the contribution was deleted or
	 *            null if the contribution has not been deleted (Long)<br/>
	 *         5: the id of the author who has deleted the contribution or null
	 *            if the contribution has not been deleted (Long)<br/>
	 *         6: a similarity value between 0.0 and 1.0 if a section could not
	 *            be mapped to any concept but one of its parent sections or the
	 *            document itself (Double). If one or more concepts could be found
	 *            for this section, this value is null.<br/>
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getContributionsToSectionsWithConceptForAuthor(Concept concept, Author author) {
		Session session = sessionFactory.getCurrentSession();
		
		SQLQuery query = session.createSQLQuery("select d.id as documentId, s.id as sectionId, s.level as sectionLevel, revCreated.count as revisionCreated, revDeleted.count as revisionDeleted, a.id as deletor, sc.similarity as similarity " + 
				"from word w " + 
				
				"join revision revCreated " + 
				"	on w.id_revision_created = revCreated.id " + 
				"	and revCreated.id_author = :authorId " + 
				"left outer join revision revDeleted " + 
				"	on w.id_revision_deleted = revDeleted.id " +
				"left outer join author a " + 
				"	on revDeleted.id_author = a.id " + 
				"join section s " + 
				"	on w.id_section = s.id " + 
				"join section_has_concept sc " + 
				"	on sc.id_section = s.id " + 
				"	and sc.id_concept = :conceptId " + 
				"join document d " + 
				"	on d.id = revCreated.id_document " +
				"where w.id_concept is null " +
				"group by word, sectionId, revisionCreated, revisionDeleted " +
				"order by documentId, sectionId, revisionCreated, revisionDeleted");
		
		query.addScalar("documentId", Hibernate.LONG).addScalar("sectionId", Hibernate.LONG).addScalar("sectionLevel", Hibernate.INTEGER).addScalar(
				"revisionCreated", Hibernate.LONG).addScalar("revisionDeleted", Hibernate.LONG).addScalar("deletor", Hibernate.LONG).addScalar("similarity",
				Hibernate.DOUBLE);
		
		query.setLong("conceptId", concept.getId());
		query.setLong("authorId", author.getId());
		
		return (List<Object[]>)query.list();
	}

	/**
	 * Retrieves ALL different words that are currently in the entire wiki (not
	 * deleted), along with the number of their occurences.
	 * 
	 * @return A list of Object[] arrays. The first object in each array is an
	 *         Integer representing the number of occurrences of the word
	 *         (grouped by their lemmas), the second object is a String
	 *         containing the word's lemma itself.
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllNonDeletedWords() {
		Session session = sessionFactory.getCurrentSession();
		SQLQuery query = session.createSQLQuery("select count(*) as wordcount, word from word " + 
				"where id_revision_deleted is null " + 
				"group by word");
		query.addScalar("wordcount", Hibernate.INTEGER).addScalar("word", Hibernate.STRING);
		
		return (List<Object[]>)query.list();
	}
	
	/**
	 * Retrieves an arbitary entity from the persistence layer.
	 * @param id The unique identifier for the entity.
	 * @param type the entity's class.
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public PersistableEntity getEntity(Serializable id, Class type) {
		Session session = sessionFactory.getCurrentSession();
		return (PersistableEntity)session.get(type, id);
	}
	
	/**
	 * Deletes all entries from the concept similarity cache.
	 */
	public void clearConceptSimilarityCache() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("delete ConceptSimilarity");
		int count = query.executeUpdate();
		log.info("Deleted " + count + " entries from the concept similarity cache.");
	}
	
	/**
	 * Retrieves the value for the given key from the application data table.
	 * 
	 * @param key
	 *            the key.
	 * @return the value for the given key from the application data table or
	 *         null if such value exists.
	 */
	public String getApplicationData(String key) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ApplicationData.class).
		add(Restrictions.eq("key", key)).setProjection(Projections.property("value"));
		
		return (String)criteria.uniqueResult();
	}
	
	public AuthorCredibility getAuthorCredibility(Author author, Concept concept) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from AuthorCredibility ac where ac.author = :author and ac.concept = :concept");
		query.setEntity("author", author);
		query.setEntity("concept", concept);
		return (AuthorCredibility)query.uniqueResult();
	}
	
	/**
	 * Stores a the given key-value pair in the application data table.
	 * @param key
	 * @param value
	 */
	public void saveApplicationData(String key, String value) {
		Session session = sessionFactory.getCurrentSession();
		ApplicationData entity = (ApplicationData)session.get(ApplicationData.class, key);
		if (entity == null) {
			entity = new ApplicationData(key, value);
		}
		session.save(entity);
	}
	
	/**
	 * Returns an SQLQuery object, initialized with the given query String.
	 * @param queryString
	 * @return an SQLQuery object, initialized with the given query String.
	 */
	public SQLQuery createSQLQuery(String queryString) {
		Session session = sessionFactory.getCurrentSession();
		return session.createSQLQuery(queryString);
	}
	
	/**
	 * Returns a Query object for HQL queries, initialized with the given query String.
	 * @param queryString
	 * @return an Query object, initialized with the given query String.
	 */
	public Query createHQLQuery(String queryString) {
		Session session = sessionFactory.getCurrentSession();
		return session.createQuery(queryString);
	}
	
	/**
	 * Commits changes to the persistence layer before committing the entire
	 * transaction.
	 */
	public void flush() {
		sessionFactory.getCurrentSession().flush();
	}
	
	/**
	 * Clears the cache to avoid OOMs.
	 */
	public void clearCache() {
		sessionFactory.getCurrentSession().clear();
	}
	
	
	
	/**
	 * Close the hibernate session, releasing the database conncection.
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		sessionFactory.getCurrentSession().close();
	}
	
}
