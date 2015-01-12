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
 * This is the base class common to all objects that are afflicted with
 * a lifespan, e.g. that are created and possibly deleted during a
 * revision.
 * @author ralph
 */
public abstract class LifespanObject<T extends Serializable> extends PersistableEntity<T> {
	
	private Revision revisionCreated;
	private Revision revisionDeleted;

	/**
	 * @return the revision in which this object appeared for the first time.
	 */
	public Revision getRevisionCreated() {
		return revisionCreated;
	}

	/**
	 * Sets the revision in which this object appeared for the first time.
	 * @param revisionCreated the revision in which this object appeared for the first time
	 */
	public void setRevisionCreated(Revision revisionCreated) {
		this.revisionCreated = revisionCreated;
	}

	/**
	 * Returns the revision in which this object was deleted.
	 * getRevisionDeleted - getRevisionCreated yields the lifetime of
	 * this object in its document.
	 * @return the the revision in which this object was deleted.
	 */
	public Revision getRevisionDeleted() {
		return revisionDeleted;
	}

	/**
	 * Sets the revision in which this object was deleted.
	 * @param revisionDeleted the revision in which this object was deleted.
	 */
	public void setRevisionDeleted(Revision revisionDeleted) {
		this.revisionDeleted = revisionDeleted;
	}


}
