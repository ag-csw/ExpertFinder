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
package de.csw.expertfinder.tokenization;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.csw.expertfinder.document.Old_TextChunk;

/**
 * This class implements a string tokenizer but returns, along with
 * the tokens, their respective positions inside the tokenized text.
 * @author ralph
 *
 */
public class StringTokenizerWithPositions {

	private ArrayList<Old_TextChunk> tokens;
	private int count = 0;


	public StringTokenizerWithPositions(String text, String delim) {
		text = text.toLowerCase();
		tokens = new ArrayList<Old_TextChunk>();
		delim = Pattern.quote(delim);
		Pattern pattern = Pattern.compile("([^" + delim + "]+?)[" + delim + "]+");
		Matcher matcher = pattern.matcher(text);
		String tokenText;
		int start, end = 0;
		while (matcher.find()) {
			tokenText = matcher.group(1);
			start = matcher.start(1);
			end = matcher.end(1);
			tokens.add(new Old_TextChunk(tokenText, start, end));
		}
		pattern = Pattern.compile("([^" + delim + "]+)");
		matcher = pattern.matcher(text);
		if (matcher.find(end + 1)) {
			tokenText = matcher.group(1);
			start = matcher.start(1);
			end = matcher.end(1);
			tokens.add(new Old_TextChunk(tokenText, start, end));
		}
	}

	public boolean hasNext() {
		return count < tokens.size();
	}

	public Old_TextChunk nextToken() {
		if (hasNext()) {
			return tokens.get(count++);
		}
		throw new NoSuchElementException();
	}

}
