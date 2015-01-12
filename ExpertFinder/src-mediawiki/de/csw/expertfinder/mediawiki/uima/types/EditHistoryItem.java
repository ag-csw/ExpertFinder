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
/* First created by JCasGen Mon Mar 16 10:04:48 CET 2009 */
package de.csw.expertfinder.mediawiki.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSList;


/** 
 * Updated by JCasGen Mon Mar 16 10:04:48 CET 2009
 * @generated */
public class EditHistoryItem extends FSList {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(EditHistoryItem.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected EditHistoryItem() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public EditHistoryItem(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public EditHistoryItem(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
 
    
  //*--------------*
  //* Feature: userName

  /** getter for userName - gets 
   * @generated */
  public String getUserName() {
    if (MediaWikiHistory_Type.featOkTst && ((MediaWikiHistory_Type)jcasType).casFeat_userName == null)
      jcasType.jcas.throwFeatMissing("userName", "de.csw.uima.mediawiki.types.MediaWikiHistory");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MediaWikiHistory_Type)jcasType).casFeatCode_userName);}
    
  /** setter for userName - sets  
   * @generated */
  public void setUserName(String v) {
    if (MediaWikiHistory_Type.featOkTst && ((MediaWikiHistory_Type)jcasType).casFeat_userName == null)
      jcasType.jcas.throwFeatMissing("userName", "de.csw.uima.mediawiki.types.MediaWikiHistory");
    jcasType.ll_cas.ll_setStringValue(addr, ((MediaWikiHistory_Type)jcasType).casFeatCode_userName, v);}    
  }

    
