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

import java.util.HashSet;
import java.util.Set;


/**
 * This class represents a document. A document may have (and normally has)
 * multiple versions or revisions. The actual content and the author(s) are
 * therefore retrievable via each of the Document's Revisions, not via the
 * Document object itself.
 * 
 * @author ralph
 *
 */
public class Document extends PersistableEntity<Long> {
	
	private Document redirectsTo;
	private String title;
	private Set<Concept> concepts;
	private Set<DocumentCategory> categories;

	
	/**
	 * Constructs an empty Document (used only by persistence framework). 
	 */
	Document() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Constructs a document with a title
	 * @param title the Document's title
	 */
	public Document(long id, String title) {
		setId(id);
		this.title = title;
	}



	/**
	 * Sets this document's title.
	 * @param documentTitle the documentTitle to set
	 */
	public void setTitle(String documentTitle) {
		this.title = documentTitle;
	}

	/**
	 * @return the document's title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * If this document is only a redirect to a different document, then the
	 * document this document actually redirects to is returned. Returns null if
	 * this is the original document and not only a redirect.
	 * 
	 * @return The target document if this is only a redirect.
	 */
	public Document getRedirectsTo() {
		return redirectsTo;
	}

	/**
	 * Sets the document this document is a redirect to.
	 * @param redirectsTo
	 */
	public void setRedirectsTo(Document redirectsTo) {
		this.redirectsTo = redirectsTo;
	}
	
	

	/**
	 * @return the concepts associated with this document
	 */
	public Set<Concept> getConcepts() {
		return concepts;
	}

	/**
	 * @param concepts the concepts associated with this document
	 */
	public void setConcepts(Set<Concept> concepts) {
		this.concepts = concepts;
	}
	
	/**
	 * Associates a concept with this document.
	 * @param concept
	 */
	public void addConcept(Concept concept) {
		if (concepts == null) {
			concepts = new HashSet<Concept>();
		}
		concepts.add(concept);
	}
	
	/**
	 * @return this revision's categories
	 */
	public Set<DocumentCategory> getCategories() {
		return categories;
	}

	/**
	 * @param categories this revision's categories
	 */
	public void setCategories(Set<DocumentCategory> categories) {
		this.categories = categories;
	}
	
	/**
	 * Adds a category to this document.
	 * @param category
	 */
	public void addCategory(DocumentCategory category) {
		if (categories == null)
			categories = new HashSet<DocumentCategory>();
		category.setDocument(this);
		categories.add(category);
	}
	

	/**
	 * @see de.csw.expertfinder.document.PersistableEntity#hashCode()
	 */
	@Override
	public int hashCode() {
		return title.hashCode();
	}

	/**
	 * @see de.csw.expertfinder.document.PersistableEntity#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Document))
			return false;
		
		return this.title.equals(((Document)obj).title);

	}
}
