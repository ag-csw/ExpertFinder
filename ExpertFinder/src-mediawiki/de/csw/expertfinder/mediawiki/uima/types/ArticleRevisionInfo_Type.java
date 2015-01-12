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
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.DocumentAnnotation_Type;

/** Information about the entire document, which in this case represents a revision of a MediaWiki article.
 * Updated by JCasGen Fri Sep 18 01:57:52 CEST 2009
 * @generated */
public class ArticleRevisionInfo_Type extends DocumentAnnotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ArticleRevisionInfo_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ArticleRevisionInfo_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ArticleRevisionInfo(addr, ArticleRevisionInfo_Type.this);
  			   ArticleRevisionInfo_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ArticleRevisionInfo(addr, ArticleRevisionInfo_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = ArticleRevisionInfo.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
 
  /** @generated */
  final Feature casFeat_authorName;
  /** @generated */
  final int     casFeatCode_authorName;
  /** @generated */ 
  public String getAuthorName(int addr) {
        if (featOkTst && casFeat_authorName == null)
      jcas.throwFeatMissing("authorName", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    return ll_cas.ll_getStringValue(addr, casFeatCode_authorName);
  }
  /** @generated */    
  public void setAuthorName(int addr, String v) {
        if (featOkTst && casFeat_authorName == null)
      jcas.throwFeatMissing("authorName", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    ll_cas.ll_setStringValue(addr, casFeatCode_authorName, v);}
    
  
 
  /** @generated */
  final Feature casFeat_articleId;
  /** @generated */
  final int     casFeatCode_articleId;
  /** @generated */ 
  public int getArticleId(int addr) {
        if (featOkTst && casFeat_articleId == null)
      jcas.throwFeatMissing("articleId", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    return ll_cas.ll_getIntValue(addr, casFeatCode_articleId);
  }
  /** @generated */    
  public void setArticleId(int addr, int v) {
        if (featOkTst && casFeat_articleId == null)
      jcas.throwFeatMissing("articleId", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    ll_cas.ll_setIntValue(addr, casFeatCode_articleId, v);}
    
  
 
  /** @generated */
  final Feature casFeat_revisionId;
  /** @generated */
  final int     casFeatCode_revisionId;
  /** @generated */ 
  public int getRevisionId(int addr) {
        if (featOkTst && casFeat_revisionId == null)
      jcas.throwFeatMissing("revisionId", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    return ll_cas.ll_getIntValue(addr, casFeatCode_revisionId);
  }
  /** @generated */    
  public void setRevisionId(int addr, int v) {
        if (featOkTst && casFeat_revisionId == null)
      jcas.throwFeatMissing("revisionId", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    ll_cas.ll_setIntValue(addr, casFeatCode_revisionId, v);}
    
  
 
  /** @generated */
  final Feature casFeat_timestamp;
  /** @generated */
  final int     casFeatCode_timestamp;
  /** @generated */ 
  public long getTimestamp(int addr) {
        if (featOkTst && casFeat_timestamp == null)
      jcas.throwFeatMissing("timestamp", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    return ll_cas.ll_getLongValue(addr, casFeatCode_timestamp);
  }
  /** @generated */    
  public void setTimestamp(int addr, long v) {
        if (featOkTst && casFeat_timestamp == null)
      jcas.throwFeatMissing("timestamp", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    ll_cas.ll_setLongValue(addr, casFeatCode_timestamp, v);}
    
  
 
  /** @generated */
  final Feature casFeat_title;
  /** @generated */
  final int     casFeatCode_title;
  /** @generated */ 
  public String getTitle(int addr) {
        if (featOkTst && casFeat_title == null)
      jcas.throwFeatMissing("title", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    return ll_cas.ll_getStringValue(addr, casFeatCode_title);
  }
  /** @generated */    
  public void setTitle(int addr, String v) {
        if (featOkTst && casFeat_title == null)
      jcas.throwFeatMissing("title", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    ll_cas.ll_setStringValue(addr, casFeatCode_title, v);}
    
  
 
  /** @generated */
  final Feature casFeat_categories;
  /** @generated */
  final int     casFeatCode_categories;
  /** @generated */ 
  public int getCategories(int addr) {
        if (featOkTst && casFeat_categories == null)
      jcas.throwFeatMissing("categories", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    return ll_cas.ll_getRefValue(addr, casFeatCode_categories);
  }
  /** @generated */    
  public void setCategories(int addr, int v) {
        if (featOkTst && casFeat_categories == null)
      jcas.throwFeatMissing("categories", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    ll_cas.ll_setRefValue(addr, casFeatCode_categories, v);}
    
   /** @generated */
  public String getCategories(int addr, int i) {
        if (featOkTst && casFeat_categories == null)
      jcas.throwFeatMissing("categories", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_categories), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_categories), i);
  return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_categories), i);
  }
   
  /** @generated */ 
  public void setCategories(int addr, int i, String v) {
        if (featOkTst && casFeat_categories == null)
      jcas.throwFeatMissing("categories", "de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_categories), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_categories), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_categories), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public ArticleRevisionInfo_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_authorName = jcas.getRequiredFeatureDE(casType, "authorName", "uima.cas.String", featOkTst);
    casFeatCode_authorName  = (null == casFeat_authorName) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_authorName).getCode();

 
    casFeat_articleId = jcas.getRequiredFeatureDE(casType, "articleId", "uima.cas.Integer", featOkTst);
    casFeatCode_articleId  = (null == casFeat_articleId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_articleId).getCode();

 
    casFeat_revisionId = jcas.getRequiredFeatureDE(casType, "revisionId", "uima.cas.Integer", featOkTst);
    casFeatCode_revisionId  = (null == casFeat_revisionId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_revisionId).getCode();

 
    casFeat_timestamp = jcas.getRequiredFeatureDE(casType, "timestamp", "uima.cas.Long", featOkTst);
    casFeatCode_timestamp  = (null == casFeat_timestamp) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_timestamp).getCode();

 
    casFeat_title = jcas.getRequiredFeatureDE(casType, "title", "uima.cas.String", featOkTst);
    casFeatCode_title  = (null == casFeat_title) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_title).getCode();

 
    casFeat_categories = jcas.getRequiredFeatureDE(casType, "categories", "uima.cas.StringArray", featOkTst);
    casFeatCode_categories  = (null == casFeat_categories) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_categories).getCode();

  }
}



    
