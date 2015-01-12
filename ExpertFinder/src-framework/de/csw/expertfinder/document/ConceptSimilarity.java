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
 * This class encapsulates a pair of concepts and their similarity.
 * This class knows nothing about the metrics used to determine the
 * similarity. It is only a data container.
 * @author ralph
 */
public class ConceptSimilarity extends PersistableEntity<Serializable> implements Serializable {
	
	private static final long serialVersionUID = 5292020410912242249L;
	
	private Concept concept1;
	private Concept concept2;
	private Double similarity;
	
	/** Internal use only */
	ConceptSimilarity() {}
	
	/**
	 * Constructs a new ConceptSimilarity.
	 * @param concept1 The first concept.
	 * @param concept2 The second concept.
	 * @param similarity The similarity between concept1 and concept2.
	 */
	public ConceptSimilarity(Concept concept1, Concept concept2, Double similarity) {
		super();
		this.concept1 = concept1;
		this.concept2 = concept2;
		this.similarity = similarity;
	}

	/**
	 * @return the concept1
	 */
	public Concept getConcept1() {
		return concept1;
	}
	/**
	 * @param concept1 the concept1 to set
	 */
	public void setConcept1(Concept concept1) {
		this.concept1 = concept1;
	}
	/**
	 * @return the concept2
	 */
	public Concept getConcept2() {
		return concept2;
	}
	/**
	 * @param concept2 the concept2 to set
	 */
	public void setConcept2(Concept concept2) {
		this.concept2 = concept2;
	}
	/**
	 * @return the similarity
	 */
	public Double getSimilarity() {
		return similarity;
	}
	/**
	 * @param similarity the similarity to set
	 */
	public void setSimilarity(Double similarity) {
		this.similarity = similarity;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ConceptSimilarity &&
		((ConceptSimilarity)obj).getConcept1().equals(concept1) && 
		((ConceptSimilarity)obj).getConcept2().equals(concept2);
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return concept1.hashCode() + 17 * concept2.hashCode();
	}
}
