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


/**
 * @author ralph
 *
 */
public class DocumentCategory extends LifespanObject<CategoryId> {
	
	/**
	 * For internal use only. 
	 */
	DocumentCategory() {
	}
	
	public DocumentCategory(Document document, Category category) {
		if (getId() == null) {
			setId(new CategoryId());
		}
		setDocument(document);
		setCategory(category);
	}
	
	/**
	 * @return the document
	 */
	public Document getDocument() {
		return getId().getDocument();
	}

	/**
	 * @param document the document to set
	 */
	public void setDocument(Document document) {
		getId().setDocument(document);
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {
		return getId().getCategory();
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(Category category) {
		getId().setCategory(category);
	}

	/**
	 * @see de.csw.expertfinder.document.PersistableEntity#hashCode()
	 */
	@Override
	public int hashCode() {
		return getId().hashCode();
	}
	
	/**
	 * @see de.csw.expertfinder.document.PersistableEntity#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof DocumentCategory &&
		getId().equals(((DocumentCategory) obj).getId());
	}

}
