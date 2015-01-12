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

import java.util.ArrayList;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import simpack.accessor.graph.JenaOntologyAccessor;
import simpack.api.impl.AbstractSimilarityMeasure;
import simpack.exception.InvalidElementException;
import simpack.measure.graph.ConceptualSimilarity;
import simpack.util.graph.GraphNode;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;

/**
 * @author ralph
 *
 */
public class TestConceptMatcher {
	
	private JenaOntologyAccessor ontologyAccessor;
	
	/**
	 */
	@BeforeClass
	public void setUp()  {
		
//		Config.read(TestConceptMatcher.class.getResourceAsStream("ExpertFinder.properties"));
		
		ontologyAccessor = new JenaOntologyAccessor(
				"http://www.ag-csw.de/ontologies/test#", 
				TestConceptMatcher.class.getResource("test2.owl").toExternalForm(), 
				"http://www.ag-csw.de/ontologies/test#",
				"http://www.ag-csw.de/ontologies/test#Test", 
				OntModelSpec.OWL_MEM_RDFS_INF);
	}
	
	/**
	 * 
	 */
	@AfterClass
	public void tearDown() {
	}
	
	/**
	 * @throws InvalidElementException 
	 * 
	 */
	@Test
	public void testSimilarity() throws InvalidElementException {
		
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
		model.read(TestConceptMatcher.class.getResourceAsStream("test2.owl"), "");
		
		ArrayList<GraphNode> graphNodes = new ArrayList<GraphNode>();
		
		Filter anonFilter = new Filter() {
			public boolean accept(Object o) {
				return ((OntClass)o).getURI().equals("http://www.w3.org/2002/07/owl#Thing");
			}
		};
		
		
		ExtendedIterator ontClasses = model.listClasses().filterDrop(anonFilter);
		while (ontClasses.hasNext()) {
			OntClass ontClass = (OntClass)ontClasses.next();
			GraphNode graphNode = new GraphNode(ontClass.getURI());
			graphNodes.add(graphNode);
		}

		for (GraphNode graphNode1 : graphNodes) {
			for (GraphNode graphNode2 : graphNodes) {
				AbstractSimilarityMeasure sim = new ConceptualSimilarity(ontologyAccessor, graphNode1, graphNode2);
				assert sim.calculate();
				assert sim.isCalculated();
				System.out.println(sim.getSimilarity() + ": " + model.getOntClass(graphNode1.getLabel()).getLocalName() + " -> " + model.getOntClass(graphNode2.getLabel()).getLocalName());
			}
			
		}
		

	}
	
//	@Test
	public void testGraphAccessor() {
		
	}
}
