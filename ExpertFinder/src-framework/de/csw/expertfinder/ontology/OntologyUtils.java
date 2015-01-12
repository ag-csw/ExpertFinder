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
package de.csw.expertfinder.ontology;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods for working with ontologies.
 * 
 * @author rheese
 * 
 */
public class OntologyUtils {

	/**
	 * Generates a concept label from the local part of a given URI.
	 * 
	 * @param uri
	 *            a URI
	 * @return a label
	 */
	public static String getLabel(String uri) {
		if (uri == null)
			return null;
		String label = uri.substring(uri.lastIndexOf('#') + 1);
		label = label.replace('_', ' ');
		return label;
	}

	/**
	 * Generates the concept labels for a list of URIs. The order is kept.
	 * 
	 * @param uris
	 *            a list of URIs
	 * @return a list of corresponding labels
	 */
	public static List<String> getLabels(List<String> uris) {
		if (uris == null)
			return null;

		List<String> labels = new ArrayList<String>(uris.size());
		for (String u : uris)
			labels.add(getLabel(u));

		return labels;
	}

}
