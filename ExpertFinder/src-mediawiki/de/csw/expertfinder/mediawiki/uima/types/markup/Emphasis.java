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


/** Any kind of markup that emphasises parts of the text (e.g. bold, italics)
 * Updated by JCasGen Sat May 09 17:30:30 CEST 2009
 * XML source: /Users/ralph/uima_workspace/ExpertFinder/desc/MediaWikiExpertFinderAEAggregate.xml
 * @generated */
public class Emphasis extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Emphasis.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Emphasis() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Emphasis(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Emphasis(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Emphasis(JCas jcas, int begin, int end) {
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
  //* Feature: text

  /** getter for text - gets The emphasised text.
   * @generated */
  public String getText() {
    if (Emphasis_Type.featOkTst && ((Emphasis_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "de.csw.expertfinder.mediawiki.uima.types.markup.Emphasis");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Emphasis_Type)jcasType).casFeatCode_text);}
    
  /** setter for text - sets The emphasised text. 
   * @generated */
  public void setText(String v) {
    if (Emphasis_Type.featOkTst && ((Emphasis_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "de.csw.expertfinder.mediawiki.uima.types.markup.Emphasis");
    jcasType.ll_cas.ll_setStringValue(addr, ((Emphasis_Type)jcasType).casFeatCode_text, v);}    
  }

    
