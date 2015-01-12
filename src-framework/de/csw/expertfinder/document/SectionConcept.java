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

import java.io.Serializable;

/**
 * This class encapsulates the Section - Concept many-to-many relationship
 * @author ralph
 *
 */
public class SectionConcept extends PersistableEntity<Serializable> implements Serializable {
	
	private static final long serialVersionUID = 1601765602967009812L;
	
	private Section section;
	private Concept concept;
	private double similarity;
	
	/**
	 * Internal use only.
	 */
	public SectionConcept() {
	}
	
	/**
	 * @param section
	 * @param concept
	 * @param similarity
	 */
	public SectionConcept(Section section, Concept concept, double similarity) {
		this.section = section;
		this.concept = concept;
		this.similarity = similarity;
	}



	/**
	 * @return the section
	 */
	public Section getSection() {
		return section;
	}



	/**
	 * @param section the section to set
	 */
	public void setSection(Section section) {
		this.section = section;
	}



	/**
	 * @return the concept
	 */
	public Concept getConcept() {
		return concept;
	}



	/**
	 * @param concept the concept to set
	 */
	public void setConcept(Concept concept) {
		this.concept = concept;
	}



	/**
	 * @return the similarity
	 */
	public double getSimilarity() {
		return similarity;
	}



	/**
	 * @param similarity the similarity to set
	 */
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	
	/**
	 * @see de.csw.expertfinder.document.PersistableEntity#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof SectionConcept &&
		((SectionConcept)obj).section.equals(section) &&
		((SectionConcept)obj).concept.equals(concept);		
	}
	
	/**
	 * @see de.csw.expertfinder.document.PersistableEntity#hashCode()
	 */
	@Override
	public int hashCode() {
		return section.hashCode() + 13 * concept.hashCode();
	}
	
}
