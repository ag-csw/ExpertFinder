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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import de.csw.expertfinder.uima.types.Sentence;

/**
 * This class builds on top of the GATE regex based sentence splitter
 * @author ralph
 */
public class GATERegexSentenceSplitter extends JCasAnnotator_ImplBase {
	
	private static final Logger log = Logger.getLogger(GATERegexSentenceSplitter.class);

	/**
	 * Encoding used when reading config files
	 */
	private static final String encoding = "UTF-8";

	/**
	 * URL pointing to a file with regex patterns for internal sentence splits.
	 */
	private static final URL internalSplitListURL = GATERegexSentenceSplitter.class.getResource("internal-split-patterns.txt");

	/**
	 * URL pointing to a file with regex patterns for external sentence splits.
	 */
	private static final URL externalSplitListURL = GATERegexSentenceSplitter.class.getResource("external-split-patterns.txt");

	/**
	 * URL pointing to a file with regex patterns for non sentence splits.
	 */
	private static final URL nonSplitListURL = GATERegexSentenceSplitter.class.getResource("non-split-patterns.txt");

	private static final Pattern internalSplitsPattern;

	private static final Pattern externalSplitsPattern;

	private static final Pattern nonSplitsPattern;

	static {
		Pattern internalSplitsPatternTmp = null;
		Pattern externalSplitsPatternTmp = null;
		Pattern nonSplitsPatternTmp = null;
		try {
			// load the known abbreviations list
			internalSplitsPatternTmp = compilePattern(internalSplitListURL, encoding);
			externalSplitsPatternTmp = compilePattern(externalSplitListURL, encoding);
			nonSplitsPatternTmp = compilePattern(nonSplitListURL, encoding);
		} catch (UnsupportedEncodingException e) {
			log.error("Could not load sentence detection inclusion/exlusion patterns from pattern list. The GATERegexSentenceSplitter analysis component will not be available!", e);
		} catch (IOException e) {
			log.error("Could not load sentence detection inclusion/exlusion patterns from pattern list. The GATERegexSentenceSplitter analysis component will not be available!", e);
		} finally {
			internalSplitsPattern = internalSplitsPatternTmp;
			externalSplitsPattern = externalSplitsPatternTmp;
			nonSplitsPattern = nonSplitsPatternTmp;
		}
	}

