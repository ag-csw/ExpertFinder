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

import java.io.IOException;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import de.csw.expertfinder.document.Document;
import de.csw.expertfinder.document.Revision;
import de.csw.expertfinder.mediawiki.api.MediaWikiAPI;
import de.csw.expertfinder.mediawiki.api.MediaWikiAPIException;
import de.csw.expertfinder.mediawiki.api.MediaWikiArticleIterator;
import de.csw.expertfinder.mediawiki.api.MediaWikiArticleVersion;
import de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo;
import de.csw.expertfinder.persistence.PersistenceStoreFacade;

/**
 * This class implements a UIMA CollectionReader which reads collections of
 * MediaWiki documents. In this use case, a MediaWiki document is not an entire
 * article but a portion that has been added (or removed) in a single editing
 * step, i.e. the diff between two subsequent article versions.
 * 
 * @author ralph
 */
public class MediaWikiArticleCollectionReader extends CollectionReader_ImplBase {

	private static final String CONFIG_PARAM_DOCUMENT_TITLE = "documentTitle";
	private static final String CONFIG_PARAM_DOCUMENT_ID = "documentID";

	private static final Logger log = Logger
			.getLogger(MediaWikiArticleCollectionReader.class);

	private MediaWikiArticleIterator versions;
	
	// in order to detect (and skip) reverts, we use this buffer
	// for looking ahead.
	private LinkedList<MediaWikiArticleVersion> versionQueue; 
	
//	private MediaWikiArticleVersion previousVersion;
	
	private ProgressImpl progress;
	private Progress[] progressArray;

	/**
	 * @see org.apache.uima.collection.CollectionReader_ImplBase#initialize()
	 */
	@Override
	public void initialize() throws ResourceInitializationException {
		PersistenceStoreFacade persistentStore = PersistenceStoreFacade.get();

		super.initialize();
		Integer articleId = (Integer)getUimaContext().getConfigParameterValue(CONFIG_PARAM_DOCUMENT_ID);
		String articleName = (String)getUimaContext().getConfigParameterValue(CONFIG_PARAM_DOCUMENT_TITLE);
		
		try {
			MediaWikiAPI mediaWiki = MediaWikiAPI.getInstance();
			
			if (articleId == null && articleName == null) {
					throw new ResourceInitializationException("One of the parameters has to be specified", null);
			}
			if (articleName == null) {
				articleName = mediaWiki.getArticleNameForId(articleId);
			} else {
				String articleIdString = mediaWiki.getArticleID(articleName);
				if (articleIdString == null || articleIdString.equals("")) {
					// sometimes articles with the given name do not exist, even when the mediawiki export interface says so.
					log.warn("Page " + articleName + " is not in this wiki.");
					versionQueue = new LinkedList<MediaWikiArticleVersion>();
					progress = new ProgressImpl(0, 0, Progress.ENTITIES);
					progressArray = new Progress[] {progress};
					return;
				}
				articleId = Integer.parseInt(articleIdString);
			}

			persistentStore.beginTransaction();

			// Check if we have seen this article before and this is just an update.
			Document document = persistentStore.getDocument((long)articleId);
			
			long lastPersistedRevisionId;
			
			if (document == null) {
				// this is the first time we see this article. Create a new Document in the persistent store.
				lastPersistedRevisionId = 0;
				document = new Document((long)articleId, articleName);
				persistentStore.save(document);
			} else {
				// we have seen this article before. We only need to start with the last revision.
				Revision lastPersistedRevision = PersistenceStoreFacade.get().getLatestPersistedRevision((long)articleId);
				lastPersistedRevisionId = lastPersistedRevision == null ? 0 : lastPersistedRevision.getId();
			}
			
			
			versions = mediaWiki.getAllVersionsOfArticle(articleId, (int)lastPersistedRevisionId);
			progress = new ProgressImpl(0, versions.size(), Progress.ENTITIES);
			progressArray = new Progress[] { progress };
			
			persistentStore.commitChanges();
			
			log.info("Processing wiki article '" + articleName + "'...");
		} catch (MediaWikiAPIException e) {
			persistentStore.rollbackChanges();
			log.error("Error querying MediaWiki using the MediaWiki API", e);
			throw new ResourceInitializationException(e);
		}
		
		// prefill buffer
		versionQueue = new LinkedList<MediaWikiArticleVersion>();
		for(int i=0; i<3 && versions.hasNext(); i++) {
			versionQueue.add(versions.next());
		}
		
	}

