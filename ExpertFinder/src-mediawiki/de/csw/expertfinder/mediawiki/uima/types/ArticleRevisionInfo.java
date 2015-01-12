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
/* First created by JCasGen Thu May 07 11:40:06 CEST 2009 */
package de.csw.expertfinder.mediawiki.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.DocumentAnnotation;


/** Information about the entire document, which in this case represents a revision of a MediaWiki article.
 * Updated by JCasGen Fri Sep 18 01:57:52 CEST 2009
 * XML source: /Users/ralph/uima_workspace/ExpertFinder/desc/GermanStemmerTypeSystem.xml
 * @generated */
public class ArticleRevisionInfo extends DocumentAnnotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(ArticleRevisionInfo.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ArticleRevisionInfo() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ArticleRevisionInfo(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ArticleRevisionInfo(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ArticleRevisionInfo(JCas jcas, int begin, int end) {
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
  //* Feature: authorName

  /** getter for authorName - gets 
   * @generated */
  public String getAuthorName() {
    if (ArticleRevisionInfo_Type.featOkTst && ((ArticleRevisionInfo_Type)jcasType).casFeat_authorName == null)
      jcasType.jcas.throwFeatMissing("authorName", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ArticleRevisionInfo_Type)jcasType).casFeatCode_authorName);}
    
  /** setter for authorName - sets  
   * @generated */
  public void setAuthorName(String v) {
    if (ArticleRevisionInfo_Type.featOkTst && ((ArticleRevisionInfo_Type)jcasType).casFeat_authorName == null)
      jcasType.jcas.throwFeatMissing("authorName", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    jcasType.ll_cas.ll_setStringValue(addr, ((ArticleRevisionInfo_Type)jcasType).casFeatCode_authorName, v);}    
   
    
  //*--------------*
  //* Feature: articleId

  /** getter for articleId - gets 
   * @generated */
  public int getArticleId() {
    if (ArticleRevisionInfo_Type.featOkTst && ((ArticleRevisionInfo_Type)jcasType).casFeat_articleId == null)
      jcasType.jcas.throwFeatMissing("articleId", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    return jcasType.ll_cas.ll_getIntValue(addr, ((ArticleRevisionInfo_Type)jcasType).casFeatCode_articleId);}
    
  /** setter for articleId - sets  
   * @generated */
  public void setArticleId(int v) {
    if (ArticleRevisionInfo_Type.featOkTst && ((ArticleRevisionInfo_Type)jcasType).casFeat_articleId == null)
      jcasType.jcas.throwFeatMissing("articleId", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    jcasType.ll_cas.ll_setIntValue(addr, ((ArticleRevisionInfo_Type)jcasType).casFeatCode_articleId, v);}    
   
    
  //*--------------*
  //* Feature: revisionId

  /** getter for revisionId - gets 
   * @generated */
  public int getRevisionId() {
    if (ArticleRevisionInfo_Type.featOkTst && ((ArticleRevisionInfo_Type)jcasType).casFeat_revisionId == null)
      jcasType.jcas.throwFeatMissing("revisionId", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    return jcasType.ll_cas.ll_getIntValue(addr, ((ArticleRevisionInfo_Type)jcasType).casFeatCode_revisionId);}
    
  /** setter for revisionId - sets  
   * @generated */
  public void setRevisionId(int v) {
    if (ArticleRevisionInfo_Type.featOkTst && ((ArticleRevisionInfo_Type)jcasType).casFeat_revisionId == null)
      jcasType.jcas.throwFeatMissing("revisionId", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    jcasType.ll_cas.ll_setIntValue(addr, ((ArticleRevisionInfo_Type)jcasType).casFeatCode_revisionId, v);}    
   
    
  //*--------------*
  //* Feature: timestamp

  /** getter for timestamp - gets The timestamp of when the revision was created.
   * @generated */
  public long getTimestamp() {
    if (ArticleRevisionInfo_Type.featOkTst && ((ArticleRevisionInfo_Type)jcasType).casFeat_timestamp == null)
      jcasType.jcas.throwFeatMissing("timestamp", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    return jcasType.ll_cas.ll_getLongValue(addr, ((ArticleRevisionInfo_Type)jcasType).casFeatCode_timestamp);}
    
  /** setter for timestamp - sets The timestamp of when the revision was created. 
   * @generated */
  public void setTimestamp(long v) {
    if (ArticleRevisionInfo_Type.featOkTst && ((ArticleRevisionInfo_Type)jcasType).casFeat_timestamp == null)
      jcasType.jcas.throwFeatMissing("timestamp", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    jcasType.ll_cas.ll_setLongValue(addr, ((ArticleRevisionInfo_Type)jcasType).casFeatCode_timestamp, v);}    
   
    
  //*--------------*
  //* Feature: title

  /** getter for title - gets The article/document/page's title
   * @generated */
  public String getTitle() {
    if (ArticleRevisionInfo_Type.featOkTst && ((ArticleRevisionInfo_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ArticleRevisionInfo_Type)jcasType).casFeatCode_title);}
    
  /** setter for title - sets The article/document/page's title 
   * @generated */
  public void setTitle(String v) {
    if (ArticleRevisionInfo_Type.featOkTst && ((ArticleRevisionInfo_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    jcasType.ll_cas.ll_setStringValue(addr, ((ArticleRevisionInfo_Type)jcasType).casFeatCode_title, v);}    
   
    
  //*--------------*
  //* Feature: categories

  /** getter for categories - gets All categories this document (revision) belongs to.
   * @generated */
  public StringArray getCategories() {
    if (ArticleRevisionInfo_Type.featOkTst && ((ArticleRevisionInfo_Type)jcasType).casFeat_categories == null)
      jcasType.jcas.throwFeatMissing("categories", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ArticleRevisionInfo_Type)jcasType).casFeatCode_categories)));}
    
  /** setter for categories - sets All categories this document (revision) belongs to. 
   * @generated */
  public void setCategories(StringArray v) {
    if (ArticleRevisionInfo_Type.featOkTst && ((ArticleRevisionInfo_Type)jcasType).casFeat_categories == null)
      jcasType.jcas.throwFeatMissing("categories", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    jcasType.ll_cas.ll_setRefValue(addr, ((ArticleRevisionInfo_Type)jcasType).casFeatCode_categories, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for categories - gets an indexed value - All categories this document (revision) belongs to.
   * @generated */
  public String getCategories(int i) {
    if (ArticleRevisionInfo_Type.featOkTst && ((ArticleRevisionInfo_Type)jcasType).casFeat_categories == null)
      jcasType.jcas.throwFeatMissing("categories", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ArticleRevisionInfo_Type)jcasType).casFeatCode_categories), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ArticleRevisionInfo_Type)jcasType).casFeatCode_categories), i);}

  /** indexed setter for categories - sets an indexed value - All categories this document (revision) belongs to.
   * @generated */
  public void setCategories(int i, String v) { 
    if (ArticleRevisionInfo_Type.featOkTst && ((ArticleRevisionInfo_Type)jcasType).casFeat_categories == null)
      jcasType.jcas.throwFeatMissing("categories", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ArticleRevisionInfo_Type)jcasType).casFeatCode_categories), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ArticleRevisionInfo_Type)jcasType).casFeatCode_categories), i, v);}
  }

    
