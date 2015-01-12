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
package de.csw.expertfinder.test.pos;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.uima.util.FileUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import edu.northwestern.at.utils.StringUtils;
import edu.northwestern.at.utils.corpuslinguistics.adornedword.AdornedWord;
import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.DefaultLemmatizer;
import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.Lemmatizer;
import edu.northwestern.at.utils.corpuslinguistics.lexicon.Lexicon;
import edu.northwestern.at.utils.corpuslinguistics.partsofspeech.PartOfSpeechTags;
import edu.northwestern.at.utils.corpuslinguistics.postagger.DefaultPartOfSpeechTagger;
import edu.northwestern.at.utils.corpuslinguistics.postagger.PartOfSpeechTagger;
import edu.northwestern.at.utils.corpuslinguistics.sentencesplitter.DefaultSentenceSplitter;
import edu.northwestern.at.utils.corpuslinguistics.sentencesplitter.SentenceSplitter;
import edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer.DefaultSpellingStandardizer;
import edu.northwestern.at.utils.corpuslinguistics.spellingstandardizer.SpellingStandardizer;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.DefaultWordTokenizer;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.PennTreebankTokenizer;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.WordTokenizer;

/**
 * @author ralph
 *
 */
public class TestPartOfSpeechTagger {
	
	private static final Logger log = Logger.getLogger(TestPartOfSpeechTagger.class);
	
	private static final String lemmaSeparator = "|";

	
	/**
	 * Tests the MorphAdorner POS tagger.
	 * @throws Exception
	 */
	@Test(dataProvider = "morphAdornerDataProvider")
	public void testMorphAdorner(List<List<String>> sentences, Class<PartOfSpeechTagger> taggerClass, Class<Lemmatizer> lemmatizerClass) throws Exception {
		
		StopWatch stopWatch = new StopWatch();
		
		// initialize POS tagger (can take long if lexicon based)
        stopWatch.start();
        
        PartOfSpeechTagger partOfSpeechTagger   =
            taggerClass.newInstance();
        
        stopWatch.stop();

        if (log.isDebugEnabled()) 
        	log.debug("Initializing POS tagger (" + taggerClass.getSimpleName() + "): " + stopWatch);

        
        // Initialize lemmatizer
        stopWatch.reset();
        stopWatch.start();
        
        Lemmatizer lemmatizer = lemmatizerClass.newInstance();
        
        stopWatch.stop();
        
        if (log.isDebugEnabled())
        	log.debug("Initializing lemmatizer (" + lemmatizerClass.getSimpleName() + "): " + stopWatch);
        
        // POS tag sentences
        stopWatch.reset();
        stopWatch.start();
        
        List<List<AdornedWord>> taggedSentences =
            partOfSpeechTagger.tagSentences( sentences );
        
        stopWatch.stop();
        
        if (log.isDebugEnabled())
        	log.debug("Tagging words: " + stopWatch);
        
		Lexicon wordLexicon	= partOfSpeechTagger.getLexicon();
		
		SpellingStandardizer standardizer	=
			new DefaultSpellingStandardizer();
		
		lemmatizer.setDictionary(standardizer.getStandardSpellings());


		//	Get the part of speech tags from
		//	the word lexicon.

		PartOfSpeechTags partOfSpeechTags	=
		wordLexicon.getPartOfSpeechTags();

		WordTokenizer spellingTokenizer	=
			new PennTreebankTokenizer();


        // write results to file
        FileWriter out = new FileWriter("posresult_" + taggerClass.getSimpleName() + ".txt");


        for ( int i = 0 ; i < sentences.size() ; i++ )
        {
                                //  Get the next adorned sentence.
                                //  This contains a list of adorned
                                //  words.  Only the spellings
                                //  and part of speech tags are
                                //  guaranteed to be defined.

            List<AdornedWord> sentence  = taggedSentences.get( i );
            

            out.write
            (
                "---------- Sentence " + ( i + 1 ) + " ----------"
            );

                                //  Print out the spelling and part(s)
                                //  of speech for each word in the
                                //  sentence.  Punctuation is treated
                                //  as a word too.

            for ( int j = 0 ; j < sentence.size() ; j++ )
            {
                AdornedWord adornedWord = sentence.get( j );

                setLemma
                (
                    adornedWord ,
                    wordLexicon ,
                    lemmatizer ,
                    partOfSpeechTags ,
                    spellingTokenizer
                );

                out.write
                (
                    StringUtils.rpad( ( j + 1 ) + "" , 3  ) + ": " +
                    StringUtils.rpad( "spell: " + adornedWord.getSpelling() , 20 ) +
                    StringUtils.rpad( "lemmata: " + adornedWord.getLemmata() , 20 ) +
                    adornedWord.getPartsOfSpeech() +
                    "\n"
                );
            }
        }
        
        out.close();
        
        partOfSpeechTagger = null;
        System.gc();
    }
	
	
	/**
	 * Data provider for the MorphAdordner test methods.
	 * Reads the text to tag from the file POStest.txt, and
	 * prepares the text by splitting it into sentences and words.
	 * 
	 * Each inner array of the returnes array contains the token
	 * list and a Tagger class, so that the test method can instanciate
	 * each of the tagger classes.
	 * 
	 * @return
	 * @throws IOException
	 */
	@DataProvider(name = "morphAdornerDataProvider")
	public Object[][] textToTag() throws IOException {
		
		InputStream in = TestPartOfSpeechTagger.class.getResourceAsStream("POStest.txt");
		String text = FileUtils.reader2String(new InputStreamReader(in));
		
		WordTokenizer wordTokenizer = new DefaultWordTokenizer();

        SentenceSplitter sentenceSplitter   =
            new DefaultSentenceSplitter();
        
        List<List<String>> sentences    =
            sentenceSplitter.extractSentences(
                text , wordTokenizer );

		return new Object[][] {
//				{ sentences, HeppleTagger.class }, 				// OutOfMemoryError with 1GB heap space
				{ sentences, DefaultPartOfSpeechTagger.class, DefaultLemmatizer.class },
//				{ sentences, AffixTagger.class },
//				{ sentences, UnigramTagger.class },
//				{ sentences, BigramTagger.class },				// Cannot be used stand-alone
//				{ sentences, BigramHybridTagger.class },		// Cannot be used stand-alone
//				{ sentences, TrigramTagger.class },				// Cannot be used stand-alone
//				{ sentences, TrigramHybridTagger.class },		// Cannot be used stand-alone
		};
	}
	
