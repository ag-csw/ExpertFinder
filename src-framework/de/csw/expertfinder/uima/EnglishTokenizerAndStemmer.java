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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import de.csw.expertfinder.util.Pair;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord;
import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.DefaultLemmatizer;
import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.Lemmatizer;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.Lexicon;
import edu.northwestern.at.utils.corpuslinguistics.partsofspeech.PartOfSpeechTags;
import edu.northwestern.at.utils.corpuslinguistics.postagger.DefaultPartOfSpeechTagger;
import edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer.DefaultSpellingStandardizer;
import edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer.SpellingStandardizer;
import edu.northwestern.at.utils.corpuslinguistics.stemmer.DefaultStemmer;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.PennTreebankTokenizer;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.WordTokenizer;

/**
 * @author ralph
 * 
 */
public class EnglishTokenizerAndStemmer extends JCasAnnotator_ImplBase {

	private static final String CONFIG_PARAM_GROUP_STOPWORDS = "stopwords";
	private static final String STOPWORD_LIST_PATH = "filePath";

	private static final String PUNCTUATION = "[.!?]";

	private static final Logger log = Logger.getLogger(EnglishTokenizerAndStemmer.class);

	public static final String WORD_PATTERN_STRING = "[\\w\\+\\#[\\p{L}&&[\\P{Alpha}]]]+";
	private static final Pattern wordPattern = Pattern.compile(WORD_PATTERN_STRING);

	private static final String lemmaSeparator = "|";
	
	private static DefaultStemmer stemmer;
	
	private static final HashSet<String> nounTags = new HashSet<String>();
	
	static {
		nounTags.add("n1");
		nounTags.add("n2");
		nounTags.add("np1");
		nounTags.add("fw-la"); // Latin
		nounTags.add("fw-fr"); // French
		nounTags.add("crd");
		nounTags.add("pno12");
		nounTags.add("pns11");
	}

	// Word Adorner Components
	// these (especially the Lexicon) are very memory-demanding and
	// take ages to initialize. That's why we keep them in the static
	// context.
	private static DefaultPartOfSpeechTagger posTagger;
	private static Lemmatizer lemmatizer;
	private static Lexicon wordLexicon;
	private static PartOfSpeechTags partOfSpeechTags;

	private static WordTokenizer spellingTokenizer;

	private static HashSet<String> stopwords;

