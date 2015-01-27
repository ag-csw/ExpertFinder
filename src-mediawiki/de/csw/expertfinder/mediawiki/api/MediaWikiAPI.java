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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;
import org.apache.tools.ant.filters.StringInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.csw.expertfinder.config.Config;


/**
 * Methods of this class provide access to the MediaWiki HTTP API.
 * 
 * 
 * @author ralph
 */
public class MediaWikiAPI {

	private static final Logger log = Logger.getLogger(MediaWikiAPI.class);

	private static MediaWikiAPI instance;

	private static final DefaultHttpClient http = new DefaultHttpClient();
	
	static {
		http.setHttpRequestRetryHandler(new HttpRequestRetryHandler() {
			@Override
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				// retry 2 times
				return executionCount < 3;
			}
		});
	}
	
	private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	
	private static final XPathExpression XPATH_LANGLINKS;
	private static final XPathExpression XPATH_CATEGORIES;
	
	private String apiURL;
	private String ajaxURL;
	
	static {
		XPathExpression tmp = null;
		try {
			tmp = XPathFactory.newInstance().newXPath().compile("/api/query/pages/page/langlinks/ll[@lang=\"en\"]");
		} catch (XPathExpressionException e) {
			log.error("Could not initialize xpath expression for language links. Some functionality of this class is not available.");
		} finally {
			XPATH_LANGLINKS = tmp;
		}
		try {
			tmp = XPathFactory.newInstance().newXPath().compile("/categories/div/div/a/text()");
		} catch (XPathExpressionException e) {
			log.error("Could not initialize xpath expression for language links. Some functionality of this class is not available.");
		} finally {
			XPATH_CATEGORIES = tmp;
		}
	}

	/**
	 * Returns the single MediaWikiAPI instance for the MediaWiki in the given
	 * language.
	 * 
	 * @param language
	 * @return
	 * @throws MediaWikiAPIException
	 */
	public static MediaWikiAPI getInstance() throws MediaWikiAPIException {
		if (instance == null) {
			instance = new MediaWikiAPI();
		}
		return instance;
	}


	private DocumentBuilder documentBuilder;

	/**
	 * Constructs a new MediaWikiAPI.
	 */
	private MediaWikiAPI() throws MediaWikiAPIException {
//		baseURL = Config.getAppProperty("wiki.baseurl");
		apiURL = Config.getAppProperty(Config.Key.WIKI_MEDIAWIKI_API);
		ajaxURL = Config.getAppProperty(Config.Key.WIKI_MEDIAWIKI_AJAX);
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new MediaWikiAPIException("Could not obtain XML parser", e);
		}

	}

