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
package de.csw.expertfinder.uima;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.csw.expertfinder.uima.types.Noun;
import de.csw.expertfinder.uima.types.Sentence;
import de.csw.expertfinder.uima.types.Word;

/**
 * This UIMA annotation engine class splits a text into tokens. It annotates
 * each token with its position and its POS (only distinguishing between nouns
 * and non-nouns).
 * 
 * The noun recognition algorithm takes advantage of the fact that nouns always
 * begin with a capital letter in the german language. To distinguish between
 * nouns and other pos at the beginning of a sentence, the algorithm uses
 * OpenThesaurus (<a
 * href="http://www.openthesaurus.de/">http://www.openthesaurus.de/</a>), and
 * additionally stores nouns detected in the middle of the sentence in a lookup
 * table for re-use on words at the beginning of a sentence.
 * 
 * @author ralph
 * 
 */
public class GermanTokenizerAndStemmer extends JCasAnnotator_ImplBase {

	private static final String CONFIG_PARAM_GROUP_DB = "db";
	private static final String CONFIG_PARAM_DB_URL = "connectionURL";
	private static final String CONFIG_PARAM_DB_USER = "user";
	private static final String CONFIG_PARAM_DB_PASSWORD = "password";

	private static final String CONFIG_PARAM_GROUP_STOPWORDS = "stopwords";
	private static final String STOPWORD_LIST_PATH = "filePath";

	private static final String PUNCTUATION = "[.!?]";

	private static final Logger log = Logger.getLogger(GermanTokenizerAndStemmer.class);

	private static final HashSet<String> nounCache = new HashSet<String>();

	private static final Pattern wordPattern = Pattern.compile("[\\w\\+\\#[\\p{L}&&[\\P{Alpha}]]]+");
	
	private static final org.apache.lucene.analysis.de.GermanStemmer stemmer = new org.apache.lucene.analysis.de.GermanStemmer();
	
	private final HashSet<String> stopwords = new HashSet<String>();

	/**
	 * Initializes this annotation engine.
	 * @see org.apache.uima.analysis_component.AnalysisComponent_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		String dbURL = (String) context.getConfigParameterValue(CONFIG_PARAM_GROUP_DB, CONFIG_PARAM_DB_URL);
		String dbUser = (String) context.getConfigParameterValue(CONFIG_PARAM_GROUP_DB, CONFIG_PARAM_DB_USER);
		String dbPassword = (String) context.getConfigParameterValue(CONFIG_PARAM_GROUP_DB, CONFIG_PARAM_DB_PASSWORD);
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT stem FROM words WHERE noun=1");
			while (rs.next()) {
				nounCache.add(rs.getString(1));
			}
		} catch (ClassNotFoundException e) {
			log.fatal("No mysql jdbc driver in the classpath", e);
			throw new ResourceInitializationException(e);
		} catch (SQLException e) {
			log.fatal("Could not establish DB connection", e);
			throw new ResourceInitializationException(e);
		}

		String stopWordListPath = (String) context.getConfigParameterValue(CONFIG_PARAM_GROUP_STOPWORDS, STOPWORD_LIST_PATH);
		File stopWordListFile = new File(stopWordListPath);
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(stopWordListFile));
			String line = null;
			while((line = in.readLine()) != null) {
				stopwords.add(line.trim().toLowerCase());
			}
		} catch (IOException e) {
			log.fatal("Could not load stopword list file " + stopWordListFile.getAbsolutePath(), e);
			throw new ResourceInitializationException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.warn("Could not load stopword list file " + stopWordListFile.getAbsolutePath(), e);
					// not fatal, let's keep on with our lives.  
				}
			}
		}
	}

	/**
	 * Splits the given cas document text into tokens and annotates each token
	 * with its position and its POS (only distinguishing between nouns and
	 * non-nouns). A noun is marked with a noun annotation, each non-noun is
	 * marked with a word annotation.
	 * 
	 * @see org.apache.uima.analysis_component.CasAnnotator_ImplBase#process(org.apache.uima.cas.CAS)
	 */
	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		String text = cas.getDocumentText();
		AnnotationIndex sentenceIndex = cas.getAnnotationIndex(Sentence.type);
		FSIterator sentenceIterator = sentenceIndex.iterator();
		int currentSentenceEnd = 0;
		Matcher matcher = wordPattern.matcher(text);
		boolean firstWordInSentence = true;
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			if (end > currentSentenceEnd) {
				// we have reached the next sentence
				if (sentenceIterator.hasNext()) {
					currentSentenceEnd = ((Sentence)sentenceIterator.next()).getEnd();
					firstWordInSentence = true;
				}
			} else {
				firstWordInSentence = false;
			}
			String word = matcher.group();
			if (stopwords.contains(word.toLowerCase()))
				continue;
			String wordStem = stemmer.stem(word);
			boolean isNoun = false;
			if (Character.isUpperCase(word.charAt(0))) {
				if (nounCache.contains(wordStem)) {
					isNoun = true;
				} else {
					// check if upper case and not at the beginning of the
					// sentence (sometimes German is a lovable language).
//					if (start > 2) {
//						String context = text.substring(start - 3, start);
//						if (!context.matches(PUNCTUATION)) {
//							isNoun = true;
//							nounCache.add(wordStem);
//						}
//					}
					if (!firstWordInSentence) {
						isNoun = true;
					}
				}
			}
			Word annotation = isNoun ? new Noun(cas, start, end) : new Word(cas, start, matcher.end());
			annotation.setWordStem(wordStem);
			annotation.addToIndexes();
		}
	}
}