	private static Pattern compilePattern(URL paternsListUrl, String encoding) throws UnsupportedEncodingException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(paternsListUrl.openStream(), encoding));
		StringBuffer patternString = new StringBuffer();

		String line = reader.readLine();
		while (line != null) {
			line = line.trim();

			if (line.length() == 0 || line.startsWith("//")) {
				// ignore empty lines and comments
			} else {
				if (patternString.length() > 0)
					patternString.append("|");
				patternString.append("(?:" + line + ")");
			}
			// move to next line
			line = reader.readLine();
		}
		if (log.isDebugEnabled()) {
			log.debug("Pattern from url " + paternsListUrl + ": " + patternString);
		}
		return Pattern.compile(patternString.toString());
	}

	// protected enum StartEnd {START, END};

	/**
	 * A comparator for MatchResult objects. This is used to find the next match
	 * result in a text. A null value is used to signify that no more matches
	 * are available, hence nulls are the largest value, according to this
	 * comparator.
	 * 
	 * @author Valentin Tablan (valyt)
	 */
	private class MatchResultComparator implements Comparator<MatchResult> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(MatchResult o1, MatchResult o2) {
			if (o1 == null && o2 == null)
				return 0;
			if (o1 == null)
				return 1;
			if (o2 == null)
				return -1;
			// at this point both match results are not null
			return o1.start() - o2.start();
		}
	}

	/**
	 * This analysis component detects sentence boundaries using the GATE regex sentence splitter, which has been
	 * adopted to work inside UIMA without the GATE-UIMA bridge.
	 * This sentence splitter is more performant and yields better results than other tested statistical sentence splitters.
	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {

		String docText = cas.getDocumentText();

		/*
		 * If the document's content is empty or contains only whitespace, we
		 * drop out right here, since there's nothing to sentence-split.
		 */
		if (docText.trim().length() < 1) {
			return;
		}

		Matcher internalSplitMatcher = internalSplitsPattern.matcher(docText);
		Matcher externalSplitMatcher = externalSplitsPattern.matcher(docText);

		Matcher nonSplitMatcher = nonSplitsPattern.matcher(docText);
		// store all non split locations in a list of pairs
		List<int[]> nonSplits = new LinkedList<int[]>();
		while (nonSplitMatcher.find()) {
			nonSplits.add(new int[] { nonSplitMatcher.start(), nonSplitMatcher.end() });
			if (log.isDebugEnabled()) {
				log.debug("Non-split match: " + nonSplitMatcher.group());
			}
		}
		// this lists holds the next matches at each step
		List<MatchResult> nextSplitMatches = new ArrayList<MatchResult>();
		// initialise matching process
		MatchResult internalMatchResult = null;
		if (internalSplitMatcher.find()) {
			internalMatchResult = internalSplitMatcher.toMatchResult();
			nextSplitMatches.add(internalMatchResult);
			if (log.isDebugEnabled()) {
				log.debug("Internal split match: " + internalMatchResult.group());
			}

		}
		MatchResult externalMatchResult = null;
		if (externalSplitMatcher.find()) {
			externalMatchResult = externalSplitMatcher.toMatchResult();
			nextSplitMatches.add(externalMatchResult);
			if (log.isDebugEnabled()) {
				log.debug("External split match: " + externalMatchResult.group());
			}

		}
		MatchResultComparator comparator = new MatchResultComparator();
		int lastSentenceEnd = 0;

		while (!nextSplitMatches.isEmpty()) {
			// see which one matches first
			Collections.sort(nextSplitMatches, comparator);
			MatchResult nextMatch = nextSplitMatches.remove(0);
			if (nextMatch == internalMatchResult) {
				// we have a new internal split; see if it's vetoed or not
				if (!veto(nextMatch, nonSplits)) {
					// split is not vetoed
					try {
						// add the split annotation
						// FeatureMap features = Factory.newFeatureMap();
						// features.put("kind", "internal");
						// outputAS.add(new Long(nextMatch.start()), new
						// Long(nextMatch.end()),
						// "Split", features);
						// generate the sentence annotation
						int endOffset = nextMatch.end();
						// find the first non whitespace character starting from
						// where the
						// last sentence ended
						while (lastSentenceEnd < endOffset && Character.isWhitespace(Character.codePointAt(docText, lastSentenceEnd))) {
							lastSentenceEnd++;
						}
						// if there is any useful text between the two offsets,
						// generate
						// a new sentence
						if (lastSentenceEnd < nextMatch.start()) {
							// outputAS.add(new Long(lastSentenceEnd), new
							// Long(endOffset),
							// ANNIEConstants.SENTENCE_ANNOTATION_TYPE,
							// Factory.newFeatureMap());
							Sentence annotation = new Sentence(cas);
							annotation.setBegin(lastSentenceEnd);
							annotation.setEnd(endOffset);
							annotation.addToIndexes();
						}
						// store the new sentence end
						lastSentenceEnd = endOffset;
					} catch (Exception e) {
						// this should never happen
						throw new AnalysisEngineProcessException(e);
					}
				} else {
					if (log.isDebugEnabled()) {
						log.debug("Vetoed internal match: " + nextMatch.group());
					}

				}
				// prepare for next step
				if (internalSplitMatcher.find()) {
					internalMatchResult = internalSplitMatcher.toMatchResult();
					nextSplitMatches.add(internalMatchResult);
				} else {
					internalMatchResult = null;
				}
			} else if (nextMatch == externalMatchResult) {
				// we have a new external split; see if it's vetoed or not
				if (!veto(nextMatch, nonSplits)) {
					// split is not vetoed
					try {
						// generate the split
						// FeatureMap features = Factory.newFeatureMap();
						// features.put("kind", "external");
						// outputAS.add(new Long(nextMatch.start()), new
						// Long(nextMatch.end()),
						// "Split", features);
						// generate the sentence annotation
						// find the last non whitespace character, going
						// backward from
						// where the external skip starts
						int endOffset = nextMatch.start();
						while (endOffset > lastSentenceEnd && Character.isSpaceChar(Character.codePointAt(docText, endOffset - 1))) {
							endOffset--;
						}
						// find the first non whitespace character starting from
						// where the
						// last sentence ended
						while (lastSentenceEnd < endOffset && Character.isSpaceChar(Character.codePointAt(docText, lastSentenceEnd))) {
							lastSentenceEnd++;
						}
						// if there is any useful text between the two offsets,
						// generate
						// a new sentence
						if (lastSentenceEnd < endOffset) {
							// outputAS.add(new Long(lastSentenceEnd), new
							// Long(endOffset),
							// ANNIEConstants.SENTENCE_ANNOTATION_TYPE,
							// Factory.newFeatureMap());
							Sentence annotation = new Sentence(cas);
							annotation.setBegin(lastSentenceEnd);
							annotation.setEnd(endOffset);
							annotation.addToIndexes();
						}
						// store the new sentence end
						lastSentenceEnd = nextMatch.end();
					} catch (Exception e) {
						// this should never happen
						throw new AnalysisEngineProcessException(e);
					}
				} else {
					if (log.isDebugEnabled()) {
						log.debug("Vetoed internal match: " + nextMatch.group());
					}

				}
				// prepare for next step
				if (externalSplitMatcher.find()) {
					externalMatchResult = externalSplitMatcher.toMatchResult();
					nextSplitMatches.add(externalMatchResult);
				} else {
					externalMatchResult = null;
				}
			} else {
				// malfunction
				throw new AnalysisEngineProcessException("Invalid state - cannot identify match!", null);
			}
		}// while(!nextMatches.isEmpty()){
	}

	/**
	 * Checks whether a possible match is being vetoed by a non split match. A
	 * possible match is vetoed if it any nay overlap with a veto region.
	 * 
	 * @param split
	 *            the match result representing the split to be tested
	 * @param vetoRegions
	 *            regions where matches are not allowed. For efficiency reasons,
	 *            this method assumes these regions to be non overlapping and
	 *            sorted in ascending order. All veto regions that end before
	 *            the proposed match are also discarded (again for efficiency
	 *            reasons). This requires the proposed matches to be sent to
	 *            this method in ascending order, so as to avoid malfunctions.
	 * @return <tt>true</tt> iff the proposed split should be ignored
	 */
	private boolean veto(MatchResult split, List<int[]> vetoRegions) {
		// if no more non splits available, accept everything
		for (Iterator<int[]> vetoRegIter = vetoRegions.iterator(); vetoRegIter.hasNext();) {
			int[] aVetoRegion = vetoRegIter.next();
			if (aVetoRegion[1] - 1 < split.start()) {
				// current veto region ends before the proposed split starts
				// --> discard the veto region
				vetoRegIter.remove();
			} else if (split.end() - 1 < aVetoRegion[0]) {
				// veto region starts after the split ends
				// -> we can return false
				return false;
			} else {
				// we have overlap
				return true;
			}
		}
		// if we got this far, all veto regions are before the split
		return false;
	}

}
