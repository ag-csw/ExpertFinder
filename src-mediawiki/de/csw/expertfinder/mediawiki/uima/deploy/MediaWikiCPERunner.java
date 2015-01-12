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
package de.csw.expertfinder.mediawiki.uima.deploy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.collection.StatusCallbackListener;
import org.apache.uima.collection.metadata.CasProcessorConfigurationParameterSettings;
import org.apache.uima.collection.metadata.CpeDescription;
import org.apache.uima.collection.metadata.CpeDescriptorException;
import org.apache.uima.collection.metadata.NameValuePair;
import org.apache.uima.util.XMLInputSource;

import de.csw.expertfinder.config.Config;
import de.csw.expertfinder.ontology.OntologyIndex;

/**
 * This class provides methods for programmatically running the MediaWiki Expert
 * Finder UIMA Collection Processing Engine (CPE) defined in a CPE descriptor
 * file.
 * 
 * It can run on a single document collection or in batch mode, if provided with
 * a file containing a list of document titles.
 *  
 * @author ralph
 */
public class MediaWikiCPERunner {

	private static final Logger log = Logger.getLogger(MediaWikiCPERunner.class);

	private static final URL CPE_DESCRIPTOR_URL = MediaWikiCPERunner.class.getResource("ExpertFinder CPE Config.xml");
	private static final URL DOCUMENT_TITLE_FILE = MediaWikiCPERunner.class.getResource("WikiArticles");

	private URL cpeDescriptorURL;
	private BufferedReader documentTitleReader;
	private StatusCallbackListener statusCallbackListener;
	
	CollectionProcessingEngine mCPE;

	/**
	 * Constructs a {@link MediaWikiCPERunner} instance.
	 * 
	 * @param cpeDescriptorURL
	 *            a URL pointing to the CPE descriptor file defining the
	 *            collection processing engine to run.
	 * @param documentTitleFile
	 *            a URL pointing to a text file containing titles of documents
	 *            that should be batch processed. Each line of the file must
	 *            contain one document title. Lines beginning with # are
	 *            ignored.
	 * @throws FileNotFoundException
	 */
	public MediaWikiCPERunner(URL cpeDescriptorURL, URL documentTitleFile) throws FileNotFoundException {
		this.cpeDescriptorURL = cpeDescriptorURL;
		this.documentTitleReader = new BufferedReader(new FileReader(documentTitleFile.getFile()));
	}

	/**
	 * Sets a callback listener for the notifications the UIMA Collection
	 * Processing Management (CPM) emits while it is running.
	 * 
	 * @param statusCallbackListener
	 */
	public void setStatusCallbackListener(StatusCallbackListener statusCallbackListener) {
		this.statusCallbackListener = statusCallbackListener;
	}

	/**
	 * Processes the next document collection from the document titles file.
	 * 
	 * @throws UIMAException if an exception inside the CPM occurs.
	 * @throws IOException if an error occurs while reading the file containing the document titles.
	 * @throws CpeDescriptorException if an error occurs processing the CPE descriptor.
	 */
	public boolean processNext() throws UIMAException, IOException, CpeDescriptorException {

		String line;
		for(;;) {
			line = documentTitleReader.readLine();
			if (line == null)
				return false; // done
			if (!(line.isEmpty() || line.startsWith("#")))
				break;  // not commented out or empty
		}
		process(line);
		return true;
	}
	
	/**
	 * Runs the MediaWiki CPE, passing the given document title to the collection reader.
	 * @param documentTitle The title of the document which should be processed.
	 * @throws UIMAException if an exception inside the CPM occurs.
	 * @throws IOException if an error occurs while reading the file containing the document titles.
	 * @throws CpeDescriptorException if an error occurs processing the CPE descriptor.
	 */
	public void process(final String documentTitle) throws UIMAException, IOException, CpeDescriptorException {
		
		log.info("Processing article " + documentTitle);
		
		// parse CPE descriptor in file specified on command line
		CpeDescription cpeDesc;
		cpeDesc = UIMAFramework.getXMLParser().parseCpeDescription(new XMLInputSource(cpeDescriptorURL));
		
		// TODO generalize this (this parameter is specific to the MediaWiki Expertfinder CPE)
		cpeDesc.getAllCollectionCollectionReaders()[0].setConfigurationParameterSettings(new CasProcessorConfigurationParameterSettings() {
			public void setParameterValue(String aParamName, Object aValue) {
				log.debug(aParamName + " = " + aValue + ". This should not have happened.");
			}
			public Object getParameterValue(String aParamName) {
				if ("documentTitle".equals(aParamName))
					return documentTitle;
				return null;
			}
			public NameValuePair[] getParameterSettings() {
				return new NameValuePair[] {
					new NameValuePair() {
						public void setValue(Object aValue) {
							log.debug(aValue + ". This should not have happened.");
						}
						public void setName(String aName) {
							log.debug(aName + ". This should not have happened.");
						}
						public Object getValue() {
							return documentTitle;
						}
						public String getName() {
							return "documentTitle";
						}
					}
				};
			}
		});
		
		// instantiate CPE
		mCPE = UIMAFramework.produceCollectionProcessingEngine(cpeDesc);
		
		// Create and register a Status Callback Listener
		mCPE.addStatusCallbackListener(statusCallbackListener);
		
		// Start Processing
		mCPE.process();
		
	}