	/**
	 * @see org.apache.uima.analysis_component.AnalysisComponent_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		
		if (stopwords == null) {
			stopwords = new HashSet<String>();
			// read stop word list
			String stopWordListPath = (String) context.getConfigParameterValue(CONFIG_PARAM_GROUP_STOPWORDS, STOPWORD_LIST_PATH);
			URL stopWordListURL = EnglishTokenizerAndStemmer.class.getResource(stopWordListPath);
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(stopWordListURL.openStream()));
				String line = null;
				while ((line = in.readLine()) != null) {
					stopwords.add(line.trim().toLowerCase());
				}
			} catch (IOException e) {
				log.error("Could not load stopword list file " + stopWordListURL, e);
				throw new ResourceInitializationException(e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						log.warn("Could not load stopword list file " + stopWordListURL, e);
						// not fatal, let's keep on with our lives.
					}
				}
			}

		}

		try {
			if (posTagger == null) {
				log.info("Initializing POS tagger and related components...");
				posTagger = new DefaultPartOfSpeechTagger();
				SpellingStandardizer standardizer = new DefaultSpellingStandardizer();
				lemmatizer = new DefaultLemmatizer();
				lemmatizer.setDictionary(standardizer.getStandardSpellings());
				wordLexicon = posTagger.getLexicon();
				partOfSpeechTags = wordLexicon.getPartOfSpeechTags();
				spellingTokenizer = new PennTreebankTokenizer();
				stemmer = new DefaultStemmer();
				log.info("POS tagger initialization completed.");
			}
		} catch (Exception e) {
			String msg = "Error initializing WordAdorner component";
			log.error(msg, e);
			throw new ResourceInitializationException(e);
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
		AnnotationIndex sentenceIndex = cas.getAnnotationIndex(Sentence.type);
		FSIterator sentenceIterator = sentenceIndex.iterator();
		
		String documentText = cas.getDocumentText();
		
		// the pos tagger needs it this way.
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<Pair<Integer, Integer>> positions = new ArrayList<Pair<Integer,Integer>>();
		
		while (sentenceIterator.hasNext()) {
			// faster than creating a new ArrayList during each iteration
			words.clear();
			positions.clear();
			
			Sentence sentence = (Sentence)sentenceIterator.next();
			int sentenceStart = sentence.getBegin();
			String sentenceText = sentence.getCoveredText();
			
			// detect words in sentence
			Matcher matcher = wordPattern.matcher(sentenceText);
			while (matcher.find()) {
				int wordStart = sentenceStart + matcher.start();
				int wordEnd   = sentenceStart + matcher.end();
				if (!isWikiMarkup(documentText, wordStart, wordEnd)) {
					String word = matcher.group();
					words.add(word);
					positions.add(new Pair<Integer, Integer>(wordStart, wordEnd));
				}
			}
			
			if (!words.isEmpty()) {
				List<AdornedWord> taggedWords = posTagger.tagSentence(words);
				
				Iterator<Pair<Integer, Integer>> posIterator = positions.iterator();
				for (AdornedWord taggedWord : taggedWords) {
					setLemma (
	                    taggedWord ,
	                    wordLexicon ,
	                    lemmatizer ,
	                    partOfSpeechTags ,
	                    spellingTokenizer );
					
					Pair<Integer, Integer> position = posIterator.next();
					
					String lemma = taggedWord.getLemmata();
					if (stopwords.contains(lemma) || stopwords.contains(taggedWord.getToken()))
						continue;
					
					String pos = taggedWord.getPartsOfSpeech();
					Word word;
					if (nounTags.contains(pos)) {
						word = new Noun(cas);
					} else {
						word = new Word(cas);
					}
					
					word.setWordStem(stemmer.stem(taggedWord.getToken()));
					word.setLemma(lemma);
					
					if (log.isDebugEnabled()) {
						log.debug("Annotating word with lemma: " + lemma + " (" + position.getKey() + ", " + position.getValue() + ")");
					}
					
					word.setBegin(position.getKey());
					word.setEnd(position.getValue());
					
					word.addToIndexes();
	
				}
			}
			
		}
		
		

	}

	/**
	 * Lemmatizes the given word. Taken from the WordAdorner examples.
	 * 
	 * @param adornedWord
	 * @param lexicon
	 * @param lemmatizer
	 * @param partOfSpeechTags
	 * @param spellingTokenizer
	 */
	private void setLemma(AdornedWord adornedWord, Lexicon lexicon, Lemmatizer lemmatizer, PartOfSpeechTags partOfSpeechTags, WordTokenizer spellingTokenizer) {
		String spelling = adornedWord.getSpelling();
		String partOfSpeech = adornedWord.getPartsOfSpeech();
		String lemmata = spelling;

		// Get lemmatization word class
		// for part of speech.
		String lemmaClass = partOfSpeechTags.getLemmaWordClass(partOfSpeech);

		// Do not lemmatize words which
		// should not be lemmatized,
		// including proper names.

		if (lemmatizer.cantLemmatize(spelling) || lemmaClass.equals("none")) {
		} else {
			// Try compound word exceptions
			// list first.

			lemmata = lemmatizer.lemmatize(spelling, "compound");

			// If lemma not found, keep trying.

			if (lemmata.equals(spelling)) {
				// Extract individual word parts.
				// May be more than one for a
				// contraction.

				List<String> wordList = spellingTokenizer.extractWords(spelling);

				// If just one word part,
				// get its lemma.

				if (!partOfSpeechTags.isCompoundTag(partOfSpeech) || (wordList.size() == 1)) {
					if (lemmaClass.length() == 0) {
						lemmata = lemmatizer.lemmatize(spelling);
					} else {
						lemmata = lemmatizer.lemmatize(spelling, lemmaClass);
					}
				}
				// More than one word part.
				// Get lemma for each part and
				// concatenate them with the
				// lemma separator to form a
				// compound lemma.
				else {
					lemmata = "";
					String lemmaPiece = "";
					String[] posTags = partOfSpeechTags.splitTag(partOfSpeech);

					if (posTags.length == wordList.size()) {
						for (int i = 0; i < wordList.size(); i++) {
							String wordPiece = (String) wordList.get(i);

							if (i > 0) {
								lemmata = lemmata + lemmaSeparator;
							}

							lemmaClass = partOfSpeechTags.getLemmaWordClass(posTags[i]);

							lemmaPiece = lemmatizer.lemmatize(wordPiece, lemmaClass);

							lemmata = lemmata + lemmaPiece;
						}
					}
				}
			}
		}

		adornedWord.setLemmata(lemmata);
	}
	
	private static final char PREFIX_LINK = '[';
	private static final char POSTFIX_LINK = ':';
	
	/**
	 * Determines if the detected word is part of wiki markup and
	 * hence not relevant.
	 * 
	 * @param text the word to test
	 * @param start the start position of the word in the document text 
	 * @param end the end position of the word in the document text
	 * @return
	 */
	private boolean isWikiMarkup(String text, int start, int end) {
		if (start < 2 || end == text.length() - 1) {
			return false;
		}
		return text.charAt(start-2) == PREFIX_LINK &&
			   text.charAt(start-1) == PREFIX_LINK &&
			   text.charAt(end) == POSTFIX_LINK;
	}
	

}