	/**
	 * @see org.apache.uima.collection.CollectionReader#getNext(org.apache.uima.cas.CAS)
	 */
	public void getNext(CAS baseCas) throws IOException, CollectionException {
		
		MediaWikiArticleVersion currentVersion = versionQueue.peekFirst();
		
		if (versionQueue.size() == 3) {
			MediaWikiArticleVersion versionAfterNext = versionQueue.peekLast();
			
			if(versionAfterNext.getText().equals(currentVersion.getText())) {
				// Detect reverts (possibly due to vandalism): 
				// We look at the current version A. We also take a peek into the future at
				// the two subsequent versions B and C. We see
				// that Version C will have the same content as Version A. This means
				// that the author of version C will revert B's changes back to
				// A. Thus, A is the only version we have to take into
				// consideration. B and C can be ignored. (B is the "vandal" and
				// C does nothing but revert to version A. Neither of both contributes
				// anything new).
				log.debug("Skipping revision " + versionQueue.get(1).getRevisionId() + " Comment: " + versionQueue.get(1).getComment());
				versionQueue.removeLast();
				versionQueue.removeLast();
				if(versions.hasNext())
					versionQueue.add(versions.next());
				if(versions.hasNext())
					versionQueue.add(versions.next());
				
				progress.increment(2);
			}
		}
		
//		CAS cas = baseCas.createView("currentVersion");
		
		JCas jCas;
		try {
//			jCas = cas.getJCas();
			jCas = baseCas.getJCas();
		} catch (CASException e) {
			log.error("Error creating JCas from CAS", e);
			throw new CollectionException("Error creating JCas from CAS", null, e);
		}
		
		jCas.setDocumentText(currentVersion.getText());
		ArticleRevisionInfo articleRevisionInfo = new ArticleRevisionInfo(jCas);
		articleRevisionInfo.setArticleId(currentVersion.getArticleId());
		articleRevisionInfo.setTitle(currentVersion.getTitle());
		articleRevisionInfo.setRevisionId(currentVersion.getRevisionId());
		articleRevisionInfo.setAuthorName(currentVersion.getUser());
		articleRevisionInfo.setTimestamp(currentVersion.getTimestamp().getTime());
		articleRevisionInfo.addToIndexes(jCas);
		
//		CAS previousVersionCAS = baseCas.createView("previousVersion");
//		JCas previousVersionJCas;
//		try {
//			previousVersionJCas = previousVersionCAS.getJCas();
//		} catch (CASException e) {
//			log.error("Error creating JCas from CAS", e);
//			throw new CollectionException("Error creating JCas from CAS", null, e);
//		}
//		previousVersionJCas.setDocumentText(previousVersion == null ? "" : previousVersion.getText());
//		
//		previousVersion = currentVersion;
		
		versionQueue.pop();
		if (versions.hasNext())
			versionQueue.add(versions.next());
		
		log.debug("Created cas for revision " + currentVersion.getRevisionId() + "(" + currentVersion.getUser() + ")");
		
		progress.increment(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#close()
	 */
	public void close() throws IOException {
		System.gc();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#hasNext()
	 */
	public boolean hasNext() throws IOException, CollectionException {
		return !versionQueue.isEmpty();
	}

	/**
	 * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#getProgress()
	 */
	public Progress[] getProgress() {
		return progressArray;
	}
	
}
