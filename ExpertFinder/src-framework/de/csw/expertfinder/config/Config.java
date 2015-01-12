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
package de.csw.expertfinder.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * This class provides static methods for accessing the general configuration of
 * the ExpertFinder application.
 * 
 * @author ralph
 * 
 */
public class Config {
	
	private static Logger log = Logger.getLogger(Config.class);

	/**
	 * A type-safe encapsulation of property file keys. Unfortunately, Java enum
	 * types cannot be type-parameterized, thus it is impossible to make the
	 * key-value binding type-safe as well. As a workaround, the Key enum allows
	 * to specify the class the values are expected to be assignable from, allowing
	 * for type checking at runtime. When the values are retrieved, an explicit cast
	 * is still necessary.
	 * 
	 * @author ralph
	 */
	public enum Key {
		
		/** The type of the target wiki (currently, only MediaWiki is supported) */
		WIKI_TYPE("wiki.type", String.class, true, "MediaWiki"),
		
		/** The URL of the target wiki's API */
		WIKI_MEDIAWIKI_API ("wiki.mediawiki.api", String.class, true),
		
		/** The URL of the wiki's ajax interface */
		WIKI_MEDIAWIKI_AJAX ("wiki.mediawiki.ajax", String.class, false),
		
		/** filename of the deployed domain ontology file */
		ONTOLOGY_FILE ("ontology.file", String.class, true),
		
		/** uri of the base concept in the ontological model */
		ONTOLOGY_BASE_CONCEPT_URI ("ontology.baseconceptURI", String.class, true),
		
		/** uri of the base concept of the domain sub model in the ontology */
		ONTOLOGY_DOMAIN_BASE_CONCEPT_URI ("ontology.domain.baseconceptURI", String.class, true),
		
		/** uri of the base concept of the problem sub model in the ontology */
		ONTOLOGY_DOMAIN_BASE_PROBLEM_URI ("ontology.problem.baseconceptURI", String.class, true),
		
		/** Semantic similarity measure */
		ONTOLOGY_SIMILARITY_MEASURE ("ontology.similarity.measure", String.class, true, "simpack.measure.graph.ConceptualSimilarity"),
		
		/** maximal length of compound terms that can be looked up in the ontology index */
		ONTOLOGY_INDEX_COMPOUND_TERMS ("ontology.index.compoundterms.maxlength", Integer.class, true, 5),
		
		/** The language of the target wiki */
		LANGUAGE ("language", String.class, true, "en"),
		
		/** Classpath relative path to the stopword list */
		STOPWORDS_FILEPATH ("analyzer.stopwords.filepath", String.class, true, "/resources/wordlists/stopWords_en"),
		
		/** 
		 * Boolean property indicating whether all words in the text should be
		 * analyzed (pos tagged and matched to ontology concepts) or not. If not,
		 * only category names, document titles and section titles are analyzed. 
		 */
		ANALYSE_ALL_WORDS ("analyzer.tagallwords", Boolean.class, true, false),

		/** The minimum similarity between two topic classes such that they are still deemed similar. */
		ONTOLOGY_MIN_TOPIC_SIMILARITY ("ontology.minTopicSimilarity", Double.class, true, .5),
		
		/** The value c > 1 in the term 1-c^(-x), determining the rate by which authors gain credibility
		when their contributions sustain a revision. */
		CREDIBILITY_BASE ("credibility.base", Double.class, true, 1.5),
		
		/** The db url to the OpenThesaurus database (for german wikis) */
		OPENTHESAURUS_DB_URL ("openthesaurus.db.url", String.class, false),
		
		/** User for the OpenThesaurus database (for german wikis) */
		OPENTHESAURUS_DB_USER ("openthesaurus.db.user", String.class, false),

		/** Password for the OpenThesaurus database (for german wikis) */
		OPENTHESAURUS_DB_PASSWORD ("openthesaurus.db.password", String.class, false),
		
		/** Classpath relative path to the wordnet dictionary */
		WORDNET_DATA_DIR ("wordnet.dictionaryDir", String.class, false),
		
		EXPERTISE_WEIGHT_TOPIC_CREATION ("expertise.weight.topic_creation", Integer.class, true),
		EXPERTISE_WEIGHT_WORD_TOPIC_MATCH ("expertise.weight.word_topic_match", Integer.class, true),
		EXPERTISE_WEIGHT_CATEGORY_CONTRIBUTION ("expertise.weight.category_contribution", Integer.class, true),
		EXPERTISE_WEIGHT_DOCUMENT_CONTRIBUTION ("expertise.weight.document_contribution", Integer.class, true),
		EXPERTISE_WEIGHT_SECTION_1_CONTRIBUTION ("expertise.weight.section_1_contribution", Integer.class, true),
		EXPERTISE_WEIGHT_SECTION_2_CONTRIBUTION ("expertise.weight.section_2_contribution", Integer.class, true),
		EXPERTISE_WEIGHT_SECTION_3_CONTRIBUTION ("expertise.weight.section_3_contribution", Integer.class, true),
		EXPERTISE_WEIGHT_SECTION_4_CONTRIBUTION ("expertise.weight.section_4_contribution", Integer.class, true),
		EXPERTISE_WEIGHT_SECTION_5_CONTRIBUTION ("expertise.weight.section_5_contribution", Integer.class, true),
		EXPERTISE_WEIGHT_SECTION_6_CONTRIBUTION ("expertise.weight.section_6_contribution", Integer.class, true)

