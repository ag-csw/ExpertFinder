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
package de.csw.expertfinder.diff.sentencealignment.levenshtein;

import java.util.List;

import de.csw.expertfinder.diff.DiffingSentences;
import de.csw.expertfinder.diff.sentencealignment.BestMatchingSentences;
import de.csw.expertfinder.diff.sentencealignment.SentenceAlignmentAlgorithm;
import de.csw.expertfinder.document.Sentence;
import de.csw.expertfinder.document.Word;
import de.csw.expertfinder.tokenization.Levenshtein;

/**
 * @author ralph
 *
 */
public class LevenshteinSentenceAligner implements SentenceAlignmentAlgorithm {

	/**
	 * @see de.csw.expertfinder.diff.sentencealignment.SentenceAlignmentAlgorithm#getBestMatchingSentences(de.csw.expertfinder.diff.DiffingSentences)
	 */
	public BestMatchingSentences getBestMatchingSentences(DiffingSentences diffingSentences) {
		BestMatchingSentences bestFittingSentences = new BestMatchingSentences();
		List<Sentence> newSentences = diffingSentences.getNewSentences();
		for(Sentence newSentence : newSentences) {
			List<Word> addedWords = newSentence.getWords();
			List<Sentence> deletedSentences = diffingSentences.getDeletedSentences();
			for(Sentence oldSentence : deletedSentences) {
				List<Word> deletedWords = oldSentence.getWords();
				int distance = Levenshtein.lev(deletedWords, addedWords);
//				double distance = JaccardMetric.calculateJaccardCoefficient(oldSentence, newSentence);
				bestFittingSentences.add(oldSentence, newSentence, distance);
			}
		}
		return bestFittingSentences;
	}
}
