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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import de.csw.expertfinder.mediawiki.uima.types.markup.Paragraph;

/**
 * This analyses engine detects and annotates text paragraphs
 * separated by at least two subsequent line-break characters.
 * If a paragraph starts with a  
 * @author ralph
 *
 */
public class ParagraphDetector extends JCasAnnotator_ImplBase {
	
	private static final Logger log = Logger.getLogger(ParagraphDetector.class);

	private static final Pattern pattern = Pattern.compile("\\n\\n+");
	
	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		// add two newlines so that we definitely catch the last paragraph
		String text = cas.getDocumentText() + "\n\n";
		
		Matcher matcher = pattern.matcher(text);
		int lastNewLineSequenceEnd = 0;
		while(matcher.find()) {
			int currentNewLineSequenceStart = matcher.start();
			if (currentNewLineSequenceStart == 0) {
				// text begins with empty lines: skip them
				lastNewLineSequenceEnd = matcher.end();
				continue; 
			}
			
			int currentNewLineSequenceEnd = matcher.end();
			// the content of our paragraph consists of the characters
			// between the end of the last match and the beginning of 
			// the current match of our regex.
			
			for (int nextNewLine = text.indexOf('\n', lastNewLineSequenceEnd); nextNewLine < currentNewLineSequenceStart; nextNewLine = text.indexOf('\n', lastNewLineSequenceEnd)) {
				if (text.substring(lastNewLineSequenceEnd, nextNewLine).startsWith("=")) {
					lastNewLineSequenceEnd = nextNewLine + 1;
				} else {
					break;
				}
			}
			
			Paragraph annotation = new Paragraph(cas);
			annotation.setBegin(lastNewLineSequenceEnd);
			annotation.setEnd(currentNewLineSequenceStart);
			annotation.addToIndexes();

			lastNewLineSequenceEnd = currentNewLineSequenceEnd;
			
		}
	}
}
