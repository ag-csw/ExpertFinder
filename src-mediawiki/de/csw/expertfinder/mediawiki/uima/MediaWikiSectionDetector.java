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

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;

import de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo;
import de.csw.expertfinder.mediawiki.uima.types.markup.Section;

/**
 * This UIMA annotator class detects sections in MediaWiki pages.
 * It has been implemented because the parser used in
 * {@link MediaWikiMarkupDetector} does not handle this correctly.
 * 
 * @author ralph
 *
 */
public class MediaWikiSectionDetector extends JCasAnnotator_ImplBase {

	private final static Pattern pattern = Pattern.compile("^(={2,6})([^=]*)\\1$", Pattern.MULTILINE);
	
	/**
	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		String text = cas.getDocumentText();
		
 		AnnotationIndex articleRevisionInfoIndex = cas.getAnnotationIndex(ArticleRevisionInfo.type);
 		ArticleRevisionInfo articleRevisionInfo = (ArticleRevisionInfo)articleRevisionInfoIndex.iterator().next(); // there is only one

 		String documentTitle = articleRevisionInfo.getTitle();
		
		// set up a stack for tracking parent sections
		Stack<Section> stack = new Stack<Section>();
		
		// The whole page level 1. This is not explit in mediawiki,
		// but we make it explicit here.
		
		Section mainSection = new Section(cas);
		mainSection.setLevel(1);
		mainSection.setBegin(0);
		mainSection.setTitle(documentTitle);
		stack.push(mainSection);
		mainSection.addToIndexes();

		Matcher matcher = pattern.matcher(text);
		
		// the matcher finds all lines starting and ending with the same
		// number of '=' characters. Group 1 contains the '=' characters
		// (their number indicates the heading level), and group 2 contains
		// the heading's title.
		while (matcher.find()) {
			int level = matcher.end(1) - matcher.start(1);
			String title = text.substring(matcher.start(2), matcher.end(2));
			int start = matcher.start();
			
			Section section = new Section(cas);
			section.setTitle(title.trim());
			section.setLevel(level);
			section.setBegin(matcher.start());
			section.addToIndexes();
			
			// if our level is smaller or equal than the one on the stack,
			// then we terminate the previous section and open a
			// new higher level one.
			while (level <= stack.peek().getLevel()) {
				Section terminated = stack.pop();
				terminated.setEnd(start-1);
			}
			
			// the section with the next larger level than ours is
			// our parent
			section.setParent(stack.peek());
			stack.push(section);
		}

		// set end position to all section remaining on stack (= the end of the entire page)
		int endOfPage = text.length();

		while (!stack.isEmpty()) {
			stack.pop().setEnd(endOfPage);
		}

	}
	
}
