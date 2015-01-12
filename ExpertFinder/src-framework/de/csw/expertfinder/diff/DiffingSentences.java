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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import de.csw.expertfinder.document.Sentence;

public class DiffingSentences {

	/** sentences that are present in version i-1 but not in version i */
	private ArrayList<Sentence> deletedSentences = new ArrayList<Sentence>();

	/** sentences that are not present in version i-1 but in version i */
	private ArrayList<Sentence> newSentences = new ArrayList<Sentence>();
	
	private HashMap<Sentence, Sentence> matchingSentencesOldToNew = new HashMap<Sentence, Sentence>();
	private HashMap<Sentence, Sentence> matchingSentencesNewToOld = new HashMap<Sentence, Sentence>();
	
	/**
	 * Adds a sentence that was not present in revision n-1 but has been added in revision n. 
	 * @param t
	 */
	public void addNewSentence(Sentence t) {
		newSentences.add(t);
	}

	/**
	 * Adds a sentence that was present in revision n-1 but has been deleted in revision n. 
	 * @param t
	 */
	public void addDeletedSentence(Sentence t) {
		deletedSentences.add(t);
	}

	/**
	 * Returns a list of all sentences that were present in revision n-1 but have been deleted in revision n.
	 * @return
	 */
	public ArrayList<Sentence> getDeletedSentences() {
		return deletedSentences;
	}

	/**
	 * Returns a list of all sentences that were not present in revision n-1 but have been added in revision n.
	 * @return
	 */
	public ArrayList<Sentence> getNewSentences() {
		return newSentences;
	}
	
	/**
	 * Adds a pair of textually (but not necessarily position-wise) equal sentences.
	 * @param oldSentence the first sentence
	 * @param newSentence the second sentence that is textually equal to the first sentence
	 */
	public void addMatchingSentencePair(Sentence oldSentence, Sentence newSentence) {
		matchingSentencesOldToNew.put(oldSentence, newSentence);
		matchingSentencesNewToOld.put(newSentence, oldSentence);
	}
	
	/**
	 * Gets the first sentence that is textually equal to the given second sentence. 
	 * @param newSentence
	 * @return
	 */
	public Sentence getMatchingOldSentence(Sentence newSentence) {
		return matchingSentencesNewToOld.get(newSentence);
	}

	/**
	 * Gets the second sentence that is textually equal to the given first sentence. 
	 * @param newSentence
	 * @return
	 */
	public Sentence getMatchingNewSentence(Sentence oldSentence) {
		return matchingSentencesOldToNew.get(oldSentence);
	}
	
	/**
	 * Gets all sentences from the n-1st revision that have an equivalent in revision n.
	 * @return
	 */
	public Set<Sentence> getMatchingOldSentences() {
		return matchingSentencesOldToNew.keySet();
	}
	
	/**
	 * Gets all sentences from the n-th revision that have an equivalent in revision n-1.
	 * @return
	 */
	public Set<Sentence> getMatchingNewSentences() {
		return matchingSentencesNewToOld.keySet();
	}
}
