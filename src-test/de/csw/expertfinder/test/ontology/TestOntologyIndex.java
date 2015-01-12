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
package de.csw.expertfinder.test.ontology;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.hp.hpl.jena.ontology.OntClass;

import de.csw.expertfinder.config.Config;
import de.csw.expertfinder.ontology.OntologyIndex;

/**
 * Tests the ontology index.
 * @author ralph
 */
public class TestOntologyIndex {
	
	@BeforeClass
	public void setUp() {
		Config.read(TestOntologyIndex.class.getResourceAsStream("/conf/ExpertFinder.properties"));
		OntologyIndex.get().load(TestOntologyIndex.class.getResource("test.owl"));
	}
	
	@Test
	public void testOntologyIndex() {
		OntologyIndex oi = OntologyIndex.get();
		
		int maxTermSize = oi.getMaximumCompoundTermSize();
		
		HashMap<String, OntClass> expected = new HashMap<String, OntClass>();		
		HashMap<String, OntClass> actual   = new HashMap<String, OntClass>();

		OntClass c1 = oi.getModel().getOntClass("http://www.csw.de/ontology/test#semantic_collaboration");
		OntClass c2 = oi.getModel().getOntClass("http://www.csw.de/ontology/test#ontology_engineering");
		expected.put("corporate semantic collaboration", c1);
		expected.put("Corporate Ontology Engineering", c2);
		expected.put("Ontology Engineering Corporate Environments", c2);
		
		String sentence1 = "In corporate semantic collaboration we research methods and tools to model knowledge collaboratively and share it in a company. Current tools support collaboration only on selected datatypes, e.g., group calendar, and files, e.g., document archive. An exchange of data between different applications is cumbersome and difficult. To address this problem we employ ontologies as a flexible, scalable, and cost-effective means for integrating data. We look at the following four topics. In Corporate Ontology Engineering we examine Ontology Engineering in Corporate Environments";
		
		List<String> words = Arrays.asList(sentence1.split(OntologyIndex.DELIMITER_PATTERN_STRING));
		LinkedList<String> queue = new LinkedList<String>();
		
		Iterator<String> wordIter = words.iterator();
		// prefill queue
		for(int i=0; i<maxTermSize && wordIter.hasNext(); i++) {
			queue.add(wordIter.next());
		}
		
		while (wordIter.hasNext()) {
			String word = wordIter.next();
			if (oi.isStopWord(word)) {
				continue;
			}
			
			// entail word in queue
			queue.add(word);
			// and remove head
			queue.remove();
			
			checkAndAddResult(oi, queue, actual);
			
		}

		// handle the rest of the queue
		while(queue.size() > 1) {
			queue.remove();
			checkAndAddResult(oi, queue, actual);
		}
		
		assert(actual.equals(expected));
	}
	
	private void checkAndAddResult(OntologyIndex oi, LinkedList<String> queue, HashMap<String, OntClass> actualResults) {
		Map<OntClass, Integer> result = oi.getClassesMatchingTerms(queue);
		Set<OntClass> keySet = result.keySet();
		for (OntClass ontClass : keySet) {
			int wordCount = result.get(ontClass);
			StringBuilder buf = new StringBuilder();
			for (int i = 0; i <= wordCount; i++) {
				buf.append(queue.get(i));
				buf.append(' ');
			}
			actualResults.put(buf.substring(0, buf.length()-1), ontClass);
		}
	}
}
