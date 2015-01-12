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
package de.csw.expertfinder.diff.sentencealignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.log4j.Logger;

import de.csw.expertfinder.document.Sentence;

/**
 * @author ralph
 *
 */
public class BestMatchingSentences implements Iterable<BestMatchingSentence> {
	
	private static final Logger log = Logger.getLogger(BestMatchingSentences.class);
	
	private final ArrayList<BestMatchingSentence> bestFittingSentences = new ArrayList<BestMatchingSentence>();
	
	private final HashSet<Sentence> oldRest = new HashSet<Sentence>();
	private final HashSet<Sentence> newRest = new HashSet<Sentence>();
	
	public void add(Sentence oldSentence, Sentence newSentence, double distance) {
		bestFittingSentences.add(new BestMatchingSentence(oldSentence, newSentence, distance));
		oldRest.add(oldSentence);
		newRest.add(newSentence);
	}
	
	/**
	 * Returns an iterator which returns the best matching sentence pairs.
	 * After the itarator is exhausted, call getOldRest and getNewRest
	 * for retrieving the sentences that have no match.
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<BestMatchingSentence> iterator() {
		Collections.sort(bestFittingSentences);
		return new Iterator<BestMatchingSentence>() {
			
			private Iterator<BestMatchingSentence> iter = bestFittingSentences.iterator();
			private BestMatchingSentence next;
		
			public void remove() {
				throw new UnsupportedOperationException();
			}
		
			public BestMatchingSentence next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				BestMatchingSentence result = next;
				next = null;
				return result;
			}
		
			public boolean hasNext() {
				if (next != null)
					return true;
				do {
					if (!iter.hasNext())
						return false;
					next = iter.next();
//					log.debug(next.getOldSentence()+", "+next.getNewSentence()+": "+next.getDistance());
				} while (!(oldRest.contains(next.getOldSentence()) && newRest.contains(next.getNewSentence())));
				// skip as long as not both sentences are in the rest sets.
				
				oldRest.remove(next.getOldSentence());
				newRest.remove(next.getNewSentence());
//				log.debug("TAKEN! : " +next.getOldSentence()+", "+next.getNewSentence()+": "+next.getDistance());
				return true;
			}
		};
	}

	/**
	 * Returns an iterator returning the sentences from the old revision that
	 * have no good match beyond the sentences from the new revision.
	 * 
	 * @return an iterator returning the sentences from the old revision that
	 * have no good match beyond the sentences from the new revision.
	 */
	public Set<Sentence> oldRest() {
		return oldRest;
	}

	/**
	 * Returns an iterator returning the sentences from the new revision that
	 * have no good match beyond the sentences from the old revision.
	 * 
	 * @return an iterator returning the sentences from the new revision that
	 * have no good match beyond the sentences from the old revision.
	 */
	public Set<Sentence> newRest() {
		return newRest;
	}
}
