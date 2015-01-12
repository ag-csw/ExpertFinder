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
package de.csw.expertfinder;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.iterators.EmptyIterator;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

import de.csw.expertfinder.config.Config;
import de.csw.expertfinder.expertise.ExpertiseModel;
import de.csw.expertfinder.ontology.OntologyIndex;

/**
 * This class implements the backend handle for the ExpertFinder application.
 * 
 * This is the single entry point front-ends should have to deal with.
 * 
 * This class is an. For front-end notification, clients can
 * either register one ore more callbacks, or manually poll the backend's state.
 * 
 * @author ralph
 */
public class ExpertFinder {
	
	private static final Logger log = Logger.getLogger(ExpertFinder.class);

	private ArrayList<String> topicLabelsSorted;
	
	/**
	 * Returns the singleton instance of this class.
	 * 
	 * Returns immediately, but initialization happens asynchronically. Thus,
	 * clients have to check for the initialization state either by calling
	 * {@link #initialized()}, or by callback notification. 
	 * 
	 * @return
	 */
	public ExpertFinder() {
	}

	private static ApplicationState applicationState = ApplicationState.UNINITIALIZED;
	
	public ApplicationState getApplicationState() {
		return applicationState;
	}

	/**
	 * State information, passed to observers.
	 * 
	 * @author ralph
	 */
	public enum ApplicationState {
		
		UNINITIALIZED("Uninitialized"),
		INITIZALIZING("Initializing"),
		INIT_DONE("Initialization complete"), 
		ERROR("An error occured");
		
		private String message;
		private ApplicationState(String message) { this.message = message; }
		public String getMessage() { return message;};
	};

	public void init(final InputStream propertiesIS, final Callback... callbacks) {
		
		// initialization can take a while, and we do not want the front-end to
		// block
		// or time-out (in case it's a web application).
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				// init code here
				
				try {
					Config.read(propertiesIS);
					
					URL ontologyFileUrl = ExpertFinder.class.getResource(Config.getAppProperty(Config.Key.ONTOLOGY_FILE));
					OntologyIndex.get().load(ontologyFileUrl);
				} catch (Throwable e) {
					applicationState = ApplicationState.ERROR;
					for (Callback callback : callbacks) {
						callback.notify(ApplicationState.ERROR, e);
					}
				}
				
				applicationState = ApplicationState.INIT_DONE;
				for (Callback callback : callbacks) {
					callback.notify(ApplicationState.INIT_DONE);
				}
			}
		}).start();
	}

	/**
	 * Returns an iterator over all super classes of the class specified by the
	 * given uri or an empty iterator if no such class exists or if it has no
	 * super classes.
	 * 
	 * @param uri
	 * @return an iterator over all super classes of the class specified by the
	 *         given uri or an empty iterator if no such class exists or if it
	 *         has no super classes.
	 */
	@SuppressWarnings("unchecked")
	public Iterator<OntClass> getSuperClasses(String uri) {
		OntModel model = OntologyIndex.get().getModel();
		OntClass clazz = model.getOntClass(uri);
		if (clazz != null) {
			return clazz.listSuperClasses(true);
		}
		
		return EmptyIterator.INSTANCE;
	}

	/**
	 * Returns an iterator over all subclasses of the class specified by the
	 * given uri or an empty iterator if no such class exists or if it has no
	 * subclasses.
	 * 
	 * @param uri
	 * @return an iterator over all super classes of the class specified by the
	 *         given uri or an empty iterator if no such class exists or if it
	 *         has no super classes.
	 */
	@SuppressWarnings("unchecked")
	public Iterator<OntClass> getSubClasses(String uri) {
		OntModel model = OntologyIndex.get().getModel();
		OntClass clazz = model.getOntClass(uri);
		if (clazz != null) {
			return clazz.listSubClasses(true);
		}
		
		return EmptyIterator.INSTANCE;
	}
	
	/**
	 * Returns the names of all topics that are covered by the underlying domain model.
	 * @return a list containing the names of all topics that are covered by the underlying domain model.
	 */
	public List<String> getAvailableTopicLabels() {
		if (applicationState != ApplicationState.INIT_DONE)
			return Collections.EMPTY_LIST;
		
		if (topicLabelsSorted == null) {
			Set<String> topicLabelsUnsorted = OntologyIndex.get().getAllConceptLabels();
			topicLabelsSorted = new ArrayList<String>(topicLabelsUnsorted.size());
			topicLabelsSorted.addAll(topicLabelsUnsorted);
			Collections.sort(topicLabelsSorted);
		}
		return topicLabelsSorted;
	}
	
	/**
	 * Retrieves a Jena OWL class object for the given label (if there are
	 * multiple classes with the same label, this method arbitrarily returns one of them).
	 * 
	 * @param label A topic name
	 * @return
	 */
	public OntClass getOntClassForLabel(String label) {
		return OntologyIndex.get().getOntClass(label);
	}
	
	/**
	 * Returns all topics the given author has contributed to.
	 * @param authorName
	 * @return
	 */
	public Set<String> getTopicsForAuthor(String authorName) {
		return ExpertiseModel.get().getTopicsForAuthor(authorName);
	}
	
	/**
	 * Returns the given author's consolidated expertise score for the given
	 * topic and similar topics, based on the similarity metric and the
	 * similarity threshold value specified in the application's configuration
	 * file.
	 *
	 * @param authorName
	 * @param topicName
	 * @return
	 */
	public double getExpertise(String authorName, String topicName) {
		return ExpertiseModel.get().getExpertiseScore(authorName, topicName);
	}
	
	/**
	 * Calculates the given author's credibility or reputation wrt. the topic
	 * represented by the given concept, based on the longevity on his/her
	 * contributions to that topic, not including similar topics.
	 * 
	 * @param authorName
	 * @param topicName
	 * @return
	 */
	public double getReputation(String authorName, String topicName) {
		return ExpertiseModel.get().getCredibility(authorName, topicName);
	}
}
