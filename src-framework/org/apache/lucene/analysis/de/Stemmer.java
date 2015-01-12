package org.apache.lucene.analysis.de;

/**
 * Interface for stemming algorithm.
 * 
 * @author rheese
 * 
 */
public interface Stemmer {
	/**
	 * Stems the given term to an unique <tt>discriminator</tt>.
	 * 
	 * @param term
	 *            The term that should be stemmed.
	 * @return Discriminator for <tt>term</tt>
	 */
	public String stem(String term);
}
