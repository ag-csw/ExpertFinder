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
import java.util.Date;
import java.util.List;

/**
 * This class represents a document revision or version.
 * 
 * @author ralph
 * 
 */
public class Revision extends PersistableEntity<Long> {

	private List<Section> sections = new ArrayList<Section>();
	
	private Author author;
	private Date timestamp;
	private Document document;
	private long count;

	/**
	 * Constructs an empty Revision (internal use only).
	 */
	Revision() {
	}

	/**
	 * Constructs a Revision with an id and a timestamp, and associates a
	 * document and an author with it.
	 * 
	 * @param id this revision's unique id
	 * @param document the document of which this is a revision.
	 * @param author this revision's author
	 * @param timestamp the timestamp of of this revision.
	 */
	public Revision(Long id, Document document, Author author, Date timestamp) {
		setId(id);
		this.document = document;
		this.author = author;
		this.timestamp = timestamp;
	}

	/**
	 * Returns this revision's sections.
	 * 
	 * @return
	 */
	public List<Section> getSections() {
		return sections;
	}

	/**
	 * Sets this revision's sections.
	 * 
	 * @param sections
	 */
	public void setSections(List<Section> sections) {
		this.sections = sections;
		// for (Section section : sections) {
		// List<Paragraph> paragraphs = section.getParagraphs();
		// for (Paragraph paragraph : paragraphs) {
		// this.sentences.addAll(paragraph.getSentences());
		// }
		// }
	}

	/**
	 * Adds a section to this revision of the document.
	 * 
	 * @param section
	 *            the section that is added to the document.
	 */
	public void addSection(Section section) {
		sections.add(section);
	}

	/**
	 * Returns all the sentences in this document.
	 * 
	 * @return
	 */
	public List<Sentence> getSentences() {
		// sentences are actually stored in the sections. So traverse
		// the section tree.
		ArrayList<Sentence> sentences = new ArrayList<Sentence>();
		for (Section section : sections) {
			sentences.addAll(section.getSentences(true));
		}
		return sentences;
	}
	
	/**
	 * Returns a list with all words of this Revision.
	 * @return
	 */
	public List<Word> getWords() {
		ArrayList<Word> words = new ArrayList<Word>();
		List<Sentence> sentences = getSentences();
		for (Sentence sentence : sentences) {
			words.addAll(sentence.getWords());
		}
		return words;
	}

	/**
	 * @return the author
	 */
	public Author getAuthor() {
		return author;
	}

	/**
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(Author author) {
		this.author = author;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the document
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * @param document
	 *            the document to set
	 */
	public void setDocument(Document document) {
		this.document = document;
	}
	
	
	

	/**
	 * @see de.csw.expertfinder.document.PersistableEntity#hashCode()
	 */
	@Override
	public int hashCode() {
		return getId().hashCode();
	}
	
	/**
	 * @see de.csw.expertfinder.document.PersistableEntity#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Revision && ((Revision)obj).getId().equals(getId());
	}

	/**
	 * Returns the index of this revision among all revisions of the associated document.
	 * (Returns n if this revision is the n-th revision of this revision's document.)
	 * @return The index of this revision among all revisions of the associated document.
	 */
	public long getCount() {
		return count;
	}

	/**
	 * Sets the index of this revision.
	 * @param count the count to set
	 * @see #getCount()
	 */
	public void setCount(long count) {
		this.count = count;
	}
	
	

}
