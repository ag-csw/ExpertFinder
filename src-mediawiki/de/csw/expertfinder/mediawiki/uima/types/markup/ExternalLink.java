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
/* First created by JCasGen Wed Apr 15 16:11:42 CEST 2009 */
package de.csw.expertfinder.mediawiki.uima.types.markup;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** An external link.
 * Updated by JCasGen Fri Sep 25 16:45:43 CEST 2009
 * XML source: /Users/ralph/uima_workspace/ExpertFinder/desc/FastMediaWikiMarkupDetector.xml
 * @generated */
public class ExternalLink extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(ExternalLink.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ExternalLink() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ExternalLink(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ExternalLink(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ExternalLink(JCas jcas, int begin, int end) {
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
  //* Feature: destination

  /** getter for destination - gets The external link's destination,
   * @generated */
  public String getDestination() {
    if (ExternalLink_Type.featOkTst && ((ExternalLink_Type)jcasType).casFeat_destination == null)
      jcasType.jcas.throwFeatMissing("destination", "de.csw.expertfinder.mediawiki.uima.types.markup.ExternalLink");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ExternalLink_Type)jcasType).casFeatCode_destination);}
    
  /** setter for destination - sets The external link's destination, 
   * @generated */
  public void setDestination(String v) {
    if (ExternalLink_Type.featOkTst && ((ExternalLink_Type)jcasType).casFeat_destination == null)
      jcasType.jcas.throwFeatMissing("destination", "de.csw.expertfinder.mediawiki.uima.types.markup.ExternalLink");
    jcasType.ll_cas.ll_setStringValue(addr, ((ExternalLink_Type)jcasType).casFeatCode_destination, v);}    
  }

    
