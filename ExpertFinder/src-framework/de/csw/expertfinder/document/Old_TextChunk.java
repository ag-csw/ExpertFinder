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

import java.util.Comparator;

/**
 * This class represents a chunk of text
 * @deprecated Use the interface TextChunk and its implementors instead.
 * @author ralph
 *
 */
public class Old_TextChunk {
	
	public enum TYPE {Word, Sentence, Chunk}
	
	private int chunkId;
	private int authorId;
	private int articleId;
	private int revisionId;
	private int firstRevisionId;

	private int wordCount;
	
	
	private String text;
	private int hashCode;
	private int startPos;
	private int endPos;

	public static final Comparator<Old_TextChunk> TEXT = new Comparator<Old_TextChunk>() {
		public int compare(Old_TextChunk token1, Old_TextChunk token2) {
			return token1.text.compareTo(token2.text);
		}
	};
	
	public static final Comparator<Old_TextChunk> POSITION = new Comparator<Old_TextChunk>() {
		public int compare(Old_TextChunk token1, Old_TextChunk token2) {
			return token1.startPos - token2.startPos;
		}
	};
	
	public Old_TextChunk(String text, int startPos, int endPos) {
		this.text = text;
		this.hashCode = text.hashCode();
		this.startPos = startPos;
		this.endPos = endPos;
	}

	public String getText() {
		return text;
	}

	public int getStartPos() {
		return startPos;
	}

	public int getEndPos() {
		return endPos;
	}

	@Override
	public String toString() {
		return text;
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Old_TextChunk && obj.hashCode() == hashCode;
	}

	public int getChunkId() {
		return chunkId;
	}

	public void setChunkId(int chunkId) {
		this.chunkId = chunkId;
	}

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public int getArticleId() {
		return articleId;
	}

	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}

	public int getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(int revisionId) {
		this.revisionId = revisionId;
	}

	public int getFirstRevisionId() {
		return firstRevisionId;
	}

	public void setFirstRevisionId(int firstRevisionId) {
		this.firstRevisionId = firstRevisionId;
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}
	
	
}