	/** Get lemma for a word.
    *
    *  @param  adornedWord         The adorned word.
    *  @param  lexicon             The word lexicon.
    *  @param  lemmatizer          The lemmatizer.
    *  @param  partOfSpeechTags    The part of speech tags.
    *  @param  spellingTokenizer   Tokenizer for spelling.
    *
    *  <p>
    *  On output, sets the lemma field of the adorned word
    *  We look in the word lexicon first for the lemma.
    *  If the lexicon does not contain the lemma, we
    *  use the lemmatizer.
    *  </p>
    */

   public static void setLemma
   (
       AdornedWord adornedWord  ,
       Lexicon lexicon ,
       Lemmatizer lemmatizer ,
       PartOfSpeechTags partOfSpeechTags ,
       WordTokenizer spellingTokenizer
   )
   {
       String spelling     = adornedWord.getSpelling();
       String partOfSpeech = adornedWord.getPartsOfSpeech();
       String lemmata      = spelling;

                               //  Get lemmatization word class
                               //  for part of speech.
       String lemmaClass   =
           partOfSpeechTags.getLemmaWordClass( partOfSpeech );

                               //  Do not lemmatize words which
                               //  should not be lemmatized,
                               //  including proper names.

       if  (   lemmatizer.cantLemmatize( spelling ) ||
               lemmaClass.equals( "none" )
           )
       {
       }
       else
       {
                               //  Try compound word exceptions
                               //  list first.

           lemmata = lemmatizer.lemmatize( spelling , "compound" );

                               //  If lemma not found, keep trying.

           if ( lemmata.equals( spelling ) )
           {
                               //  Extract individual word parts.
                               //  May be more than one for a
                               //  contraction.

               List wordList   =
                   spellingTokenizer.extractWords( spelling );

                               //  If just one word part,
                               //  get its lemma.

               if  (   !partOfSpeechTags.isCompoundTag( partOfSpeech ) ||
                       ( wordList.size() == 1 )
                   )
               {
                   if ( lemmaClass.length() == 0 )
                   {
                       lemmata = lemmatizer.lemmatize( spelling );
                   }
                   else
                   {
                       lemmata =
                           lemmatizer.lemmatize( spelling , lemmaClass );
                   }
               }
                               //  More than one word part.
                               //  Get lemma for each part and
                               //  concatenate them with the
                               //  lemma separator to form a
                               //  compound lemma.
               else
               {
                   lemmata             = "";
                   String lemmaPiece   = "";
                   String[] posTags    =
                       partOfSpeechTags.splitTag( partOfSpeech );

                   if ( posTags.length == wordList.size() )
                   {
                       for ( int i = 0 ; i < wordList.size() ; i++ )
                       {
                           String wordPiece    = (String)wordList.get( i );

                           if ( i > 0 )
                           {
                               lemmata = lemmata + lemmaSeparator;
                           }

                           lemmaClass  =
                               partOfSpeechTags.getLemmaWordClass
                               (
                                   posTags[ i ]
                               );

                           lemmaPiece  =
                               lemmatizer.lemmatize
                               (
                                   wordPiece ,
                                   lemmaClass
                               );

                           lemmata = lemmata + lemmaPiece;
                       }
                   }
               }
           }
       }

       adornedWord.setLemmata( lemmata );
   }


}
