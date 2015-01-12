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
package de.csw.expertfinder.mediawiki.uima;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;

import de.csw.expertfinder.mediawiki.uima.types.ArticleRevisionInfo;
import de.csw.expertfinder.mediawiki.uima.types.markup.ExternalLink;
import de.csw.expertfinder.mediawiki.uima.types.markup.InternalLink;
import de.csw.expertfinder.mediawiki.uima.types.markup.LanguageLink;
import de.csw.expertfinder.mediawiki.uima.types.markup.Tag;
import de.tudarmstadt.ukp.wikipedia.parser.ContentElement;
import de.tudarmstadt.ukp.wikipedia.parser.Link;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Span;
import de.tudarmstadt.ukp.wikipedia.parser.SrcSpan;
import de.tudarmstadt.ukp.wikipedia.parser.Content.FormatType;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;

/**
 * This analysis engine detects MediaWiki markup in a Wikipedia article and
 * annotates it accordingly.
 * 
 * @author ralph
 * @deprecated The MediaWiki parser used by this annotor engine cannot parse
 *             certain pages. Use {@link FastMediaWikiMarkupDetector} instead,
 *             which is also faster.
 */
public class MediaWikiMarkupDetector extends JCasAnnotator_ImplBase {
	
	private static final Logger log = Logger.getLogger(MediaWikiMarkupDetector.class);
	
	private static final String PARAM_NAME_PREFIX_CATEGORY = "CategoryPrefix";
	private static final String PARAM_NAME_PREFIX_IMAGE = "ImagePrefix";
	
	private int prefixCategoryLength;

	//	private static final String START_TAG = "<.*>";
//	private static final String END_TAG   = "</.*>";
//	private static final String EMPTY_ELEMENT_TAG = "<.*/>";
//	
//	private static final Pattern pattern = Pattern.compile("");

	private MediaWikiParser parser ;
	
