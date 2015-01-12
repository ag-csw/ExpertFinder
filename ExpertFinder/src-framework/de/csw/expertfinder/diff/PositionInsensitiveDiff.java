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
package de.csw.expertfinder.diff;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.incava.util.diff.Difference;

import de.csw.expertfinder.diff.sentencealignment.BestMatchingSentence;
import de.csw.expertfinder.diff.sentencealignment.BestMatchingSentences;
import de.csw.expertfinder.diff.sentencealignment.SentenceAlignmentAlgorithm;
import de.csw.expertfinder.diff.sentencealignment.nounandanchor.NounAndAnchorSentenceAligner;
import de.csw.expertfinder.document.Revision;
import de.csw.expertfinder.document.Sentence;
import de.csw.expertfinder.document.Word;

/**
 * This class implements a diff algorithm for comparing two versions of a
 * document.
 * 
 * In contrast to other well-known diff implementations, this algorithm is
 * insensitive to move operations, i.e. moving a sentence to a different
 * position in the document is not considered a change, as long as the sentence
 * is not changed otherwise.
 * 
 * Furthermore, this algorithm tries to find the best matching sentence pair.
 * That means that if during the transition from one version to the next a sentence is
 * slightly changed (only a few words), the algorithm will try to detect that the
 * two sentences are, although slightly different, actually the same. It will
 * then only return the words that have changed.
 * 
 * The algorithm works as follows:
 * 
 * First, it constructs a list of sentences from each of the two versions and
 * sorts these two lists alphabetically. Then it runs a diff based on the
 * longest common subsequence algorithm on the two lists, yielding only
 * sentences that have been removed or added (sentences from the old version of
 * the document that are no more present in the new version, and sentences that
 * are present in the new version but were not in the old version).
 * 
 * Then it calculates the edit (levenshtein) distance between each sentence from
 * the list of the removed sentences with each sentence from the list of the
 * added sentences. Then the sentence pairs with the lowest edit distances are
 * considered to be the same (but modified) sentence. The threshold is 0.5 times
 * the word count of the shorter sentence. I.e. if half of the edit distance
 * between sentence 1 and sentence 2 is below the length of the shorter of the
 * two sentences, they are considered the same modified sentence. If not, they
 * are considered completely different sentences.
 * 
 * Then, for the sentences considered the same, a word based diff is performed.
 * The sentences identified as completely new or completely removed, are
 * completely added to the list of added or removed words, respectively.
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * @author ralph
 * 
 */
public class PositionInsensitiveDiff {
	
	private static final Logger log = Logger.getLogger(PositionInsensitiveDiff.class);

	private final double distanceThreshold = 0.5d;

	private ArrayList<Word> deletedWords = new ArrayList<Word>();
	private ArrayList<Word> addedWords = new ArrayList<Word>();
	private Map<Word, Word> unchangedWords = new HashMap<Word, Word>();

	private SentenceAlignmentAlgorithm sentenceAlignmentAlgorithm = new NounAndAnchorSentenceAligner();

