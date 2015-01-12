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
/* First created by JCasGen Wed Apr 15 21:28:05 CEST 2009 */
package de.csw.expertfinder.mediawiki.uima.types.markup;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** a MediaWiki section
 * Updated by JCasGen Fri Sep 25 16:45:43 CEST 2009
 * XML source: /Users/ralph/uima_workspace/ExpertFinder/desc/FastMediaWikiMarkupDetector.xml
 * @generated */
public class Section extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Section.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Section() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Section(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Section(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Section(JCas jcas, int begin, int end) {
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
  //* Feature: level

  /** getter for level - gets Describes the level of the heading (valid values are 1-6).
   * @generated */
  public int getLevel() {
    if (Section_Type.featOkTst && ((Section_Type)jcasType).casFeat_level == null)
      jcasType.jcas.throwFeatMissing("level", "de.csw.expertfinder.mediawiki.uima.types.markup.Section");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Section_Type)jcasType).casFeatCode_level);}
    
  /** setter for level - sets Describes the level of the heading (valid values are 1-6). 
   * @generated */
  public void setLevel(int v) {
    if (Section_Type.featOkTst && ((Section_Type)jcasType).casFeat_level == null)
      jcasType.jcas.throwFeatMissing("level", "de.csw.expertfinder.mediawiki.uima.types.markup.Section");
    jcasType.ll_cas.ll_setIntValue(addr, ((Section_Type)jcasType).casFeatCode_level, v);}    
   
    
  //*--------------*
  //* Feature: title

  /** getter for title - gets the title of the section
   * @generated */
  public String getTitle() {
    if (Section_Type.featOkTst && ((Section_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "de.csw.expertfinder.mediawiki.uima.types.markup.Section");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Section_Type)jcasType).casFeatCode_title);}
    
  /** setter for title - sets the title of the section 
   * @generated */
  public void setTitle(String v) {
    if (Section_Type.featOkTst && ((Section_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "de.csw.expertfinder.mediawiki.uima.types.markup.Section");
    jcasType.ll_cas.ll_setStringValue(addr, ((Section_Type)jcasType).casFeatCode_title, v);}    
   
    
  //*--------------*
  //* Feature: parent

  /** getter for parent - gets the parent section, if this is a subsection
   * @generated */
  public Section getParent() {
    if (Section_Type.featOkTst && ((Section_Type)jcasType).casFeat_parent == null)
      jcasType.jcas.throwFeatMissing("parent", "de.csw.expertfinder.mediawiki.uima.types.markup.Section");
    return (Section)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Section_Type)jcasType).casFeatCode_parent)));}
    
  /** setter for parent - sets the parent section, if this is a subsection 
   * @generated */
  public void setParent(Section v) {
    if (Section_Type.featOkTst && ((Section_Type)jcasType).casFeat_parent == null)
      jcasType.jcas.throwFeatMissing("parent", "de.csw.expertfinder.mediawiki.uima.types.markup.Section");
    jcasType.ll_cas.ll_setRefValue(addr, ((Section_Type)jcasType).casFeatCode_parent, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    
