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
/* First created by JCasGen Tue May 05 20:23:32 CEST 2009 */
package de.csw.expertfinder.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Annotates a single word in the text.
 * Updated by JCasGen Fri Sep 18 01:57:52 CEST 2009
 * XML source: /Users/ralph/uima_workspace/ExpertFinder/desc/GermanStemmerTypeSystem.xml
 * @generated */
public class Word extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Word.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Word() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Word(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Word(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Word(JCas jcas, int begin, int end) {
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
  //* Feature: WordStem

  /** getter for WordStem - gets The stem of the word.
   * @generated */
  public String getWordStem() {
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_wordStem == null)
      jcasType.jcas.throwFeatMissing("wordStem", "de.csw.expertfinder.uima.types.Word");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Word_Type)jcasType).casFeatCode_wordStem);}
    
  /** setter for WordStem - sets The stem of the word. 
   * @generated */
  public void setWordStem(String v) {
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_wordStem == null)
      jcasType.jcas.throwFeatMissing("wordStem", "de.csw.expertfinder.uima.types.Word");
    jcasType.ll_cas.ll_setStringValue(addr, ((Word_Type)jcasType).casFeatCode_wordStem, v);}    
   
    
  //*--------------*
  //* Feature: lemma

  /** getter for lemma - gets The lemma of the word
   * @generated */
  public String getLemma() {
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_lemma == null)
      jcasType.jcas.throwFeatMissing("lemma", "de.csw.expertfinder.uima.types.Word");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Word_Type)jcasType).casFeatCode_lemma);}
    
  /** setter for lemma - sets The lemma of the word 
   * @generated */
  public void setLemma(String v) {
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_lemma == null)
      jcasType.jcas.throwFeatMissing("lemma", "de.csw.expertfinder.uima.types.Word");
    jcasType.ll_cas.ll_setStringValue(addr, ((Word_Type)jcasType).casFeatCode_lemma, v);}    
  }

    
