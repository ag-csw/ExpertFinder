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
package de.csw.expertfinder.mediawiki.ontology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.URIref;

import de.csw.expertfinder.mediawiki.api.MediaWikiAPI;
import de.csw.expertfinder.mediawiki.api.MediaWikiAPIException;

/**
 * Extracts the category tree of a MediaWiki in a rather dirty fashion (the only
 * way possible). It uses the CategoryTree extension which is actually designed
 * to load a part of the tree via Ajax.
 * 
 * There is also no way of determining the root categories (mediawiki does not
 * have ONE root category. there may exist many categories without a parent, so
 * actually there is no category tree but multiple category trees), these have
 * to be provided externally (in a text file, listing each root category on a 
 * separate line).
 * 
 * @author ralph
 */
public class CategoryExtractor {

	
	/** the location of the root category file */
	private static final String ROOT_CATEGORY_FILE_PATH = "/resources/topics/EclipseWikiTopLevelCategories";
	
	private static final String PREFIX_NS_EXPERTFINDER = "http://www.ag-csw.de/ontologies/expertfinder#";
	
	private static OntModel model;
	
	private static MediaWikiAPI wiki; 

	private static void addChildcategories(OntClass categoryClass) {
		String categoryName = categoryClass.getLocalName();
		try {
			List<String> childCategoryNames = wiki.getChildCategories(categoryName);
			for (String childCategoryName : childCategoryNames) {
				childCategoryName = URIref.encode(childCategoryName.replace(' ', '_').replace("+", "_Plus"));
				OntClass childCategoryClass = categoryClass.getOntModel().createClass(PREFIX_NS_EXPERTFINDER + childCategoryName);
				categoryClass.addSubClass(childCategoryClass);
				
				// recursively call addChildCategories
				addChildcategories(childCategoryClass);
			}
		} catch (MediaWikiAPIException e) {
			e.printStackTrace();
		}
	}
	
		
	/**
	 * @param args
	 */
	public static void readSubcategories() {
		OntClass baseClass = model.createClass(PREFIX_NS_EXPERTFINDER + "Topic");
		
		String categoryName = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(CategoryExtractor.class.getResourceAsStream(ROOT_CATEGORY_FILE_PATH)));
		try {
			
			while ((categoryName = in.readLine()) != null) {
				categoryName = URIref.encode(categoryName.replace(' ', '_'));
				OntClass rootCategoryClass = model.createClass(PREFIX_NS_EXPERTFINDER + categoryName);
				baseClass.addSubClass(rootCategoryClass);
				addChildcategories(rootCategoryClass);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
