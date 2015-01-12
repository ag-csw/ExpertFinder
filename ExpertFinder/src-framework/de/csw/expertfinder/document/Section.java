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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ralph
 * 
 */
/**
 * @author ralph
 *
 */
public class Section extends LifespanObject<Long> implements TextChunk {

	// private List<Paragraph> paragraphs = new ArrayList<Paragraph>();

	private List<Sentence> sentences = new ArrayList<Sentence>();
	private Set<SectionConcept> concepts;
	private Set<Link> links;
	
	private List<Word> words;
	

	/**
	 * Constructs an empty Section (internal use only).
	 */
	Section() {
	}

	/**
	 * Constructs a section with a title, the nesting level, and the start and
	 * end position in the text.
	 * 
	 * @param title the section title
	 * @param level the nesting level
	 * @param startPos the start position in the text
	 * @param endPos the end position in the text
	 */
	public Section(String title, int level, int startPos, int endPos) {
		this.title = title;
		this.level = level;
		this.startPos = startPos;
		this.endPos = endPos;
	}

	private int startPos;
	private int endPos;

	private String title;
	private int level;
	private Section parentSection;
	private ArrayList<Section> childSections = new ArrayList<Section>();

	// public List<Paragraph> getParagraphs() {
	// return paragraphs;
	// }
	//
	// public void setParagraphs(List<Paragraph> paragraphs) {
	// this.paragraphs = paragraphs;
	// calculatePosition();
	// }
	//	
	// public void addParagraph(Paragraph p) {
	// paragraphs.add(p);
	// calculatePosition();
	// }

	public List<Sentence> getSentences(boolean includeChildSections) {
		if (includeChildSections) {
			ArrayList<Sentence> result = new ArrayList<Sentence>();
			result.addAll(sentences);
			for (Section childSection : childSections) {
				result.addAll(childSection.getSentences(true));
			}
			return result;
		} else {
			return sentences;
		}
	}

	// public void setSentences(List<Sentence> sentences) {
	// this.sentences = sentences;
	// startPos = sentences.get(0).getStartPos();
	// endPos = sentences.get(sentences.size()-1).getEndPos();
	// }

	public void addSentence(Sentence sentence) {
		sentences.add(sentence);
		sentence.setSection(this);
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

	// private void calculatePosition() {
	// startPos = paragraphs.get(0).getStartPos();
	// endPos = paragraphs.get(paragraphs.size()-1).getEndPos();
	// }

	/**
	 * Sets this section's start position relative to the document's entire
	 * text.
	 */
	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	/**
	 * Sets this section's end position relative to the document's entire text.
	 */
	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	/**
	 * @return this section's parent section
	 */
	public Section getParentSection() {
		return parentSection;
	}

	/**
	 * Sets this section's parent section
	 * 
	 * @param parentSection
	 */
	public void setParentSection(Section parentSection) {
		if (parentSection == null) {
			return;
		}
		this.parentSection = parentSection;
		parentSection.addChildSection(this);
	}

	/**
	 * @return all child sections of this section
	 */
	private List<Section> getChildSections() {
		return childSections;
	}

	/**
	 * adds a child section to this section
	 * 
	 * @param childSection
	 */
	private void addChildSection(Section childSection) {
		childSections.add(childSection);
	}

	/**
	 * @return this section's title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * sets the title for this section
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return this section's level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Sets this section's level
	 * 
	 * @param level
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	
	/**
	 * @return the concepts associated with this section
	 */
	public Set<SectionConcept> getConcepts() {
		return concepts;
	}

	/**
	 * @param concepts the concepts associated with this section
	 */
	public void setConcepts(Set<SectionConcept> concepts) {
		// we need to do addAll, because hibernate does not allow sharing of proxied collections
		if (concepts == null)
			return;
		if (this.concepts == null) {
			this.concepts = new HashSet<SectionConcept>();
		}
		this.concepts.addAll(concepts);
	}
	
	/**
	 * Associates a concept with this section.
	 * @param concept
	 */
	public void addConcept(Concept concept, double similarity) {
		if (concepts == null) {
			concepts = new HashSet<SectionConcept>();
		}
		concepts.add(new SectionConcept(this, concept, similarity));
	}
	
	/**
	 * @return the links of this section
	 */
	public Set<Link> getLinks() {
		return links;
	}

	/**
	 * @param links the links for this section
	 */
	public void setLinks(Set<Link> links) {
		this.links = links;
	}
	
	public void addLink(Link link) {
		if (links == null) {
			links = new HashSet<Link>();
		}
		links.add(link);
	}

	/**
	 * @see de.csw.expertfinder.document.PersistableEntity#hashCode()
	 */
	@Override
	public int hashCode() {
		return level + 29 * startPos;
	}
	
	/**
	 * @see de.csw.expertfinder.document.PersistableEntity#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Section
			&& ((Section)obj).getLevel() == level
			&& ((Section)obj).getStartPos() == startPos;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Section " + getId() +  ": " + title + " (" + level + ") " + startPos + "-" + endPos;
	}

	/**
	 * @return this Sections words
	 */
	public List<Word> getWords() {
		return words;
	}

	/**
	 * @param words the words to set for this Section.
	 */
	public void setWords(List<Word> words) {
		this.words = words;
	}

	
}
