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
/* First created by JCasGen Sat May 09 19:15:26 CEST 2009 */
package de.csw.expertfinder.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


import org.apache.uima.jcas.cas.StringArray;


/** Denotes an ontological concept
 * Updated by JCasGen Thu Aug 06 10:59:23 CEST 2009
 * XML source: /Users/ralph/uima_workspace/ExpertFinder/desc/MediaWikiExpertFinderAEAggregate.xml
 * @generated */
public class OntologyConcept extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(OntologyConcept.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected OntologyConcept() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public OntologyConcept(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public OntologyConcept(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public OntologyConcept(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
 
    
  //*--------------*
  //* Feature: conceptURIs

  /** getter for conceptURIs - gets 
   * @generated */
  public StringArray getConceptURIs() {
    if (OntologyConcept_Type.featOkTst && ((OntologyConcept_Type)jcasType).casFeat_conceptURIs == null)
      jcasType.jcas.throwFeatMissing("conceptURIs", "de.csw.expertfinder.uima.types.OntologyConcept");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((OntologyConcept_Type)jcasType).casFeatCode_conceptURIs)));}
    
  /** setter for conceptURIs - sets  
   * @generated */
  public void setConceptURIs(StringArray v) {
    if (OntologyConcept_Type.featOkTst && ((OntologyConcept_Type)jcasType).casFeat_conceptURIs == null)
      jcasType.jcas.throwFeatMissing("conceptURIs", "de.csw.expertfinder.uima.types.OntologyConcept");
    jcasType.ll_cas.ll_setRefValue(addr, ((OntologyConcept_Type)jcasType).casFeatCode_conceptURIs, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for conceptURIs - gets an indexed value - 
   * @generated */
  public String getConceptURIs(int i) {
    if (OntologyConcept_Type.featOkTst && ((OntologyConcept_Type)jcasType).casFeat_conceptURIs == null)
      jcasType.jcas.throwFeatMissing("conceptURIs", "de.csw.expertfinder.uima.types.OntologyConcept");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((OntologyConcept_Type)jcasType).casFeatCode_conceptURIs), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((OntologyConcept_Type)jcasType).casFeatCode_conceptURIs), i);}

  /** indexed setter for conceptURIs - sets an indexed value - 
   * @generated */
  public void setConceptURIs(int i, String v) { 
    if (OntologyConcept_Type.featOkTst && ((OntologyConcept_Type)jcasType).casFeat_conceptURIs == null)
      jcasType.jcas.throwFeatMissing("conceptURIs", "de.csw.expertfinder.uima.types.OntologyConcept");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((OntologyConcept_Type)jcasType).casFeatCode_conceptURIs), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((OntologyConcept_Type)jcasType).casFeatCode_conceptURIs), i, v);}
  }

    
