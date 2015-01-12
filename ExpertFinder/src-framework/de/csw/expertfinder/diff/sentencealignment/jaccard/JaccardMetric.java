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
package de.csw.expertfinder.diff.sentencealignment.jaccard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import de.csw.expertfinder.document.Sentence;
import de.csw.expertfinder.document.Word;

/**
 * Calculates the Jaccard coefficient for two sentences.
 * The Jaccard coefficient is defined by the formula
 * jaccard(s1, s2) = |s1 intersection s2| / |s1 union s2|,
 * Where s1 and s2 are sentences which in turn are sets of words.
 *   
 * @author ralph
 */
public class JaccardMetric {
	
	/**
	 * Calculates the jaccard coefficient for the two given sentences.
	 * @param s1 the first sentence
	 * @param s2 the second sentence
	 * @return the Jaccard coefficient for the given sentence pair.
	 */
	public static double calculateJaccardCoefficient(Sentence s1, Sentence s2) {
		
		HashSet<String> words1 = makeSet(s1.getWords());
		HashSet<String> words2 = makeSet(s2.getWords());
		
		// equality of words is determined by equality of their texts
		HashSet<String> union = new HashSet<String>(words1);
		union.addAll(words2);
		
		HashSet<String> intersection = new HashSet<String>(words1);
		intersection.retainAll(words2);
		
		return 1d - ((double)intersection.size() / (double)union.size());
	}

	
	public List<Sentence> makeSentences(String text) {
		text = text.toLowerCase();
		ArrayList<Sentence> result = new ArrayList<Sentence>();
		StringTokenizer sentences = new StringTokenizer(text, ".");
		while(sentences.hasMoreTokens()) {
			Sentence s = new Sentence();
			result.add(s);
			String sentence = sentences.nextToken().trim();
			StringTokenizer words = new StringTokenizer(sentence, " ");
			while(words.hasMoreTokens()) {
				Word word = new Word(words.nextToken(), null, 0, 0);
				s.addWord(word);
			}
		}
		return result;
	}
	
	/**
	 * This makes a set out of a list of words, so that union, intersection
	 * and difference operations can be performed on them based on text
	 * equality instead of Word object equality. The problem is that if a
	 * sentence conains the same word more than once, we want to have it
	 * included in the set.
	 * @param words
	 * @return
	 */
	private static HashSet<String> makeSet(List<Word> words) {
		HashSet<String> result = new HashSet<String>();
		for (Word word : words) {
			String text = word.getWord();
			while(result.contains(text)) {
				text += "*";
			}
			result.add(text);
		}
		return result;
	}

}