	/**
	 * Constructs a new PositionSensitiveDiff
	 * 
	 * @param oldRevision
	 * @param newRevision
	 */
	public PositionInsensitiveDiff(Revision oldRevision, Revision newRevision) {
		
		// first run a position insensitive diff, seperating matching sentence pairs,
		// and deleted and added sentences without an equivalent
		DiffingSentences diffingSentences = getDiffingSentences(oldRevision, newRevision);
		
		// put matching words in the unchangedWords set
		Set<Sentence> matchingOldSentences = diffingSentences.getMatchingOldSentences();
		for (Sentence oldSentence : matchingOldSentences) {
			Sentence newSentence = diffingSentences.getMatchingNewSentence(oldSentence);
			List<Word> oldWords = oldSentence.getWords();
			List<Word> newWords = newSentence.getWords();
			int oldSize = oldWords.size();
			int newSize = newWords.size();
			if (oldSize != newSize) {
				log.error("sentences '" + oldSentence + "' and '" + newSentence + "' have been deemed equal, but their words' sizes are different.");
			}
			for (int i=0; i<oldSize; i++) {
				unchangedWords.put(oldWords.get(i), newWords.get(i));
			}
		}

		// Try to find changed sentences, i.e. sentences that have no exact match in the old and
		// new revision but are somewhat similar
		// not necessary if no unmatched old or new sentences are left
		
		List<Sentence> addedSentences = diffingSentences.getNewSentences();
		List<Sentence> deletedSentences = diffingSentences.getDeletedSentences();

		if (deletedSentences.isEmpty()) {
			for (Sentence addedSentence : addedSentences) {
				addedWords.addAll(addedSentence.getWords());
			}
		} else if (addedSentences.isEmpty()) {
			for (Sentence deleSentence : deletedSentences) {
				deletedWords.addAll(deleSentence.getWords());
			}
		} else {
		
			BestMatchingSentences bestFittingSentences = getBestFittingSentences(diffingSentences);
			for (BestMatchingSentence bestFittingSentence : bestFittingSentences) {
				Sentence oldSentence = bestFittingSentence.getOldSentence();
				Sentence newSentence = bestFittingSentence.getNewSentence();
				double distance = bestFittingSentence.getDistance();
	
				// int lengthOfShorterSentence =
				// Math.min(oldSentence.getWords().size(),
				// newSentence.getWords().size());
				// if (2*distance < lengthOfShorterSentence) {
				if (distance < distanceThreshold) {
					// these sentences are probably related
					diffSentences(oldSentence, newSentence);
				} else {
					deletedWords.addAll(oldSentence.getWords());
					addedWords.addAll(newSentence.getWords());
				}
			}
			
			Set<Sentence> oldRest = bestFittingSentences.oldRest();
			for (Sentence deletedSentence : oldRest) {
				deletedWords.addAll(deletedSentence.getWords());
			}
			
			Set<Sentence> newRest = bestFittingSentences.newRest();
			for (Sentence addedSentence : newRest) {
				addedWords.addAll(addedSentence.getWords());
			}
		}		

	}

	/**
	 * Returns the words that have been deleted from version 1 to version 2.
	 * 
	 * @return the words that have been deleted from version 1 to version 2.
	 */
	public List<Word> getDeletedWords() {
		return deletedWords;
	}

	/**
	 * Returns the words that have been added from version 1 to version 2.
	 * 
	 * @return the words that have been added from version 1 to version 2.
	 */
	public List<Word> getAddedWords() {
		return addedWords;
	}
	
	/**
	 * Returns the words that have been not been changed from version 1 to version 2.
	 * @return
	 */
	public Map<Word, Word> getUnchangedWords() {
		return unchangedWords;
	}

	/**
	 * Compares two sentences that have been considered similar, i.e. they are
	 * probably the same sentences, but some words have been changed.
	 * Adds deleted, added and unchanged words to the appropriate lists.
	 * @param oldSentence
	 * @param newSentence
	 */
	private void diffSentences(Sentence oldSentence, Sentence newSentence) {
		LinkedList<Word> oldWords = new LinkedList<Word>(oldSentence.getWords());
		Collections.sort(oldWords, Word.TEXT);
		LinkedList<Word> newWords = new LinkedList<Word>(newSentence.getWords());
		Collections.sort(newWords, Word.TEXT);

		Word oldWord = oldWords.getFirst();
		Word newWord = newWords.getFirst();
		for (;;) {
			int comp = oldWord.getWord().compareTo(newWord.getWord());
			if (comp == 0) {
				unchangedWords.put(oldWords.removeFirst(), newWords.removeFirst());
			} else if (comp < 0) {
				deletedWords.add(oldWord);
				oldWords.removeFirst();
			} else {
				addedWords.add(newWord);
				newWords.removeFirst();
			}
			if (oldWords.isEmpty() && newWords.isEmpty()) {
				break;
			}
			if (oldWords.isEmpty()) {
				addedWords.addAll(newWords);
				break;
			}
			if (newWords.isEmpty()) {
				deletedWords.addAll(oldWords);
				break;
			}
			oldWord = oldWords.getFirst();
			newWord = newWords.getFirst();
		}

	}

	/**
	 * 
	 * @param diffingSentences
	 * @return
	 */
	private BestMatchingSentences getBestFittingSentences(DiffingSentences diffingSentences) {
		return sentenceAlignmentAlgorithm.getBestMatchingSentences(diffingSentences);
	}