	/**
	 * This class implements a UIMA CPM status callback listener. It realizes
	 * the batch processing of this runner by subsequently calling the
	 * {@link MediaWikiCPERunner#processNext()} method when one collection
	 * processing has finished successfully.
	 * 
	 * @author ralph
	 * 
	 */
	private static class BatchStatusCallbackListener implements StatusCallbackListener {
		
		private MediaWikiCPERunner caller;

		/**
		 * Constructs a {@link BatchStatusCallbackListener}.
		 * 
		 * @param caller
		 *            the caller on which the
		 *            {@link MediaWikiCPERunner#processNext()} method should be
		 *            called when the processing of a collection has finished
		 *            successfully.
		 */
		public BatchStatusCallbackListener(MediaWikiCPERunner caller) {
			this.caller = caller;
		}

		/**
		 * @see org.apache.uima.collection.StatusCallbackListener#entityProcessComplete(org.apache.uima.cas.CAS,
		 *      org.apache.uima.collection.EntityProcessStatus)
		 */
		public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
		}
	
		/**
		 * @see org.apache.uima.collection.base_cpm.BaseStatusCallbackListener#aborted()
		 */
		public void aborted() {
			log.error("Aborted");
		}
	
		/**
		 * @see org.apache.uima.collection.base_cpm.BaseStatusCallbackListener#batchProcessComplete()
		 */
		public void batchProcessComplete() {
			log.info("Batch process completed");
		}
	
		/**
		 * Triggers the processing of the next document collection in the batch.
		 * @see org.apache.uima.collection.base_cpm.BaseStatusCallbackListener#collectionProcessComplete()
		 */
		public void collectionProcessComplete() {
			log.info("Collection process completed");

			try {
				caller.processNext();
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Trying again...");
				try {
					caller.processNext();
				} catch (Exception e1) {
					e1.printStackTrace();
					log.error("Giving up");
				}
			}
		}
	
		/**
		 * @see org.apache.uima.collection.base_cpm.BaseStatusCallbackListener#initializationComplete()
		 */
		public void initializationComplete() {
			log.info("Initialisation complete");
		}
	
		/**
		 * @see org.apache.uima.collection.base_cpm.BaseStatusCallbackListener#paused()
		 */
		public void paused() {
			log.info("Paused");
		}
	
		/**
		 * @see org.apache.uima.collection.base_cpm.BaseStatusCallbackListener#resumed()
		 */
		public void resumed() {
			log.info("resumed");
		}
	
	}

	/**
	 * Runs this {@link MediaWikiCPERunner} with the CPE descriptor specified by
	 * {@link MediaWikiCPERunner#CPE_DESCRIPTOR_URL} and the batch file
	 * specified by {@link MediaWikiCPERunner#DOCUMENT_TITLE_FILE}.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Config.read(MediaWikiCPERunner.class.getResourceAsStream("/conf/ExpertFinder.properties"));
			OntologyIndex.get().load(OntologyIndex.class.getResource(Config.getAppProperty(Config.Key.ONTOLOGY_FILE)));
			final MediaWikiCPERunner runner = new MediaWikiCPERunner(CPE_DESCRIPTOR_URL, DOCUMENT_TITLE_FILE);
			runner.setStatusCallbackListener(new BatchStatusCallbackListener(runner));
			
			try {
				runner.processNext();
			} catch (UIMAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CpeDescriptorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
