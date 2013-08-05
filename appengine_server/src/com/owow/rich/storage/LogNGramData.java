package com.owow.rich.storage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.owow.rich.apiHandler.ApiHandler;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.items.NGramDatastoreEntity;
import com.owow.rich.items.WebPage;

public class LogNGramData {
	public static NGramDatastoreEntity retriveNGramDatastoreEntity
	      (String ngram, WebPage webPage) throws MalformedURLException
	{
		ApiHandler handler = ApiType.freebase.createHandler();
		List<ApiResponse> apiResponseList = null;
		try {
			apiResponseList = handler.getAllApiResponses(ngram, ApiType.freebase);
		} catch (Exception e) {}
		handler = ApiType.wiki.createHandler();
		ApiResponse wikiResponse = null;
		try {
			wikiResponse = handler.getFirstResponse(ngram, ApiType.wiki);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new NGramDatastoreEntity(new URL(webPage.url), ngram, apiResponseList, wikiResponse);
	}
}
