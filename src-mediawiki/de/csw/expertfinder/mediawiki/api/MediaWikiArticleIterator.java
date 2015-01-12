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
package de.csw.expertfinder.mediawiki.api;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This Iterator iterates over all versions of a given article, retrieved from
 * a MediaWiki instances.
 * This implementation is thread safe.
 * @author ralph
 *
 */
public class MediaWikiArticleIterator implements Iterator<MediaWikiArticleVersion> {
	
	private static final int CHUNK_SIZE = 50;
	private static final String CHUNK_SIZE_STR = "" + CHUNK_SIZE;
	
	private static final Logger log = Logger.getLogger(MediaWikiArticleIterator.class);
	
	private static final SimpleDateFormat timestampDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	private Iterator<Integer> revIdIterator;
	private int articleId;
	private MediaWikiAPI mediaWiki;
	private int revisionCount;
	private boolean fetchContent;
	
	private Queue<MediaWikiArticleVersion> queue = new LinkedList<MediaWikiArticleVersion>();
	private int startRevId;
	
	/**
	 * Constructs a new {@link MediaWikiArticleIterator}
	 * 
	 * @param revIdIterator an Iterator returning the ids of the revisions that should be returned by this {@link MediaWikiArticleIterator}
	 * @param articleId the id of the document/article/page whose revisions should be retrieved  
	 * @param mediaWiki an instance of the MediaWiki API
	 * @param revisionCount the number of revision ids the revIdItertator contains
	 * @param fetchContent a boolean indicating whether the full text of each revision should be retrieved or just information about the revisions
	 */
	public MediaWikiArticleIterator(Iterator<Integer> revIdIterator, int articleId, MediaWikiAPI mediaWiki, int revisionCount, boolean fetchContent) {
		this.revIdIterator = revIdIterator;
		this.articleId = articleId;
		this.mediaWiki = mediaWiki;
		this.revisionCount = revisionCount;
		this.fetchContent = fetchContent;
	}

	/**
	 * Not implemented
	 * @see java.util.Iterator#remove()
	 * @throws UnsupportedOperationException
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the next {@link MediaWikiArticleVersion} from this Iterator.
	 * @see java.util.Iterator#next()
	 */
	public MediaWikiArticleVersion next() {
		synchronized (this) {
			// Internally, 50 (the maximum number) of versions are loaded in a chunk
			// and buffered for efficiency.
			if (!hasNext())
				throw new NoSuchElementException();
			
			if (queue.isEmpty()) {
				loadNextChunk();
			}
			
			return queue.poll();
		}

	}

	public boolean hasNext() {
		synchronized (this) {
			return !queue.isEmpty() || revIdIterator.hasNext();
		}
	}
	
	public int size() {
		return revisionCount;
	}
	
	private void loadNextChunk() {
		synchronized (this) {

			if (!revIdIterator.hasNext())
				return;
			
			startRevId = revIdIterator.next();
			int endRevId = startRevId;
			for (int i = 1; i < CHUNK_SIZE && revIdIterator.hasNext(); i++) {
				endRevId = revIdIterator.next();
			}
			BasicNameValuePair[] params = new BasicNameValuePair[] { new BasicNameValuePair("prop", "revisions"),
					new BasicNameValuePair("pageids", "" + articleId),
					new BasicNameValuePair("rvstartid", "" + startRevId),
					new BasicNameValuePair("rvendid", "" + endRevId),
					new BasicNameValuePair("rvlimit", CHUNK_SIZE_STR),
					new BasicNameValuePair("rvdir", "newer"),
					new BasicNameValuePair("rvprop", fetchContent ? "ids|flags|user|comment|content|timestamp" : "ids|flags|user|comment|timestamp"),
					new BasicNameValuePair("redirects", "")};

			try {
				Document document = mediaWiki.queryMediaWiki("query", params);
				
				Element pageElement = (Element)document.getElementsByTagName("page").item(0);
				
				NodeList revisions = document.getElementsByTagName("rev");
				int revCount = revisions.getLength();
				
				for (int j = 0; j < revCount; j++) {
					Element revision = (Element)revisions.item(j);
					MediaWikiArticleVersion result = new MediaWikiArticleVersion();
					result.setArticleId(articleId);
					result.setTitle(pageElement.getAttribute("title"));
					result.setRevisionId(Integer.parseInt(revision.getAttribute("revid")));
					result.setUser(revision.getAttribute("user"));
					result.setComment(revision.getAttribute("comment"));
					
					String timestamp = revision.getAttribute("timestamp");
					result.setTimestamp(timestampDF.parse(timestamp));
					
					if (fetchContent)
						result.setText(revision.getTextContent());
					
					queue.add(result);
				}
			} catch (Exception e) {
				log.error("Request to MediaWiki API failed", e);
				throw new RuntimeException(e);
			}
		}
	}

}