	/**
	 * @see org.apache.uima.analysis_component.AnalysisComponent_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		
		// read AE parameters
		String prefixCategory = (String)context.getConfigParameterValue(PARAM_NAME_PREFIX_CATEGORY);
		if (prefixCategory == null || "".equals(prefixCategory)) {
			log.warn("Parameter " + PARAM_NAME_PREFIX_CATEGORY + " not set for UIMA AE " + MediaWikiMarkupDetector.class.getName() + ". Defaulting to 'Category'.");
			prefixCategory = "Category";
		}
		
		prefixCategoryLength = prefixCategory.length();

		String prefixImage = (String)context.getConfigParameterValue(PARAM_NAME_PREFIX_IMAGE);
		if (prefixImage == null || "".equals(prefixImage)) {
			log.warn("Parameter " + PARAM_NAME_PREFIX_IMAGE + " not set for UIMA AE " + MediaWikiMarkupDetector.class.getName() + ". Defaulting to 'Image'.");
			prefixImage = "Image";
		}
		
		
		MediaWikiParserFactory parserFactory = new MediaWikiParserFactory();
		parserFactory.setCalculateSrcSpans(true);
		parserFactory.setImageIdentifers(Arrays.asList(prefixImage));
		parserFactory.setCategoryIdentifers(Arrays.asList(prefixCategory));
		parserFactory.setShowImageText(true);
		parser = parserFactory.createParser();
	}
	
	public void process(JCas cas) throws AnalysisEngineProcessException {
		String text = cas.getDocumentText();
		
		if ("".equals(text)) {
			// Oops, empty text, probably vandalism. Nothing to do.
			return;
		}
		
		// first get Infobox content, if available. The parser cannot handle this, we have to do it manually.		
		
		
		
 		ParsedPage parsedPage = parser.parse(text);
 		 		
 		String plainText = parsedPage.getText();

// 		List<Paragraph> paragraphs = parsedPage.getParagraphs();
// 		if (paragraphs.isEmpty()) {
// 			// This page revision contains no paragraphs. This may be the case if the page simply contains a redirect directive.
// 			// We can return immediately (in fact, we must, because the MediaWikiParser does not handle this case correctly and
// 			// throws NullPointerExceptions).
// 			return;
// 		}
// 		for (Paragraph paragraph : paragraphs) {
//			SrcSpan srcSpan = paragraph.getSrcSpan();
//			if (srcSpan != null) {
//				int start = srcSpan.getStart();
//				int end = srcSpan.getEnd();
//				de.csw.expertfinder.mediawiki.uima.types.markup.Paragraph paragraphAnnotation = new de.csw.expertfinder.mediawiki.uima.types.markup.Paragraph(cas);
//				paragraphAnnotation.setBegin(start);
//				paragraphAnnotation.setEnd(end);
//				paragraphAnnotation.addToIndexes();
//			}
//		}

 		ContentElement languagesElement = parsedPage.getLanguagesElement();
 		if (languagesElement != null) {
 	 		List<Link> languageLinks = parsedPage.getLanguages(); // throws NPE if getLanguagesElement() returns null 
 	 		for (Link link : languageLinks) {
 				SrcSpan srcSpan = link.getSrcSpan();
 				int start = srcSpan.getStart();
 				int end = srcSpan.getEnd();
 				LanguageLink linkAnnotation = new LanguageLink(cas);
 				linkAnnotation.setBegin(start);
 				linkAnnotation.setEnd(end);
 				String linkTarget = link.getTarget();
 				String[] linkTargetComponents = linkTarget.split(":");
 				linkAnnotation.setLanguage(linkTargetComponents[0]);
 				linkAnnotation.setTranslatedArticleName(linkTargetComponents[1]);
 				linkAnnotation.addToIndexes();
 			}
 		}
 		
 		List<Link> links = parsedPage.getLinks();
 		for (Link link : links) {
			SrcSpan srcSpan = link.getSrcSpan();
			int start = srcSpan.getStart();
			int end = srcSpan.getEnd();
			if (start == -1 || end == -1) {
				// the parser sometimes has problems with calculating the source span of image links
				continue;
			}
			switch(link.getType()) {
				case INTERNAL : {
					InternalLink linkAnnotation = new InternalLink(cas);
					linkAnnotation.setArticle(link.getTarget());
					linkAnnotation.setTitle(link.getText());
					linkAnnotation.setBegin(start);
					linkAnnotation.setEnd(end);
					linkAnnotation.addToIndexes();
					break;
				}
				case IMAGE :
				case AUDIO :
				case VIDEO : {
					InternalLink linkAnnotation = new InternalLink(cas);
					linkAnnotation.setFile(link.getTarget());
					linkAnnotation.setTitle(link.getText());
					linkAnnotation.setBegin(start);
					linkAnnotation.setEnd(end);
					linkAnnotation.addToIndexes();
					break;
				}
				case EXTERNAL : {
					ExternalLink linkAnnotation = new ExternalLink(cas);
					linkAnnotation.setDestination(link.getTarget());
					linkAnnotation.setBegin(start);
					linkAnnotation.setEnd(end);
					linkAnnotation.addToIndexes();
					break;
				}
			}
		}
 		
 		List<FormatType> formats = parsedPage.getFormats();
 		for (FormatType formatType : formats) {
			List<Span> formatSpans = parsedPage.getFormatSpans(formatType);
			for (Span span : formatSpans) {
				SrcSpan srcSpan = span.getSrcSpan();
				int start = srcSpan.getStart();
				int end = srcSpan.getEnd();
				switch(formatType) {
					case BOLD:
					case ITALIC: {
						// why bother?
//						Emphasis emphasisAnntation = new Emphasis(cas);
//						emphasisAnntation.setBegin(start);
//						emphasisAnntation.setEnd(end);
//						emphasisAnntation.setText(span.getText(plainText));
//						emphasisAnntation.addToIndexes();
						break;
					}
					case TAG: {
						Tag tagAnnotation = new Tag(cas);
						tagAnnotation.setBegin(start);
						tagAnnotation.setEnd(end);
						tagAnnotation.setText(span.getText(plainText));
						tagAnnotation.addToIndexes();
						break;
					}
				}
			}
		}
 		
 		AnnotationIndex articleRevisionInfoIndex = cas.getAnnotationIndex(ArticleRevisionInfo.type);
 		ArticleRevisionInfo articleRevisionInfo = (ArticleRevisionInfo)articleRevisionInfoIndex.iterator().next(); // there is only one
 		
 		ContentElement categoryElement = parsedPage.getCategoryElement();
 		if (categoryElement != null) {
	 		List<Link> categories = categoryElement.getLinks();
	 		int length = categories.size();
			StringArray categoryArray = new StringArray(cas, length);
	 		
	 		for (int i=0; i<length; i++) {
	 			Link category = categories.get(i);
	 			categoryArray.set(i, category.getTarget().substring(prefixCategoryLength));
			}

			articleRevisionInfo.setCategories(categoryArray);
 		}
 		

// we do not detect sections here, since the parser does not get it right
// in too many cases.
// we use our own section detector instead. (MediaWikiSectionDetector)
// 		List<Section> sections = parsedPage.getSections();
// 		for (Section section : sections) {
//			SrcSpan srcSpan = section.getSrcSpan();
//			int start = srcSpan.getStart();
//			int end = srcSpan.getEnd();
//			de.csw.expertfinder.mediawiki.uima.types.markup.Section sectionAnnotation = new de.csw.expertfinder.mediawiki.uima.types.markup.Section(cas);
//			sectionAnnotation.setBegin(start);
//			sectionAnnotation.setEnd(end);
//			ContentElement title = section.getTitleElement();
//			if (title != null) {
//				sectionAnnotation.setTitle(title.getText());
//			}
//			sectionAnnotation.setLevel(section.getLevel());
//			sectionAnnotation.addToIndexes();
//		}
 		
 		// we don't need that here
// 		ArticleTitle articleTitleAnnotation = new ArticleTitle(cas);
// 		articleTitleAnnotation.setTitle(parsedPage.getName());
// 		articleTitleAnnotation.addToIndexes();
	}
	

}
