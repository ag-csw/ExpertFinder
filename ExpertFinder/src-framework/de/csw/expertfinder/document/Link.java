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
import java.util.List;

/**
 * @author ralph
 *
 */
public class Link extends LifespanObject<Long> {
	private Section sourceSection;
	private Section targetSection;
	
	private int startPos;
	private int endPos;
	
	private List<Word> description;
	
	/**
	 * Constructs an empty Link (internal use only).
	 */
	Link() {
	}

	/**
	 * Constructs a Link with its source and target Section, the words that
	 * describe it, and start and end position within the document text.
	 * 
	 * @param sourceSection
	 * @param targetSection
	 * @param description
	 * @param startPos
	 * @param endPos
	 */
	public Link(Revision revisionCreated, Section sourceSection, Section targetSection, List<Word> description, int startPos, int endPos) {
		super();
		setRevisionCreated(revisionCreated);
		this.sourceSection = sourceSection;
		this.targetSection = targetSection;
		this.description = description == null ? new ArrayList<Word>() : description;
		this.startPos = startPos;
		this.endPos = endPos;
	}

	/**
	 * @return the sourceSection
	 */
	public Section getSourceSection() {
		return sourceSection;
	}

	/**
	 * @param sourceSection the sourceSection to set
	 */
	public void setSourceSection(Section sourceSection) {
		this.sourceSection = sourceSection;
	}

	/**
	 * @return the targetSection
	 */
	public Section getTargetSection() {
		return targetSection;
	}

	/**
	 * @param targetSection the targetSection to set
	 */
	public void setTargetSection(Section targetSection) {
		this.targetSection = targetSection;
	}

	/**
	 * @return the description
	 */
	public List<Word> getDescription() {
		return description;
	}
	
	

	/**
	 * @return the startPos
	 */
	public int getStartPos() {
		return startPos;
	}

	/**
	 * @param startPos the startPos to set
	 */
	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	/**
	 * @return the endPos
	 */
	public int getEndPos() {
		return endPos;
	}

	/**
	 * @param endPos the endPos to set
	 */
	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(List<Word> description) {
		this.description = description;
	}
	
	/**
	 * @see de.csw.expertfinder.document.PersistableEntity#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Link))
			return false;
		
		Revision myRevisionDeleted = getRevisionDeleted();
		Revision otherRevisionDeleted = ((Link)obj).getRevisionDeleted();
		
		if (myRevisionDeleted == null ^ otherRevisionDeleted != null)
			return false;
		
		return
			(myRevisionDeleted == null && otherRevisionDeleted == null ||
			myRevisionDeleted.equals(otherRevisionDeleted)) &&
			getStartPos() == ((Link)obj).getStartPos() &&
			getEndPos() == ((Link)obj).getEndPos();
	}
	
	/**
	 * @see de.csw.expertfinder.document.PersistableEntity#hashCode()
	 */
	@Override
	public int hashCode() {
		return getDescription().hashCode() + 29 * getRevisionCreated().hashCode();
	}

}