	/**
	 * <p>
	 * Performs a diff on the given two texts and returns the result in the form
	 * of a DiffingSentences object.
	 * </p>
	 * 
	 * <p>
	 * The resulting DiffingSentences object contains only sentences which have
	 * changed. Sentences that have an equal counter part in the respecively
	 * other text are omitted.
	 * 
	 * TODO doc for this method is not up to date.
	 * 
	 * @param rev1
	 * @param rev2
	 * @return
	 */
	private DiffingSentences getDiffingSentences(Revision rev1, Revision rev2) {
		// in order to recognize rearrangement of text, we can get rid of
		// position information anyway.
		// so a least common subsequence approach is overkill. instead, we put
		// all sentences into
		// ordered sets, and just run a simpler (wrt runtime) least common
		// subset algorithm on them.

		// List<Sentence> text1Sentences = new
		// ArrayList<Sentence>(doc1.getSentences());
		// Collections.sort(text1Sentences, Sentence.TEXT);
		// List<Sentence> text2Sentences = new
		// ArrayList<Sentence>(doc2.getSentences());
		// Collections.sort(text2Sentences, Sentence.TEXT);
		//
		// Diff<Sentence> diff = new Diff<Sentence>(text1Sentences,
		// text2Sentences, Sentence.TEXT);
		// return getDiffingSentences(diff.diff(), text1Sentences,
		// text2Sentences);

		DiffingSentences result = new DiffingSentences();

		// put sentences into sorted sets, ordered alphabetically, eliminating
		// text-wise duplicates
		// because a duplicate sentence does not add new information anyway and
		// would introduce a
		// dilemma: which author is the owner of which of the two duplicate
		// sentences? This becomes
		// important when one of the two sentences is deleted at some time in
		// the future. We risk to
		// take away tribute from the wrong author.
		TreeSet<Sentence> sentences1 = new TreeSet<Sentence>(Sentence.TEXT);
		sentences1.addAll(rev1.getSentences());
		TreeSet<Sentence> sentences2 = new TreeSet<Sentence>(Sentence.TEXT);
		sentences2.addAll(rev2.getSentences());

		Iterator<Sentence> iter1 = sentences1.iterator();
		Iterator<Sentence> iter2 = sentences2.iterator();
		
		if (iter1.hasNext() && iter2.hasNext()) {

			Sentence sentence1 = iter1.next();
			Sentence sentence2 = iter2.next();

			while (true) {
				int compare = sentence1.toString().compareTo(sentence2.toString());
				if (compare == 0) {
					// both sentences are equal
					result.addMatchingSentencePair(sentence1, sentence2);
					if (iter1.hasNext() && iter2.hasNext()) {
						sentence1 = iter1.next();
						sentence2 = iter2.next();
					} else {
						// At least one iterator is exhausted. This means that there
						// are definitely no matching elements for the elements remaining
						// in the other iterator. Thus, we can break here and just need to
						// add the remaining elements of the other iterator to the list of
						// non-matching elements.
						break;
					}
				} else if (compare < 0) {
					// sentence1 < sentence2. since both sets are ordered, there
					// will never be a matching sentence for sentence 1.
					result.addDeletedSentence(sentence1);
					if (iter1.hasNext()) {
						sentence1 = iter1.next();
					} else {
						// iter1 is exhausted. we can terminate but have to make
						// sure that sentence2 is added to the list of added
						// sentences
						result.addNewSentence(sentence2);
						break;
					}
				} else {
					// sentence1 > sentence2. since both sets are ordered, there
					// will never be a matching sentence for sentence 2.
					result.addNewSentence(sentence2);
					if (iter2.hasNext()) {
						sentence2 = iter2.next();
					} else {
						// iter2 is exhausted. we can terminate but have to make
						// sure that sentence1 is added to the list of deleted
						// sentences
						result.addDeletedSentence(sentence1);
						break;
					}
				}
			}
		}

		// when we arrive here, at most one of the two iterators has more items

		while (iter1.hasNext()) {
			result.addDeletedSentence(iter1.next());
		}
		while (iter2.hasNext()) {
			result.addNewSentence(iter2.next());
		}

		return result;
	}

	/**
	 * 
	 * @param diff
	 * @param sentences1
	 * @param sentences2
	 * @return
	 */
	private DiffingSentences getDiffingSentences(List<Difference> diff, List<Sentence> sentences1, List<Sentence> sentences2) {
		DiffingSentences result = new DiffingSentences();
		int start, end;
		for (Difference difference : diff) {
			start = difference.getDeletedStart();
			end = difference.getDeletedEnd();
			if (start != Difference.NONE && end != Difference.NONE) {
				for (int i = start; i <= end; i++) {
					result.addDeletedSentence(sentences1.get(i));
				}
			}
			start = difference.getAddedStart();
			end = difference.getAddedEnd();
			if (start != Difference.NONE && end != Difference.NONE) {
				for (int i = start; i <= end; i++) {
					result.addNewSentence(sentences2.get(i));
				}
			}
		}
		return result;
	}

}
