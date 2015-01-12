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
package de.csw.expertfinder.expertise;

import java.io.Serializable;

import de.csw.expertfinder.document.Author;
import de.csw.expertfinder.document.Concept;
import de.csw.expertfinder.document.PersistableEntity;

/**
 * Instances of this class represent the credibility model and are thereby
 * part of the expertise model.
 * 
 * @author ralph
 */
public class AuthorCredibility extends PersistableEntity<Serializable> implements Serializable {
	
	private Author author;
	private Concept concept;
	
	private Double expertise;
	private Double expertiseAll;
	private Long expertiseItemCount;
	private Long expertiseItemCountAll;

	private Double credibility;
	private Double credibilityAll;
	private Long credibilityItemCount;
	private Long credibilityItemCountAll;
	

	/**
	 * Internal use. 
	 */
	AuthorCredibility() {
	}
	
	/**
	 * @param author
	 * @param concept
	 * @param credibility
	 */
	public AuthorCredibility(Author author, Concept concept) {
		this.author = author;
		this.concept = concept;
	}

	/**
	 * @return the author
	 */
	public Author getAuthor() {
		return author;
	}

	/**
	 * @param author the author
	 */
	public void setAuthor(Author author) {
		this.author = author;
	}

	/**
	 * @return the concept
	 */
	public Concept getConcept() {
		return concept;
	}

	/**
	 * @param concept the concept
	 */
	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	/**
	 * @return the credibility this author has as for the topic specified by
	 *         this concept.
	 */
	public Double getCredibility() {
		return credibility;
	}

	/**
	 * @param credibility
	 *            the credibility this author has as for the topic specified by
	 *            this concept.
	 */
	public void setCredibility(Double credibility) {
		this.credibility = credibility;
	}
	
	/**
	 * @return the expertise
	 */
	public Double getExpertise() {
		return expertise;
	}

	/**
	 * @param expertise the expertise to set
	 */
	public void setExpertise(Double expertise) {
		this.expertise = expertise;
	}
	
	/**
	 * @return the expertiseItemCount
	 */
	public Long getExpertiseItemCount() {
		return expertiseItemCount;
	}

	/**
	 * @param expertiseItemCount the expertiseItemCount to set
	 */
	public void setExpertiseItemCount(Long expertiseItemCount) {
		this.expertiseItemCount = expertiseItemCount;
	}

	/**
	 * @return the credibilityItemCount
	 */
	public Long getCredibilityItemCount() {
		return credibilityItemCount;
	}

	/**
	 * @param credibilityItemCount the credibilityItemCount to set
	 */
	public void setCredibilityItemCount(Long credibilityItemCount) {
		this.credibilityItemCount = credibilityItemCount;
	}
	
	/**
	 * @return the expertiseAll
	 */
	public Double getExpertiseAll() {
		return expertiseAll;
	}

	/**
	 * @param expertiseAll the expertiseAll to set
	 */
	public void setExpertiseAll(Double expertiseAll) {
		this.expertiseAll = expertiseAll;
	}

	/**
	 * @return the credibilityAll
	 */
	public Double getCredibilityAll() {
		return credibilityAll;
	}

	/**
	 * @param credibilityAll the credibilityAll to set
	 */
	public void setCredibilityAll(Double credibilityAll) {
		this.credibilityAll = credibilityAll;
	}
	
	/**
	 * @return the expertiseItemCountAll
	 */
	public Long getExpertiseItemCountAll() {
		return expertiseItemCountAll;
	}

	/**
	 * @param expertiseItemCountAll the expertiseItemCountAll to set
	 */
	public void setExpertiseItemCountAll(Long expertiseItemCountAll) {
		this.expertiseItemCountAll = expertiseItemCountAll;
	}

	/**
	 * @return the credibilityItemCountAll
	 */
	public Long getCredibilityItemCountAll() {
		return credibilityItemCountAll;
	}

	/**
	 * @param credibilityItemCountAll the credibilityItemCountAll to set
	 */
	public void setCredibilityItemCountAll(Long credibilityItemCountAll) {
		this.credibilityItemCountAll = credibilityItemCountAll;
	}



	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof AuthorCredibility &&
		((AuthorCredibility)obj).getAuthor().equals(author) && 
		((AuthorCredibility)obj).getConcept().equals(concept);
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return author.hashCode() + 23 * concept.hashCode();
	}

	
}
