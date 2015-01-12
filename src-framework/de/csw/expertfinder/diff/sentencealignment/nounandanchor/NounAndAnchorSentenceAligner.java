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
package de.csw.expertfinder.diff.sentencealignment.nounandanchor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.csw.expertfinder.diff.DiffingSentences;
import de.csw.expertfinder.diff.sentencealignment.BestMatchingSentence;
import de.csw.expertfinder.diff.sentencealignment.BestMatchingSentences;
import de.csw.expertfinder.diff.sentencealignment.SentenceAlignmentAlgorithm;
import de.csw.expertfinder.document.Noun;
import de.csw.expertfinder.document.Sentence;
import de.csw.expertfinder.document.Word;

/**
 * @author ralph
 * 
 */
public class NounAndAnchorSentenceAligner implements SentenceAlignmentAlgorithm {

	private class Interval {
		int start, end;
	}
	
	/**
	 * @see de.csw.expertfinder.diff.sentencealignment.SentenceAlignmentAlgorithm#getBestMatchingSentences(de.csw.expertfinder.diff.DiffingSentences)
	 */
	public BestMatchingSentences getBestMatchingSentences(DiffingSentences diffingSentences) {
		BestMatchingSentences bestFittingSentences = new BestMatchingSentences();
		List<Sentence> newSentences = diffingSentences.getNewSentences();
		List<Sentence> deletedSentences = diffingSentences.getDeletedSentences();
		for (Sentence newSentence : newSentences) {
			List<Word> addedWords = newSentence.getWords();
			for (Sentence oldSentence : deletedSentences) {
				List<Word> deletedWords = oldSentence.getWords();
				// int distance = Levenshtein.lev(deletedWords, addedWords);
				double distance = 1 - sim(oldSentence, newSentence);
				bestFittingSentences.add(oldSentence, newSentence, distance);
			}
		}
		return bestFittingSentences;

	}


	/**
	 * @see de.csw.expertfinder.diff.sentencealignment.SentenceAlignmentAlgorithm#getBestMatchingSentences(de.csw.expertfinder.diff.DiffingSentences)
	 */
	public List<BestMatchingSentence> getBestMatchingSentencesNonsense(DiffingSentences diffingSentences) {
		ArrayList<BestMatchingSentence> result = new ArrayList<BestMatchingSentence>();

		List<Sentence> oldUnchangedSentences = new ArrayList<Sentence>(diffingSentences.getMatchingOldSentences());
		Collections.sort(oldUnchangedSentences, Sentence.POSITION);

		List<Sentence> newUnchangedSentences = new ArrayList<Sentence>(diffingSentences.getMatchingNewSentences());
		Collections.sort(newUnchangedSentences, Sentence.POSITION);

		HashMap<Noun, List<Sentence>> deletedSentencesByNoun = sortSentencesByNouns(diffingSentences.getDeletedSentences());
		HashMap<Noun, List<Sentence>> newSentencesByNoun = sortSentencesByNouns(diffingSentences.getNewSentences());

		Set<Noun> deletedNouns = deletedSentencesByNoun.keySet();
		Set<Noun> newNouns = newSentencesByNoun.keySet();

		for (Noun noun : deletedNouns) {
			List<Sentence> newSentencesWithNoun = newSentencesByNoun.get(noun);

		}

		return result;
	}

	private HashMap<Noun, List<Sentence>> sortSentencesByNouns(List<Sentence> sentences) {
		HashMap<Noun, List<Sentence>> map = new HashMap<Noun, List<Sentence>>();
		for (Sentence sentence : sentences) {
			List<Noun> nouns = sentence.getNouns();
			for (Noun noun : nouns) {
				List<Sentence> sentencesContainingNoun = map.get(noun);
				if (sentencesContainingNoun == null) {
					sentencesContainingNoun = new ArrayList<Sentence>();
					map.put(noun, sentencesContainingNoun);
				}
				sentencesContainingNoun.add(sentence);
			}
		}
		return map;
	}

	/**
	 * Uses a modified Jaccard metric to calculate the difference
	 * between two sentences
	 * @param s1
	 * @param s2
	 * @return
	 */
	private double sim(Sentence s1, Sentence s2) {
		HashSet<String> words1 = makeSet(s1.getWords());
		HashSet<String> words2 = makeSet(s2.getWords());
		double minLen = Math.min(words1.size(), words2.size());
		HashSet<String> intersection = new HashSet<String>();
		intersection.addAll(words1);
		intersection.retainAll(words2);
		return intersection.size() / minLen;
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
