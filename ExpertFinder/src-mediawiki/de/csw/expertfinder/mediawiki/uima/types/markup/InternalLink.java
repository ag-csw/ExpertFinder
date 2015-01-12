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


/** An internal wiki link. Points either to an article (or a section inside an article), a MediaWiki category, or a file. Only one of these three attributes is set, the other two are null.
 * Updated by JCasGen Fri Sep 25 16:45:43 CEST 2009
 * XML source: /Users/ralph/uima_workspace/ExpertFinder/desc/FastMediaWikiMarkupDetector.xml
 * @generated */
public class InternalLink extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(InternalLink.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected InternalLink() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public InternalLink(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public InternalLink(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public InternalLink(JCas jcas, int begin, int end) {
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
  //* Feature: article

  /** getter for article - gets the link destination (i.e. the wiki article this link points to)
   * @generated */
  public String getArticle() {
    if (InternalLink_Type.featOkTst && ((InternalLink_Type)jcasType).casFeat_article == null)
      jcasType.jcas.throwFeatMissing("article", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    return jcasType.ll_cas.ll_getStringValue(addr, ((InternalLink_Type)jcasType).casFeatCode_article);}
    
  /** setter for article - sets the link destination (i.e. the wiki article this link points to) 
   * @generated */
  public void setArticle(String v) {
    if (InternalLink_Type.featOkTst && ((InternalLink_Type)jcasType).casFeat_article == null)
      jcasType.jcas.throwFeatMissing("article", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    jcasType.ll_cas.ll_setStringValue(addr, ((InternalLink_Type)jcasType).casFeatCode_article, v);}    
   
    
  //*--------------*
  //* Feature: title

  /** getter for title - gets the alternative title for this link that is visible in the wiki text
   * @generated */
  public String getTitle() {
    if (InternalLink_Type.featOkTst && ((InternalLink_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    return jcasType.ll_cas.ll_getStringValue(addr, ((InternalLink_Type)jcasType).casFeatCode_title);}
    
  /** setter for title - sets the alternative title for this link that is visible in the wiki text 
   * @generated */
  public void setTitle(String v) {
    if (InternalLink_Type.featOkTst && ((InternalLink_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    jcasType.ll_cas.ll_setStringValue(addr, ((InternalLink_Type)jcasType).casFeatCode_title, v);}    
   
    
  //*--------------*
  //* Feature: namespace

  /** getter for namespace - gets The namespace of the article this link points to
   * @generated */
  public String getNamespace() {
    if (InternalLink_Type.featOkTst && ((InternalLink_Type)jcasType).casFeat_namespace == null)
      jcasType.jcas.throwFeatMissing("namespace", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    return jcasType.ll_cas.ll_getStringValue(addr, ((InternalLink_Type)jcasType).casFeatCode_namespace);}
    
  /** setter for namespace - sets The namespace of the article this link points to 
   * @generated */
  public void setNamespace(String v) {
    if (InternalLink_Type.featOkTst && ((InternalLink_Type)jcasType).casFeat_namespace == null)
      jcasType.jcas.throwFeatMissing("namespace", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    jcasType.ll_cas.ll_setStringValue(addr, ((InternalLink_Type)jcasType).casFeatCode_namespace, v);}    
   
    
  //*--------------*
  //* Feature: category

  /** getter for category - gets the category this link points to (if so, the article attribute is null).
   * @generated */
  public String getCategory() {
    if (InternalLink_Type.featOkTst && ((InternalLink_Type)jcasType).casFeat_category == null)
      jcasType.jcas.throwFeatMissing("category", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    return jcasType.ll_cas.ll_getStringValue(addr, ((InternalLink_Type)jcasType).casFeatCode_category);}
    
  /** setter for category - sets the category this link points to (if so, the article attribute is null). 
   * @generated */
  public void setCategory(String v) {
    if (InternalLink_Type.featOkTst && ((InternalLink_Type)jcasType).casFeat_category == null)
      jcasType.jcas.throwFeatMissing("category", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    jcasType.ll_cas.ll_setStringValue(addr, ((InternalLink_Type)jcasType).casFeatCode_category, v);}    
   
    
  //*--------------*
  //* Feature: file

  /** getter for file - gets the file this link points to (if so, the article and category attributes are null)
   * @generated */
  public String getFile() {
    if (InternalLink_Type.featOkTst && ((InternalLink_Type)jcasType).casFeat_file == null)
      jcasType.jcas.throwFeatMissing("file", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    return jcasType.ll_cas.ll_getStringValue(addr, ((InternalLink_Type)jcasType).casFeatCode_file);}
    
  /** setter for file - sets the file this link points to (if so, the article and category attributes are null) 
   * @generated */
  public void setFile(String v) {
    if (InternalLink_Type.featOkTst && ((InternalLink_Type)jcasType).casFeat_file == null)
      jcasType.jcas.throwFeatMissing("file", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    jcasType.ll_cas.ll_setStringValue(addr, ((InternalLink_Type)jcasType).casFeatCode_file, v);}    
   
    
  //*--------------*
  //* Feature: section

  /** getter for section - gets The target section of this link.
   * @generated */
  public String getSection() {
    if (InternalLink_Type.featOkTst && ((InternalLink_Type)jcasType).casFeat_section == null)
      jcasType.jcas.throwFeatMissing("section", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    return jcasType.ll_cas.ll_getStringValue(addr, ((InternalLink_Type)jcasType).casFeatCode_section);}
    
  /** setter for section - sets The target section of this link. 
   * @generated */
  public void setSection(String v) {
    if (InternalLink_Type.featOkTst && ((InternalLink_Type)jcasType).casFeat_section == null)
      jcasType.jcas.throwFeatMissing("section", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    jcasType.ll_cas.ll_setStringValue(addr, ((InternalLink_Type)jcasType).casFeatCode_section, v);}    
  }

    