//	public Language getLanguage() {
//		return language;
//	};

	/**
	 * Gets articles related to the given article.
	 * 
	 * @param articleName
	 * @return
	 * @throws MediaWikiAPIException
	 */
	public Set<String> getRelatedArticleNames(String articleName) throws MediaWikiAPIException {
		TreeSet<String> result = new TreeSet<String>();

		result.addAll(getInternalLinks(articleName));

		Set<String> categories = getCategories(articleName);
		for (String category : categories) {
			result.addAll(getArticlesForCategory(category));
		}

		return result;
	}

	/**
	 * Returns the article name for an article ID or null if no such article
	 * exists.
	 * 
	 * @param id
	 *            an article id.
	 * @return the article name for the given article ID or null if no such
	 *         article exists.
	 * @throws MediaWikiAPIException
	 */
	public String getArticleNameForId(int id) throws MediaWikiAPIException {
		Iterator<String> result = queryListResult(
				new BasicNameValuePair[] { new BasicNameValuePair("prop", "info"), new BasicNameValuePair("pageids", "" + id) }, "page", "title").iterator();
		if (result.hasNext()) {
			return result.next();
		}
		return null;
	}

	/**
	 * Returns a set of article namen for a set of article IDs. If none of the
	 * given ids match an exisiting article, an empty Set is returned.
	 * 
	 * @param id
	 *            an article id.
	 * @return a Set of article names for the given article IDs or an empty Set
	 *         if no such article exists.
	 * @throws MediaWikiAPIException
	 */
	public Set<String> getArticleNamesForIds(Set<Integer> ids) throws MediaWikiAPIException {

		TreeSet<String> result = new TreeSet<String>();
		// The limit of the mediawiki API for info requests is 50.
		Iterator<Integer> iter = ids.iterator();
		while (iter.hasNext()) {
			HashSet<Integer> partOfIds = new HashSet<Integer>();
			for (int i = 0; i < 50 && iter.hasNext(); i++) {
				partOfIds.add(iter.next());
			}
			result.addAll(getArticleNamesFor50Ids(partOfIds));
		}

		return result;
	}

	/**
	 * Performs the actual retrieval of the article names. The mediawiki API
	 * limits this kind of requests to 50 article IDs.
	 * 
	 * @param ids
	 * @return
	 * @throws MediaWikiAPIException
	 */
	private Set<String> getArticleNamesFor50Ids(Set<Integer> ids) throws MediaWikiAPIException {
		StringBuilder buf = new StringBuilder();
		for (Integer id : ids) {
			buf.append(id);
			buf.append('|');
		}
		String idsStr = buf.substring(0, buf.length() - 1);
		return queryListResult(new BasicNameValuePair[] { new BasicNameValuePair("prop", "info"), new BasicNameValuePair("pageids", "" + idsStr) }, "page",
				"title");
	}

	/**
	 * Retrieves the MediaWiki Categories the given article belongs to.
	 * 
	 * @param articleName
	 *            article name
	 * @return a list of all Category names the given article belongs to
	 * @throws MediaWikiAPIException
	 */
	public Set<String> getCategories(String articleName) throws MediaWikiAPIException {

		return queryListResult(new BasicNameValuePair[] { new BasicNameValuePair("prop", "categories"), new BasicNameValuePair("titles", articleName),
				new BasicNameValuePair("cllimit", "100"), new BasicNameValuePair("clshow", "!hidden") }, "cl", "title");
	}

	public Set<String> getArticlesForCategory(String categoryName) throws MediaWikiAPIException {

		return queryListResult(new BasicNameValuePair[] { new BasicNameValuePair("list", "categorymembers"), new BasicNameValuePair("cmtitle", categoryName),
				new BasicNameValuePair("cmlimit", "500") }, "cm", "title");
	}

	public Set<String> getArticlesBeginningWith(String prefix) throws MediaWikiAPIException {

		return openSearch(prefix);
	}

	/**
	 * Retrieves a list of MediaWiki articles that are linked to from within the
	 * given article.
	 * 
	 * @param articleName
	 *            article name
	 * @return a list containing all articles which are linked to from within
	 *         the given article.
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public Set<String> getInternalLinks(String articleName) throws MediaWikiAPIException {

		return queryListResult(new BasicNameValuePair[] { new BasicNameValuePair("prop", "links"), new BasicNameValuePair("titles", articleName),
				new BasicNameValuePair("pllimit", "500") }, "pl", "title");
	}

	public String getArticleID(String articleName) throws MediaWikiAPIException {

		Set<String> articleIDs = queryListResult(new BasicNameValuePair[] { new BasicNameValuePair("prop", "info"),
				new BasicNameValuePair("titles", articleName) }, "page", "pageid");

		if (articleIDs.isEmpty())
			return null;

		return articleIDs.iterator().next();
	}

	/**
	 * Performs a list query to the MediaWiki API
	 * 
	 * @param params parameters (see <http://<your.mediawiki.host>/w/api.php).
	 * @param elementName the name of the element of the resulting xml document that contains the desired information.
	 * @param attributeName the name of the attribute of the elements specified by elementName that contains the desired information. 
	 * @return a set of strings containing the contents of the attributes specified by attributeName in the elements specified by elementName
	 * @throws MediaWikiAPIException if something goes wrong connecting to or talking with the MediaWiki api.
	 */
	private Set<String> queryListResult(BasicNameValuePair[] params, String elementName, String attributeName) throws MediaWikiAPIException {

		TreeSet<String> result = new TreeSet<String>();

		Document document = queryMediaWiki("query", params);

		NodeList plElements = document.getElementsByTagName(elementName);
		int length = plElements.getLength();
		for (int i = 0; i < length; i++) {
			Element plElement = (Element) plElements.item(i);
			String attributeValue = plElement.getAttribute(attributeName);
			if (attributeValue != null) {
				result.add(attributeValue);
			}
		}

		return result;
	}

	/**
	 * Performs a request to the wiki's OpenSearch API
	 * (http://<your.wiki.host>/w/api.php)
	 * 
	 * @param params
	 * @param elementName
	 * @param attributeName
	 * @return a list containing article names starting with the given prefix
	 * @throws MediaWikiAPIException
	 */
	private Set<String> openSearch(String prefix) throws MediaWikiAPIException {

		TreeSet<String> result = new TreeSet<String>();

		Document document = queryMediaWiki("opensearch", new BasicNameValuePair[] { new BasicNameValuePair("search", prefix),
				new BasicNameValuePair("limit", "10") });

		NodeList plElements = document.getElementsByTagName("Text");
		int length = plElements.getLength();
		for (int i = 0; i < length; i++) {
			Element plElement = (Element) plElements.item(i);
			String articleName = plElement.getTextContent().trim();
			result.add(articleName);
		}

		return result;
	}

	/**
	 * Returns an iterator iterating over all versions of the article specified
	 * by the given id, in ascending chronological order (version 0 first,
	 * latest version last).
	 * 
	 * Returns article revisions WITHOUT the actual text content.
	 * 
	 * @see MediaWikiAPI#getAllVersionsOfArticle(int)
	 * 
	 * @param articleId
	 * @return iterator iterating over all versions of the given article
	 */
	public MediaWikiArticleIterator getInfoForAllVersionsOfArticle(final int articleId, int startRevId) {
		return getAllVersionsOfArticleInternal(articleId, startRevId, false);
	}

	/**
	 * Returns an iterator iterating over all versions of the article specified
	 * by the given id, in ascending chronological order (version 0 first,
	 * latest version last).
	 * 
	 * Returns article revisions including their content.
	 * 
	 * @see MediaWikiAPI#getInfoForAllVersionsOfArticle(int)
	 * 
	 * @param articleId
	 * @return iterator iterating over all versions of the given article
	 */
	public MediaWikiArticleIterator getAllVersionsOfArticle(final int articleId, int startRevId) {
		return getAllVersionsOfArticleInternal(articleId, startRevId, true);
	}

	private MediaWikiArticleIterator getAllVersionsOfArticleInternal(final int articleId, int startRevId, boolean fetchContent) {

		final ArrayList<Integer> revIds = new ArrayList<Integer>();

		Iterator<Element> revisions = getInfoForAllRevisions(articleId, startRevId, fetchContent);
		Element currentRevision = null;
		if (revisions.hasNext()) {
			currentRevision = revisions.next();
		}

		Element nextRevision = null;
		while (revisions.hasNext()) {
			nextRevision = revisions.next();
			// look ahead if we have a sequence of edits by the same author
			// we only need to pick the last edit of this sequence.
			String nextAuthor = nextRevision.getAttribute("user");
			String currentAuthor = currentRevision.getAttribute("user");
			if (currentAuthor.equals(nextAuthor)) {
//				continue;
			}
			String revId = currentRevision.getAttribute("revid");
			revIds.add(Integer.parseInt(revId));
			currentRevision = nextRevision;
		}
		
		if (nextRevision != null) {
			String revId = nextRevision.getAttribute("revid");
			revIds.add(Integer.parseInt(revId));
		}

		final Iterator<Integer> revIdIterator = revIds.iterator();
		final int revisionCount = revIds.size();

		return new MediaWikiArticleIterator(revIdIterator, articleId, this, revisionCount, fetchContent);
	}

	private Iterator<Element> getInfoForAllRevisions(final int articleId, final int startRevId, boolean fetchContent) {
		return new Iterator<Element>() {

			private int current = 0;
			private int rvstartid = startRevId;
			private NodeList revisions;
			private boolean queryContinue = true;

			public void remove() {
				throw new UnsupportedOperationException();
			}

			public Element next() {
				if (hasNext())
					return (Element) revisions.item(current++);
				throw new NoSuchElementException();
			}

			public boolean hasNext() {

				if (revisions == null || // first call
						queryContinue && current == revisions.getLength()) { // current
																				// list
																				// exhausted,
																				// but
																				// there's
																				// more
																				// to
																				// fetch
					fetchNext();
				}

				if (!queryContinue && current == revisions.getLength()) // current
																		// list
																		// exhausted
																		// and
																		// nothing
																		// more
																		// to
																		// fetch:
																		// finished!
					return false;

				return true;
			}

			private void fetchNext() {
				BasicNameValuePair[] params = new BasicNameValuePair[] { new BasicNameValuePair("prop", "revisions"),
						new BasicNameValuePair("pageids", "" + articleId), new BasicNameValuePair("rvstartid", "" + rvstartid),
						new BasicNameValuePair("rvdir", "newer"), new BasicNameValuePair("rvlimit", "500"), new BasicNameValuePair("rvprop", "ids|user"),
						new BasicNameValuePair("redirects", "") };
				try {
					Document document = queryMediaWiki("query", params);
					revisions = document.getElementsByTagName("rev");
					current = 0;
					NodeList queryContinues = document.getElementsByTagName("query-continue");
					if (queryContinues.getLength() == 0) {
						queryContinue = false;
					} else {
						Element queryContinueElement = (Element) queryContinues.item(0);
						NodeList nextRevisionsElements = queryContinueElement.getElementsByTagName("revisions");
						Element nextRevisions = (Element) nextRevisionsElements.item(0);
						String rvstartidStr = nextRevisions.getAttribute("rvstartid");
						if (rvstartidStr == null || rvstartidStr.isEmpty()) {
							rvstartidStr = nextRevisions.getAttribute("rvcontinue"); 
						}
						rvstartid = Integer.parseInt(rvstartidStr);
					}
				} catch (MediaWikiAPIException e) {
					log.error("Request to MediaWiki API failed", e);
				}
			}
		};
	}

	/**
	 * Returns the translation of the given article name in the language
	 * specified. (The source language is, of course, the one with which this
	 * instance has been initialized).
	 * 
	 * @param articleName the article name in this {@link MediaWikiAPI}'s language.
	 * @param language the destination language (ISO code).
	 * @return The article name translated to the destination language, if available. Otherwise returns null. 
	 * @throws MediaWikiAPIException if connection to the MediaWiki endpoint fails.
	 */
	public String getArticleNameInLanguage(String articleName, String language) throws MediaWikiAPIException {
		if (XPATH_LANGLINKS == null) {
			throw new MediaWikiAPIException("XPath expression for getting language element could not be initialized earlier (see earlier in the logs).");
		}
		
		BasicNameValuePair[] params = new BasicNameValuePair[] { new BasicNameValuePair("prop", "langlinks"), new BasicNameValuePair("titles", articleName),
				new BasicNameValuePair("lllimit", "500"), new BasicNameValuePair("redirects", "") };
		
		Document doc = queryMediaWiki("query", params);
		
		try {
			String result = XPATH_LANGLINKS.evaluate(doc);
			// XPathExpression.evaluate returns empty String if nothing matches. But we want null. 
			return "".equals(result) ? null : result;
		} catch (XPathExpressionException e) {
			throw new MediaWikiAPIException("");
		}
	}

	/**
	 * Returns all names of articles that redirect to the article with the given
	 * name. If the given article name belongs to a redirect itself, then it is
	 * first resolved.
	 * 
	 * @param articleName
	 * @return A list containing all names for the given article. The first
	 *         element of the list is the actual name of the article, all others
	 *         are redirects.
	 * @throws MediaWikiAPIException 
	 */
	public List<String> getSynonyms(String articleName) throws MediaWikiAPIException {
		List<String> result = new ArrayList<String>();
		
		// First determine the actual page name (in case this is a redirect)
		String actualArticleName = getActualArticleName(articleName);
		result.add(actualArticleName);
		
		// get backlinks, filter by those that are redirects.
		BasicNameValuePair[] params = new BasicNameValuePair[] { new BasicNameValuePair("prop", "links"), new BasicNameValuePair("list", "backlinks"),
				new BasicNameValuePair("bltitle", actualArticleName), new BasicNameValuePair("blfilterredir", "redirects"),
				new BasicNameValuePair("bllimit", "500") };
		
		Document doc = queryMediaWiki("query", params);
		NodeList redirects = doc.getElementsByTagName("bl");
		int size = redirects.getLength();
		for (int i=0; i<size; i++) {
			Element redirect = (Element)redirects.item(i);
			result.add(redirect.getAttribute("title"));
		}
		
		return result;

	}
	
	/**
	 * Returns the actual article name for the given one (many pages in the MediaWiki are merely references
	 * to the actual page with a different, often synonym name). 
	 * @param articleName
	 * @return The actual article name or the given article name itself, if it does not reference a redirect. Returns null if no article with the given name exists.
	 * @throws MediaWikiAPIException 
	 */
	public String getActualArticleName(String articleName) throws MediaWikiAPIException {
		BasicNameValuePair[] params = new BasicNameValuePair[] { new BasicNameValuePair("titles", articleName), new BasicNameValuePair("redirects", "") };
		Document doc = queryMediaWiki("query", params);
		
		try {
			NodeList pages = doc.getElementsByTagName("page");
			if (pages.getLength() == 0)
				return null;
			return ((Element)pages.item(0)).getAttribute("title");
		} catch (Exception e) {
			throw new MediaWikiAPIException("Could not retrieve result from MediaWiki api xml", e);
		}
	}
	
	public List<String> getAllArticleNamesInWiki() throws MediaWikiAPIException {
		ArrayList<String> result = new ArrayList<String>();

		Document doc = queryMediaWiki("query", new BasicNameValuePair[] {
				new BasicNameValuePair("list", "allpages"),
				new BasicNameValuePair("aplimit", "500")
		});
		
		extractPageNames(doc);
//		result.addAll(extractPageNames(doc));

		String apFrom = null;
		for(;;) {
			NodeList queryContinueNodes = doc.getElementsByTagName("query-continue");
			int length = queryContinueNodes.getLength();
			if (length == 0)
				break;
			
			apFrom = ((Element)queryContinueNodes.item(0).getFirstChild()).getAttribute("apfrom");
		
			doc = queryMediaWiki("query", new BasicNameValuePair[] {
					new BasicNameValuePair("list", "allpages"),
					new BasicNameValuePair("aplimit", "500"),
					new BasicNameValuePair("apfrom", apFrom)
			});
			
			result.addAll(extractPageNames(doc));
		}
		
		return result;
	}
	
	/**
	 * Queries the original wiki for redirects and saves them to a csv file.
	 * Problem: MediaWiki can only return ids for the original pages, but
	 * in the local MediaWiki db, page ids are different.
	 * Thus, it is necessary to call {@link #repairRedirects()} afterwards which will
	 * sort out the problem.
	 * @throws Exception
	 */
	private void getAllRedirects() throws Exception {
		BufferedWriter out = new BufferedWriter(new FileWriter("D:\\Evaluierung\\AllRedirects.txt"));

		Document doc = queryMediaWiki("query", new BasicNameValuePair[] {
				new BasicNameValuePair("list", "allpages"),
				new BasicNameValuePair("apfilterredir", "redirects"),
				new BasicNameValuePair("aplimit", "500")
		});
		
		String apFrom = extractRedirectsAndReturnContinue(out, doc);
		while (apFrom != null) {
			doc = queryMediaWiki("query", new BasicNameValuePair[] {
					new BasicNameValuePair("list", "allpages"),
					new BasicNameValuePair("apfilterredir", "redirects"),
					new BasicNameValuePair("aplimit", "500"),
					new BasicNameValuePair("apfrom", apFrom)
			});
			apFrom = extractRedirectsAndReturnContinue(out, doc);
		}
		
		out.close();
	}
	
	private String extractRedirectsAndReturnContinue(BufferedWriter out, Document doc) throws IOException {
		NodeList pElements = doc.getElementsByTagName("p");
		for(int i=0; i<pElements.getLength(); i++) {
			Element pElement = (Element)pElements.item(i);
			String pageId = pElement.getAttribute("pageid");
			String ns = pElement.getAttribute("ns");
			String pageTitle = pElement.getAttribute("title");
			
			out.write(pageId);
			out.write(";");
			out.write(ns);
			out.write(";");
			out.write(pageTitle);
			out.write("\n");
		}
		out.flush();
		
		NodeList queryContinueList = doc.getElementsByTagName("query-continue");
		if (queryContinueList.getLength() == 0) {
			// no more items
			return null;
		}
		
		Element allpageElement = (Element)((Element)(queryContinueList.item(0))).getElementsByTagName("allpages").item(0);
		return allpageElement.getAttribute("apfrom");
	}
	
	/**
	 * Reads the redirect data gathered with {@link #getAllRedirects()} and retrieves all missing data
	 * necessary to restore the redirects in the local MediaWiki db. In particular, it retrieves the
	 * titles of the redirect (pseudo) pages, because {@link #getAllRedirects()} only gives us page ids
	 * which do not correspsond to the page ids in our local db.
	 * @throws Exception 
	 */
	private void repairRedirects() throws Exception {
		List<String> lines = FileUtils.readLines(new File("D:\\Evaluierung\\AllRedirects.txt"));
		BufferedWriter out = new BufferedWriter(new FileWriter("D:\\Evaluierung\\AllRedirectsWithPageNames.txt", true));

		for (String line : lines) {
			StringTokenizer tok = new StringTokenizer(line, ";");
			Integer pageId = Integer.parseInt(tok.nextToken());
			String ns = tok.nextToken();
			String pageTitle = tok.nextToken();
			
			Document doc = queryMediaWiki("query", new BasicNameValuePair[] {
					new BasicNameValuePair("prop", "info"),
					new BasicNameValuePair("titles", pageTitle),
					new BasicNameValuePair("redirects", "")
			});

			Element redirectElement = (Element)doc.getElementsByTagName("r").item(0);
			if (redirectElement == null)
				continue;
			
			String targetPageTitle = redirectElement.getAttribute("to");
			
			out.write(pageTitle);
			out.write(";");
			out.write(ns);
			out.write(";");
			out.write(targetPageTitle);
			out.write("\n");

		}
		out.flush();
		out.close();
	}
	
	private List<String> extractPageNames(Document doc) {
		ArrayList<String> result = new ArrayList<String>();
		
		NodeList pages = doc.getElementsByTagName("p");
		int length = pages.getLength();
		for(int i=0; i<length; i++) {
			result.add(((Element)pages.item(i)).getAttribute("title"));
		}
		
		return result;
	}

	/**
	 * Returns all contributions for the given user.
	 * @param userName
	 * @return
	 * @throws MediaWikiAPIException
	 */
	public List<MediaWikiArticleContribution> getAllContributionsForUser(String userName) throws MediaWikiAPIException {
		BasicNameValuePair[] params = new BasicNameValuePair[] { new BasicNameValuePair("list", "usercontribs"), new BasicNameValuePair("ucnamespace", "0"),
				new BasicNameValuePair("ucuser", userName), new BasicNameValuePair("ucshow", "!minor"), new BasicNameValuePair("uclimit", "500") };

		HashMap<Integer, MediaWikiArticleContribution> contributionsByArticleId = new HashMap<Integer, MediaWikiArticleContribution>();
		for (;;) {
			Document doc = queryMediaWiki("query", params);
			NodeList itemElements = doc.getElementsByTagName("item");
			int len = itemElements.getLength();
			for (int i = 0; i < len; i++) {
				Element itemElement = (Element) itemElements.item(i);
				int articleId = Integer.parseInt(itemElement.getAttribute("pageid"));
				String title = itemElement.getAttribute("title");
				MediaWikiArticleContribution contribution = contributionsByArticleId.get(articleId);
				if (contribution == null) {
					contribution = new MediaWikiArticleContribution(articleId, title, userName);
					contributionsByArticleId.put(articleId, contribution);
				}
				contribution.increaseContributionCount();
			}
			
			NodeList queryContinueElements = doc.getElementsByTagName("query-continue");
			if (queryContinueElements.getLength() == 0) {
				ArrayList<MediaWikiArticleContribution> result = new ArrayList<MediaWikiArticleContribution>(contributionsByArticleId.size());
				result.addAll(contributionsByArticleId.values());
				Collections.sort(result, new Comparator<MediaWikiArticleContribution>() {
					public int compare(MediaWikiArticleContribution o1, MediaWikiArticleContribution o2) {
						// we want the result to be sorted in descending order, thus we swap o1 and o2 here.
						return o2.getContributionCount().compareTo(o1.getContributionCount());
					}
				});
				return result;
			}

			Element queryContinueElement = (Element) queryContinueElements.item(0);
			Element userContribsElement = (Element) queryContinueElement.getElementsByTagName("usercontribs").item(0);
			String ucstart = userContribsElement.getAttribute("ucstart");

			params = new BasicNameValuePair[] { new BasicNameValuePair("list", "usercontribs"), new BasicNameValuePair("ucnamespace", "0"),
					new BasicNameValuePair("ucuser", userName), new BasicNameValuePair("ucshow", "!minor"), new BasicNameValuePair("uclimit", "500"),
					new BasicNameValuePair("ucstart", ucstart) };
		}
	}

	/**
	 * Performs a request to the http mediawiki api. returns the xml response
	 * encapsulated in a {@link Document} object. 
	 * 
	 * @param action the action
	 * @param params
	 * @return
	 * @throws MediaWikiAPIException
	 */
	Document queryMediaWiki(String action, BasicNameValuePair[] params) throws MediaWikiAPIException {

		StringBuilder url = new StringBuilder(apiURL);

		url.append("?action=");
		url.append(action);
		url.append("&format=xml");

		try {
			for (BasicNameValuePair param : params) {
				url.append('&');
				url.append(URLEncoder.encode(param.getName(), "UTF-8"));
				url.append('=');
				url.append(URLEncoder.encode(param.getValue(), "UTF-8"));
			}
		} catch (UnsupportedEncodingException e1) {
			throw new MediaWikiAPIException("The UTF-8 character encoding is not supported on this platform.", e1);
		}
		
		return httpRequest(url.toString());
	}
		
	private Document httpRequest(String url) throws MediaWikiAPIException {
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response;
		try {
			response = http.execute(httpGet);
		} catch (ClientProtocolException e) {
			throw new MediaWikiAPIException("Could not execute HTTP call to MediaWiki API", e);
		} catch (IOException e) {
			throw new MediaWikiAPIException("Could not execute HTTP call to MediaWiki API", e);
		}

		InputStream content;
		try {
			content = response.getEntity().getContent();
		} catch (IllegalStateException e) {
			throw new MediaWikiAPIException("Could not process response from call to MediaWiki API", e);
		} catch (IOException e) {
			throw new MediaWikiAPIException("Could not process response from call to MediaWiki API", e);
		}

		try {
			return documentBuilder.parse(content);
		} catch (SAXException e) {
			throw new MediaWikiAPIException("Error parsing response from MediaWiki API", e);
		} catch (IOException e) {
			throw new MediaWikiAPIException("Error parsing response from MediaWiki API", e);
		}

	}
	
	/**
	 * Returns all child categories of the given category.
	 * Works only for MediaWiki installations with the CategoryTree
	 * extension installed (most of the public MediaWikis should have it
	 * installed).
	 * @param category
	 * @return
	 */
	public List<String> getChildCategories(String categoryName) throws MediaWikiAPIException {
		if (XPATH_CATEGORIES == null) {
			// should never happen
			throw new MediaWikiAPIException("XPath expression for getting categories not initialized");
		}
		//http://wiki.eclipse.org/index.php?action=ajax
		
		StringBuilder buf = new StringBuilder(ajaxURL);
		buf.append("&rs=efCategoryTreeAjaxWrapper&rsargs[]=");
		try {
			buf.append(URLEncoder.encode(categoryName.replace(' ', '_'), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new MediaWikiAPIException("The UTF-8 character encoding is not supported on this platform.", e);
		}
		buf.append("&rsargs[]=0"); // mode = 0 (no parent categories, no pages). 
		
		HttpGet httpGet = new HttpGet(buf.toString());
		HttpResponse response;
		try {
			response = http.execute(httpGet);
		} catch (ClientProtocolException e) {
			throw new MediaWikiAPIException("Could not execute HTTP call to MediaWiki API", e);
		} catch (IOException e) {
			throw new MediaWikiAPIException("Could not execute HTTP call to MediaWiki API", e);
		}

		InputStream content;
		try {
			content = response.getEntity().getContent();
		} catch (IllegalStateException e) {
			throw new MediaWikiAPIException("Could not process response from call to MediaWiki API", e);
		} catch (IOException e) {
			throw new MediaWikiAPIException("Could not process response from call to MediaWiki API", e);
		}

		StringWriter out;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(content));
			out = new StringWriter();
			String line;
			while((line = in.readLine()) != null) {
				out.write(line + "\n");
			}
		} catch (IOException e) {
			throw new MediaWikiAPIException("Could not read response", e);
		}
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<categories>\n" + out.toString() + "\n</categories>";
		
		Document doc;
		
		try {
			doc = documentBuilder.parse(new StringInputStream(xml));
		} catch (SAXException e) {
			throw new MediaWikiAPIException("Error parsing xml document", e);
		} catch (IOException e) {
			throw new MediaWikiAPIException("Errror reading xml document", e);
		}
		
		NodeList childCategoryNodes;
		try {
			childCategoryNodes = (NodeList)XPATH_CATEGORIES.evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new MediaWikiAPIException("Could not evaluate XPath expression " + XPATH_CATEGORIES + " on result of category name query for category " + categoryName);
		}
		
		ArrayList<String> childCategories = new ArrayList<String>();

		int length = childCategoryNodes.getLength();
		for (int i = 0; i < length; i++) {
			Node childCategoryNode = childCategoryNodes.item(i);
			String childCategoryName = childCategoryNode.getTextContent().trim();
			childCategories.add(childCategoryName);
		}
		
		return childCategories;
	}


	public static void eraseRedirects(MediaWikiAPI w) {
		try {
			BufferedReader in = new BufferedReader(new FileReader("Z:\\csw\\Dipomarbeit\\Evaluierung\\Alle Artikel.txt"));
			FileWriter out = new FileWriter("Z:\\csw\\Dipomarbeit\\Evaluierung\\Alle Artikel ohne Redirects.txt");
			
			String articleName;
			while ((articleName = in.readLine()) != null) {
				String actualName = w.getActualArticleName(articleName);
				if (articleName.equals(actualName)) {
					out.write(articleName + "\n");
				} 
//				else {
//					System.out.println("Omitting: " + articleName + " -> " + actualName);
//				}
			}
			out.flush();
			out.close();
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MediaWikiAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
