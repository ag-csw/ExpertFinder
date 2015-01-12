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

/** A reference to an external source of information.
 * Updated by JCasGen Fri Sep 25 16:45:43 CEST 2009
 * @generated */
public class Reference_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Reference_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Reference_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Reference(addr, Reference_Type.this);
  			   Reference_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Reference(addr, Reference_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = Reference.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.csw.expertfinder.mediawiki.uima.types.markup.Reference");
 
  /** @generated */
  final Feature casFeat_author;
  /** @generated */
  final int     casFeatCode_author;
  /** @generated */ 
  public String getAuthor(int addr) {
        if (featOkTst && casFeat_author == null)
      jcas.throwFeatMissing("author", "de.csw.expertfinder.mediawiki.uima.types.markup.Reference");
    return ll_cas.ll_getStringValue(addr, casFeatCode_author);
  }
  /** @generated */    
  public void setAuthor(int addr, String v) {
        if (featOkTst && casFeat_author == null)
      jcas.throwFeatMissing("author", "de.csw.expertfinder.mediawiki.uima.types.markup.Reference");
    ll_cas.ll_setStringValue(addr, casFeatCode_author, v);}
    
  
 
  /** @generated */
  final Feature casFeat_collection;
  /** @generated */
  final int     casFeatCode_collection;
  /** @generated */ 
  public String getCollection(int addr) {
        if (featOkTst && casFeat_collection == null)
      jcas.throwFeatMissing("collection", "de.csw.expertfinder.mediawiki.uima.types.markup.Reference");
    return ll_cas.ll_getStringValue(addr, casFeatCode_collection);
  }
  /** @generated */    
  public void setCollection(int addr, String v) {
        if (featOkTst && casFeat_collection == null)
      jcas.throwFeatMissing("collection", "de.csw.expertfinder.mediawiki.uima.types.markup.Reference");
    ll_cas.ll_setStringValue(addr, casFeatCode_collection, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Reference_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_author = jcas.getRequiredFeatureDE(casType, "author", "uima.cas.String", featOkTst);
    casFeatCode_author  = (null == casFeat_author) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_author).getCode();

 
    casFeat_collection = jcas.getRequiredFeatureDE(casType, "collection", "uima.cas.String", featOkTst);
    casFeatCode_collection  = (null == casFeat_collection) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_collection).getCode();

  }
}



    
