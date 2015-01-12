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

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import de.csw.expertfinder.config.Config;
import de.csw.expertfinder.ontology.OntologyIndex;

public class SimilarArticleFinder {

	private static Logger log = Logger.getLogger(SimilarArticleFinder.class);
	
	private static SimilarArticleFinder instance;

	private SimilarArticleFinder() {

	}

	public static SimilarArticleFinder getInstance() {
		if (instance == null) {
			instance = new SimilarArticleFinder();
		}
		return instance;
	}

	/**
	 * Finds related articles to a given article name in the MediaWiki. Takes a
	 * two-fold approach:
	 * <ol>
	 * <li>Looks up related concepts in the underlying ontology</li>
	 * <li>Finds related articles by following Category and See also links in
	 * the coresponding MediaWiki article</li>
	 * </ol>
	 * 
	 * @param articleName
	 *            The article name
	 * @return a set (i.e. no duplicate entries) of names of related articles,
	 *         including the original article name itself.
	 */
	public Set<String> findRelatatedArticles(String articleName) {
		Config.read(SimilarArticleFinder.class.getResourceAsStream("/conf/ExpertFinder.properties"));
		HashSet<String> result = new HashSet<String>();
		result.add(articleName);

		// ask ontology:
		result.addAll(OntologyIndex.get().getSimilarMatchLabels(articleName));

		// ask MediaWiki
		try {
			result.addAll(MediaWikiAPI.getInstance().getRelatedArticleNames(articleName));
		} catch (MediaWikiAPIException e) {
			log.error("Could not get related MediaWiki articles for article " + articleName, e);
		}
		
		return result;
	}
}
