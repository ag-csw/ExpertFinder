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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;

import de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo;
import de.csw.expertfinder.mediawiki.uima.types.markup.ExternalLink;
import de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink;

/**
 * This class implements a JCas annotator engine that detects relevant MediaWiki
 * markup.<br/>
 * It has basically been implemented because the only MediaWiki parser that
 * fullfilled this project's functional requirements, used in the deprecated
 * annotator engine {@link MediaWikiMarkupDetector} had errors and could not
 * parse certain pages.<br/>
 * As a side effect, this simple regex based markup detector runs much faster
 * than the aforementioned annotator.
 * 
 * @author ralph
 */
public class FastMediaWikiMarkupDetector extends JCasAnnotator_ImplBase {
	
	private static final String PREFIX_CATEGORY = "Category:";
	private static final String PREFIX_IMAGE = "Image:";
	
	private static final int PREFIX_CATEGORY_LENGTH = PREFIX_CATEGORY.length();
	private static final int PREFIX_IMAGE_LENGTH = PREFIX_IMAGE.length();

	private static final Pattern internalLinkPattern = Pattern.compile("(?<!<nowiki>)\\[\\[(.*?)\\]\\]");
	private static final Pattern externalLinkPattern = Pattern.compile("(?<!<nowiki>)(?<!\\[)\\[(?!\\[)(.*?)(?<!\\[)\\](?!\\[)");
	
	private static final String TARGET_TITLE_DELIMITER = "|";
	private static final String PAGE_SECTION_DELIMITER = "#";
	
	/**
	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		
		String text = cas.getDocumentText();
		
		if ("".equals(text)) {
			// Oops, empty text, probably vandalism. Nothing to do.
			return;
		}
		
		ArrayList<String> categoryNames = new ArrayList<String>();
		
		// detect internal links
		Matcher matcher = internalLinkPattern.matcher(text);
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			String linkText = matcher.group(1);
			if (linkText.startsWith(PREFIX_CATEGORY)) {
				categoryNames.add(linkText.substring(PREFIX_CATEGORY_LENGTH));
			} else if (linkText.startsWith(PREFIX_IMAGE)) {
				InternalLink link = new InternalLink(cas);
				link.setBegin(start);
				link.setEnd(end);
				link.setFile(linkText.substring(PREFIX_IMAGE_LENGTH));
				link.addToIndexes();
			} else {
				InternalLink link = new InternalLink(cas);
				link.setBegin(start);
				link.setEnd(end);
				
				String target;
				
				// links are in the format [[target]] or [[target | title]]
				int targetTitleDelimiterPos = linkText.indexOf(TARGET_TITLE_DELIMITER);

				if (targetTitleDelimiterPos == -1) {
					target = linkText;
				} else {
					target = linkText.substring(0, targetTitleDelimiterPos).trim();
					link.setTitle(linkText.substring(targetTitleDelimiterPos + 1).trim());
				}
				
				String pageName;
				
				// link target is in the format page or page#section
				int pageSectionDelimiterPos = target.indexOf(PAGE_SECTION_DELIMITER);

				if (pageSectionDelimiterPos == -1) {
					pageName = target;
				} else {
					pageName = target.substring(0, pageSectionDelimiterPos).trim();
					link.setSection(target.substring(pageSectionDelimiterPos + 1).trim());
				}
				
				link.setArticle(pageName);
				link.addToIndexes();

			}
		}
		
		// externalLinks
		matcher = externalLinkPattern.matcher(text);
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			
			ExternalLink link = new ExternalLink(cas);
			link.setBegin(start);
			link.setEnd(end);
			link.addToIndexes();
		}
		
		// add categories to revision annotation index
 		AnnotationIndex articleRevisionInfoIndex = cas.getAnnotationIndex(ArticleRevisionInfo.type);
 		ArticleRevisionInfo articleRevisionInfo = (ArticleRevisionInfo)articleRevisionInfoIndex.iterator().next(); // there is only one
 		
 		int categoryCount = categoryNames.size();
		StringArray categoryArray = new StringArray(cas, categoryCount);
 		
 		for (int i=0; i<categoryCount; i++) {
 			categoryArray.set(i, categoryNames.get(i));
		}

		articleRevisionInfo.setCategories(categoryArray);
		
	}

}
