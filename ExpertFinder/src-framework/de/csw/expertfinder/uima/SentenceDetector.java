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
package de.csw.expertfinder.uima;

import java.text.BreakIterator;
import java.util.Locale;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import de.csw.expertfinder.uima.types.Sentence;

/**
 * This simple analysis engine detects sentences using the Java built-in
 * BreakIterator 
 * @author ralph
 *
 */
public class SentenceDetector extends JCasAnnotator_ImplBase {
	
	private static final BreakIterator breakIterator = BreakIterator.getSentenceInstance(Locale.GERMAN);

	/**
	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		breakIterator.setText(cas.getDocumentText());
	     int start = breakIterator.first();
	     for (int end = breakIterator.next(); end != BreakIterator.DONE; start = end, end = breakIterator.next()) {
	    	 Sentence annotation = new Sentence(cas);
	    	 annotation.setBegin(start);
	    	 annotation.setEnd(end);
	    	 annotation.addToIndexes();
	     }

	}

	

}
