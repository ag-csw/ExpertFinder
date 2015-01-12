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
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** An internal wiki link. Points either to an article (or a section inside an article), a MediaWiki category, or a file. Only one of these three attributes is set, the other two are null.
 * Updated by JCasGen Fri Sep 25 16:45:43 CEST 2009
 * @generated */
public class InternalLink_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (InternalLink_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = InternalLink_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new InternalLink(addr, InternalLink_Type.this);
  			   InternalLink_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new InternalLink(addr, InternalLink_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = InternalLink.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
 
  /** @generated */
  final Feature casFeat_article;
  /** @generated */
  final int     casFeatCode_article;
  /** @generated */ 
  public String getArticle(int addr) {
        if (featOkTst && casFeat_article == null)
      jcas.throwFeatMissing("article", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    return ll_cas.ll_getStringValue(addr, casFeatCode_article);
  }
  /** @generated */    
  public void setArticle(int addr, String v) {
        if (featOkTst && casFeat_article == null)
      jcas.throwFeatMissing("article", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    ll_cas.ll_setStringValue(addr, casFeatCode_article, v);}
    
  
 
  /** @generated */
  final Feature casFeat_title;
  /** @generated */
  final int     casFeatCode_title;
  /** @generated */ 
  public String getTitle(int addr) {
        if (featOkTst && casFeat_title == null)
      jcas.throwFeatMissing("title", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    return ll_cas.ll_getStringValue(addr, casFeatCode_title);
  }
  /** @generated */    
  public void setTitle(int addr, String v) {
        if (featOkTst && casFeat_title == null)
      jcas.throwFeatMissing("title", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    ll_cas.ll_setStringValue(addr, casFeatCode_title, v);}
    
  
 
  /** @generated */
  final Feature casFeat_namespace;
  /** @generated */
  final int     casFeatCode_namespace;
  /** @generated */ 
  public String getNamespace(int addr) {
        if (featOkTst && casFeat_namespace == null)
      jcas.throwFeatMissing("namespace", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    return ll_cas.ll_getStringValue(addr, casFeatCode_namespace);
  }
  /** @generated */    
  public void setNamespace(int addr, String v) {
        if (featOkTst && casFeat_namespace == null)
      jcas.throwFeatMissing("namespace", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    ll_cas.ll_setStringValue(addr, casFeatCode_namespace, v);}
    
  
 
  /** @generated */
  final Feature casFeat_category;
  /** @generated */
  final int     casFeatCode_category;
  /** @generated */ 
  public String getCategory(int addr) {
        if (featOkTst && casFeat_category == null)
      jcas.throwFeatMissing("category", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    return ll_cas.ll_getStringValue(addr, casFeatCode_category);
  }
  /** @generated */    
  public void setCategory(int addr, String v) {
        if (featOkTst && casFeat_category == null)
      jcas.throwFeatMissing("category", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    ll_cas.ll_setStringValue(addr, casFeatCode_category, v);}
    
  
 
  /** @generated */
  final Feature casFeat_file;
  /** @generated */
  final int     casFeatCode_file;
  /** @generated */ 
  public String getFile(int addr) {
        if (featOkTst && casFeat_file == null)
      jcas.throwFeatMissing("file", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    return ll_cas.ll_getStringValue(addr, casFeatCode_file);
  }
  /** @generated */    
  public void setFile(int addr, String v) {
        if (featOkTst && casFeat_file == null)
      jcas.throwFeatMissing("file", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    ll_cas.ll_setStringValue(addr, casFeatCode_file, v);}
    
  
 
  /** @generated */
  final Feature casFeat_section;
  /** @generated */
  final int     casFeatCode_section;
  /** @generated */ 
  public String getSection(int addr) {
        if (featOkTst && casFeat_section == null)
      jcas.throwFeatMissing("section", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    return ll_cas.ll_getStringValue(addr, casFeatCode_section);
  }
  /** @generated */    
  public void setSection(int addr, String v) {
        if (featOkTst && casFeat_section == null)
      jcas.throwFeatMissing("section", "de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink");
    ll_cas.ll_setStringValue(addr, casFeatCode_section, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public InternalLink_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_article = jcas.getRequiredFeatureDE(casType, "article", "uima.cas.String", featOkTst);
    casFeatCode_article  = (null == casFeat_article) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_article).getCode();

 
    casFeat_title = jcas.getRequiredFeatureDE(casType, "title", "uima.cas.String", featOkTst);
    casFeatCode_title  = (null == casFeat_title) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_title).getCode();

 
    casFeat_namespace = jcas.getRequiredFeatureDE(casType, "namespace", "uima.cas.String", featOkTst);
    casFeatCode_namespace  = (null == casFeat_namespace) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_namespace).getCode();

 
    casFeat_category = jcas.getRequiredFeatureDE(casType, "category", "uima.cas.String", featOkTst);
    casFeatCode_category  = (null == casFeat_category) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_category).getCode();

 
    casFeat_file = jcas.getRequiredFeatureDE(casType, "file", "uima.cas.String", featOkTst);
    casFeatCode_file  = (null == casFeat_file) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_file).getCode();

 
    casFeat_section = jcas.getRequiredFeatureDE(casType, "section", "uima.cas.String", featOkTst);
    casFeatCode_section  = (null == casFeat_section) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_section).getCode();

  }
}



    
