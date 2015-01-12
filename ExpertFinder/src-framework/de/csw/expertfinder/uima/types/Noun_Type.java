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
/* First created by JCasGen Sun May 10 11:21:46 CEST 2009 */
package de.csw.expertfinder.uima.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** A noun.
 * Updated by JCasGen Fri Sep 18 01:57:52 CEST 2009
 * @generated */
public class Noun_Type extends Word_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Noun_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Noun_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Noun(addr, Noun_Type.this);
  			   Noun_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Noun(addr, Noun_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = Noun.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.csw.expertfinder.uima.types.Noun");



  /** @generated */
  final Feature casFeat_conceptURIs;
  /** @generated */
  final int     casFeatCode_conceptURIs;
  /** @generated */ 
  public int getConceptURIs(int addr) {
        if (featOkTst && casFeat_conceptURIs == null)
      jcas.throwFeatMissing("conceptURIs", "de.csw.expertfinder.uima.types.Noun");
    return ll_cas.ll_getRefValue(addr, casFeatCode_conceptURIs);
  }
  /** @generated */    
  public void setConceptURIs(int addr, int v) {
        if (featOkTst && casFeat_conceptURIs == null)
      jcas.throwFeatMissing("conceptURIs", "de.csw.expertfinder.uima.types.Noun");
    ll_cas.ll_setRefValue(addr, casFeatCode_conceptURIs, v);}
    
   /** @generated */
  public String getConceptURIs(int addr, int i) {
        if (featOkTst && casFeat_conceptURIs == null)
      jcas.throwFeatMissing("conceptURIs", "de.csw.expertfinder.uima.types.Noun");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_conceptURIs), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_conceptURIs), i);
  return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_conceptURIs), i);
  }
   
  /** @generated */ 
  public void setConceptURIs(int addr, int i, String v) {
        if (featOkTst && casFeat_conceptURIs == null)
      jcas.throwFeatMissing("conceptURIs", "de.csw.expertfinder.uima.types.Noun");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_conceptURIs), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_conceptURIs), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_conceptURIs), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Noun_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_conceptURIs = jcas.getRequiredFeatureDE(casType, "conceptURIs", "uima.cas.StringArray", featOkTst);
    casFeatCode_conceptURIs  = (null == casFeat_conceptURIs) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_conceptURIs).getCode();

  }
}



    
