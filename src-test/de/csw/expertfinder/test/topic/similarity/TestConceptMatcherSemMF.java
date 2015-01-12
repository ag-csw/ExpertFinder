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
package de.csw.expertfinder.test.topic.similarity;

import java.io.FileNotFoundException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import de.csw.expertfinder.ontology.OntologyIndex;

/**
 * @author ralph
 *
 */
public class TestConceptMatcherSemMF {
	
	/**
	 * @throws FileNotFoundException 
	 */
	@BeforeClass
	public void setUp() throws FileNotFoundException {
		OntologyIndex.get().load(TestConceptMatcher.class.getResource("/resources/ontology/jura.owl"));
	}
	
	/**
	 * 
	 */
	@AfterClass
	public void tearDown() {
		System.out.println("tearDown");
	}
	
	/**
	 * 
	 */
	/*
	@Test
	private void testSimilarity() {
		Model model = OntologyIndex.get().getModel();
		Resource class1 = model.getResource("http://www.ag-csw.de/ontologies/jura#Urlaubsgeld");
		Resource class2 = model.getResource("http://www.ag-csw.de/ontologies/jura#Schmerzensgeld");
		
		Model m = ModelFactory.createDefaultModel();
		
		// build the graph matching description
		Resource gmd = m.createResource();
		gmd.addProperty(RDF.type, MD.GraphMatchingDescription);
		
		String queryModelURL = TestConceptMatcher.class.getResource("/resources/ontology/jura.owl").toExternalForm();
		
		gmd.addProperty(MD.queryModelURL, "file:./doc/examples/jpp.rdf");
		gmd.addProperty(MD.resModelURL, "file:./doc/examples/jps.rdf");
		gmd.addProperty(MD.queryGraphURI, "http://example.org/JobPositionPosting.rdfs#JPP_1");
		gmd.addProperty(MD.resGraphURIpath, "(?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/JobPositionSeeker.rdfs#JobPositionSeeker>)");
		
		// Create bag for node matching descriptions
		Bag nodeMatchingDescriptors = m.createBag();
		gmd.addProperty(MD.hasNodeMatchingDescriptions, nodeMatchingDescriptors);
		
		
		Resource cmd_2 = m.createResource();
		cmd_2.addProperty(RDF.type, MD.ClusterMatchingDescription);		
		cmd_2.addProperty(MD.label, "topics");
		cmd_2.addProperty(MD.weight, "1.00");						
		cmds.add(cmd_2);

		
		
		Bag pmds = m.createBag();
		c2_nmd_1.addProperty(MD.hasPropertyMatchingDescriptions, pmds);
				
		Resource pmd_1 = m.createResource();
		pmds.add(pmd_1);
		pmd_1.addProperty(RDF.type, MD.PropertyMatchingDescription);
		pmd_1.addProperty(MD.label, "skill");
		pmd_1.addProperty(MD.weight, "0.8");
		pmd_1.addProperty(MD.queryPropURI, RDF.type);
		pmd_1.addProperty(MD.resPropURI, RDF.type);
		pmd_1.addProperty(MD.reverseMatching, "false");
		
		// add a taxonomic matcher
		Resource tm_1 = m.createResource();
		tm_1.addProperty(RDF.type, MD.TaxonomicMatcher);
		tm_1.addProperty(MD.simInheritance, "true");
		pmd_1.addProperty(MD.useMatcher, tm_1);
				
		Resource taxon_skills = m.createResource();
		taxon_skills.addProperty(RDF.type, MD.Taxonomy);
		taxon_skills.addProperty(MD.taxonomyURL, "file:./doc/examples/it-skills.rdfs");
		taxon_skills.addProperty(MD.rootConceptURI, "http://example.org/it-skills.rdfs#IT_Skills");
		tm_1.addProperty(MD.taxonomy, taxon_skills);
				
		Resource emc = m.createResource();
		emc.addProperty(RDF.type, MD.ExpMilestCalc);
		emc.addProperty(MD.k_factor, "2");
		tm_1.addProperty(MD.useMilestoneCalc, emc);
		
	} */
}
