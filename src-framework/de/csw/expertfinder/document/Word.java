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

import java.util.Comparator;

/**
 * This class represents a single word in a document.
 * As words appear (and disappear) during revisions, instances of this class
 * store the revision in which this word was created and, when deleted, the
 * revision in which is was deleted, allowing to calculate the life span of
 * the word.
 * Words can also be associated with concepts from the ontology. This is why
 * instances of this class can also store an associated concept, if one has been
 * identified.
 * @author ralph
 *
 */
public class Word extends LifespanObject<Long> implements TextChunk {
	
	public static final Comparator<Word> TEXT = new Comparator<Word>() {
		public int compare(Word word1, Word word2) {
			return word1.word.compareTo(word2.word);
		}
	};
	
	public static final Comparator<Word> POSITION = new Comparator<Word>() {
		public int compare(Word word1, Word word2) {
			return word1.startPos - word2.startPos;
		}
	};
	

	private String word;
	private String wordStem;
	
	private Concept concept;
	private Section section;
	
	private int startPos;
	private int endPos;
	
	protected Word() {
	}

	/**
	 * Constructs a Word with text and start and end position in the document
	 * and associates it with the Revision in which it was created.
	 * 
	 * @param word
	 *            the actual text of the word (actually its lemma)
	 * @param revisionCreated the revision in which this word first appeared.
	 * @param startPos
	 *            the start position of this Word in the document text
	 * @param endPos
	 *            the end position of this Word in the document text
	 */
	public Word(String word, String wordStem, int startPos, int endPos) {
		this.word = word.toLowerCase();
		this.wordStem = wordStem;
		this.startPos = startPos;
		this.endPos = endPos;
	}
	
	public String getWord() {
		return word;
	}

	public void setWord(String text) {
		this.word = text;
	}
	
	public String getWordStem() {
		return wordStem;
	}
	
	public void setWordStem(String wordStem) {
		this.wordStem = wordStem;
	}

	/**
	 * @return the concept from the ontology associated with this word
	 * or null if no such concept exists.
	 */
	public Concept getConcept() {
		return concept;
	}

	/**
	 * Sets the concept from the ontology associated with this word.
	 * @param concept the concept to set
	 */
	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	/**
	 * Gets the section this word appears in.
	 * @return the section
	 */
	public Section getSection() {
		return section;
	}

	/**
	 * @param section the section this word appears in.
	 */
	public void setSection(Section section) {
		this.section = section;
	}

	public int getStartPos() {
		return startPos;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	public int getEndPos() {
		return endPos;
	}

	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}
	
	
//	/**
//	 * @see de.csw.expertfinder.document.PersistableEntity#equals(Object)
//	 */
//	@Override
//	public boolean equals(Object obj) {
//		return obj instanceof Word && ((Word)obj).word.equals(word);
//	}
//	
//	/**
//	 * @see de.csw.expertfinder.document.PersistableEntity#hashCode()
//	 */
//	@Override
//	public int hashCode() {
//		return word.hashCode();
//	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (getRevisionCreated() == null) {
			return "*** " + word + " ***";
		}
		return word;
	}
	
}
