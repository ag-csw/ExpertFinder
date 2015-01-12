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
package de.csw.expertfinder.document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * @author ralph
 *
 */
public class Sentence extends PersistableEntity<Long> implements TextChunk {
	
	public static final Comparator<Sentence> TEXT = new Comparator<Sentence>() {
		public int compare(Sentence sentence1, Sentence sentence2) {
			return sentence1.text.compareTo(sentence2.text);
		}
	};
	
	public static final Comparator<Sentence> POSITION = new Comparator<Sentence>() {
		public int compare(Sentence sentence1, Sentence sentence2) {
			return sentence1.startPos - sentence2.startPos;
		}
	};
	
	private Section section;
	
	private List<Word> words;
	private List<Noun> nouns;
	
	private String text;
	
	private int startPos;
	private int endPos;
	
	private Sentence contextBefore;
	private Sentence contextAfter;

	public Sentence() {
		words = new ArrayList<Word>();
		nouns = new ArrayList<Noun>();
	}
	
	public Sentence(List<Word> words) {
		setWords(words);
		nouns = new ArrayList<Noun>();
		for (Word word : words) {
			if (word instanceof Noun) {
				nouns.add((Noun)word);
			}
		}
	}
	
	public void addWord(Word word) {
		if (word instanceof Noun) {
			nouns.add((Noun)word);
		}
		words.add(word);
		if (text == null) {
			text = word.getWord();
		} else {
			text = text.concat(" ").concat(word.getWord());
		}
		startPos = words.get(0).getStartPos();
		endPos = word.getEndPos();
	}
	
	public Sentence getContextBefore() {
		return contextBefore;
	}

	public void setContextBefore(Sentence contextBefore) {
		this.contextBefore = contextBefore;
	}

	public Sentence getContextAfter() {
		return contextAfter;
	}

	public void setContextAfter(Sentence contextAfter) {
		this.contextAfter = contextAfter;
	}

	public List<Word> getWords() {
		return words;
	}
	
	public List<Noun> getNouns() {
		return nouns;
	}

	public void setWords(List<Word> words) {
		this.words = words;
		StringBuilder buf = new StringBuilder();
		for (Word word : words) {
			buf.append(word);
			buf.append(' ');
		}
		text = buf.substring(0, buf.length() - 1);
		
		startPos = words.get(0).getStartPos();
		endPos = words.get(words.size()-1).getEndPos();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return text;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Sentence && ((Sentence)obj).text.hashCode() == text.hashCode();
	}

	/**
	 * @see de.csw.expertfinder.document.TextChunk#getEndPos()
	 */
	public int getEndPos() {
		return endPos;
	}

	/**
	 * @see de.csw.expertfinder.document.TextChunk#getStartPos()
	 */
	public int getStartPos() {
		return startPos;
	}

	/**
	 * @return the section in which this sentence occurs.
	 * Note that sections can be nested and you can retrieve
	 * the higher level sections by calling getParentSection
	 * on the Section object.
	 */
	public Section getSection() {
		return section;
	}
	
	/**
	 * Sets the section for this sentence.
	 * Package private because this is done
	 * automatically when adding a sentence
	 * to a section.
	 * @param section
	 */
	void setSection(Section section) {
		this.section = section;
	}
	
	
}