		;
		
		private Class<? extends Object> type;
		private Object defaultValue;
		private String name;
		private boolean mandatory;
		
		private Key(String key, Class<? extends Object> type, boolean mandatory, Object defaultValue) {
			this.name = key;
			this.type = type;
			this.defaultValue = defaultValue;
			this.mandatory = mandatory;
		}

		private Key(String key, Class<? extends Object> type, boolean mandatory) {
			this(key, type, mandatory, null);
		}
		
		/** Returns the actual name of the property */
		public String getName() {
			return name;
		}
	}
	
	/** 
	 * Boolean property indicating whether all words in the text should be
	 * analyzed (pos tagged and matched to ontology concepts) or not. If not,
	 * only category names, document titles and section titles are analyzed. 
	 */
	public static final String ANALYSE_ALL_WORDS = "analyzer.tagallwords";

	private static Properties applicationProperties;


	/**
	 * Loads the properties from an input stream to the property hash table.
	 * 
	 * @param is
	 *            input stream to be loaded
	 * @param optional
	 *            if <code>true</code> a missing file will be reported as a
	 *            warning, otherwise it is reported as an error.
	 */
	public static void read(InputStream is) {
		Properties tmpProp = new Properties();
		if (is != null) {
			try {
				tmpProp.load(is);
			} catch (IOException e) {
				log.error("ERROR loading application property file ", e);
			}
			getAppProperties().putAll(tmpProp);
			log.debug("** " + tmpProp.size() + " properties loaded.");
		}
	}
	
	/**
	 * Performs a check 
	 */
	private void check() {
		EnumSet<Key> keys = EnumSet.allOf(Key.class);
		for (Key key : keys) {
			String value = applicationProperties.getProperty(key.name);
			
			// check if entry is present
			if (value == null) {
				if (key.defaultValue != null) {
					log.warn("The entry " + key.name + " is missing in the application properties file. Using default value " + key.defaultValue);
					applicationProperties.setProperty(key.name, key.defaultValue.toString());
				} else {
					log.error("The entry " + key.name + " is missing in the application properties file.");
				}
			} else {
				// rough type check
				Constructor<? extends Object> constructor;
				try {
					constructor = key.type.getConstructor(String.class);
				} catch (Exception e) {
					log.error("Could not perform type check on property file entry with key " + key.name);
					continue;
				}
				
				try {
					constructor.newInstance(value);
				} catch (IllegalArgumentException e) {
					log.error("Illegal type declared for properties file key " + key.name);
				} catch (InstantiationException e) {
					log.error("Illegal type declared for properties file key " + key.name);
				} catch (IllegalAccessException e) {
					log.error("Illegal type declared for properties file key " + key.name);
				} catch (InvocationTargetException e) {
					log.error("The entry " + key.name + " must be of type " + key.type.getName() + ".");
				}
			}
		}
	}

	/**
	 * Return the value of property <code>name</code>. If necessary the property
	 * file is read.
	 * 
	 * @param key
	 * @return value of property <code>name</code>
	 */
	public static String getAppProperty(Key key) {
		if (applicationProperties == null) {
			log.error("No application properties.");
			return "";
		}
		String value = applicationProperties.getProperty(key.name);
		if (value == null) {
			log.error("Property not found: " + key.name);
		}
		return value;
	}

	/**
	 * @see #getAppProperty(String)
	 */
	public static int getIntAppProperty(Key key) {
		String value = applicationProperties.getProperty(key.name);
		if (value == null) {
			log.error("Property not found: " + key.name);
		}
		return Integer.valueOf(value);
	}

	/**
	 * Discards all property definitions read from the property file of the
	 * application.
	 * 
	 */
	public static void discardAppProperties() {
		applicationProperties.clear();
		applicationProperties = null;
	}

	/**
	 * Return all application properties.
	 */
	public static Properties getAppProperties() {
		if (applicationProperties == null) {
			applicationProperties = new Properties();
		}
		return applicationProperties;
	}

	/**
	 * Initializes the application config externally (for testing).
	 */
	public static void setAppProperties(Properties p) {
		applicationProperties = p;
	}

	public static boolean getBooleanAppProperty(Key key) {
		String value = applicationProperties.getProperty(key.name);
		if (value == null) {
			log.error("Property not found: " + key.name);
		}
		return Boolean.valueOf(applicationProperties.getProperty(key.name));
	}

	public static double getDoubleAppProperty(Key key) {
		String value = applicationProperties.getProperty(key.name);
		if (value == null) {
			log.error("Property not found: " + key.name);
		}
		return Float.parseFloat(applicationProperties.getProperty(key.name));
	}

}
