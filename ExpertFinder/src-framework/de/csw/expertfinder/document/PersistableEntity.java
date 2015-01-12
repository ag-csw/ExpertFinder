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
 * This is the base class of all persistable entity classes.
 * 
 * @author ralph
 */
public abstract class PersistableEntity<T extends Serializable> {

	private T id;

	public T getId() {
		return id;
	}

	public void setId(T id) {
		this.id = id;
	}

	/**
	 * Subclasses must override {@link #equals(Object)} and {@link #hashCode()}.
	 * See <a href="http://docs.jboss.org/hibernate/stable/core/reference/en/html/persistent-classes.html#persistent-classes-equalshashcode"
	 * >the hibernate documentation</a> for the reasons.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	/**
	 * Subclasses must override {@link #equals(Object)} and {@link #hashCode()}.
	 * See <a href="http://docs.jboss.org/hibernate/stable/core/reference/en/html/persistent-classes.html#persistent-classes-equalshashcode"
	 * >the hibernate documentation</a> for the reasons.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
}
