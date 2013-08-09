package com.owow.rich.utils;

import java.util.List;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.owow.rich.items.WebPage;

public class RelatedLinkSearch {

	private static SearchUtils searchUtils = new SearchUtils();
	
	public static void index(WebPage page) {
		searchUtils.index(page.url, page.getText(), page.getHost());
	}
	
	public static  List<WebPage> search(WebPage currentPage, String query) {
		List<WebPage> webPages = Lists.newArrayList();
		Results<ScoredDocument> documents = searchUtils.search(query, currentPage.getHost());
		
		int counter = 0;
		for (ScoredDocument scoredDocument : documents) {
			String url = scoredDocument.getId();
			WebPage webPage = new WebPage(null, null, url);
			
			
			webPages.add(webPage);
			
			
			counter++;
			if(counter == 3) break;
      }
		return webPages;
	}
	
}
