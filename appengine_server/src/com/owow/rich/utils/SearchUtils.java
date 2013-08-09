package com.owow.rich.utils;

import java.util.List;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.MatchScorer;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.SortOptions;
import com.owow.rich.items.Token;

/**
 * A wrapper around appengine text search.
 */
public class SearchUtils {

	public String DEFAULT_NAMESPACE = "DEFAULT_NAMESAPCE";
	private final static  String INDEX_NAME = "INDEX1";
   private Index index = SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder().setName(INDEX_NAME).build());
	private TokenizerUtil tokenizeUtil = new TokenizerUtil();

	public void  index(String documentId, String documentText, String namespace) {
		if (namespace == null) {
			namespace = DEFAULT_NAMESPACE;
		}
		
		Document document = Document.newBuilder().setId(documentId)
				.addField(Field.newBuilder().setName("content").setText(documentText))
				.addField(Field.newBuilder().setName("namespcae").setText(namespace))
				.build();
		index.put(document);
	}
	
	public Results<ScoredDocument> search(String text, String namespace) {
		if (namespace == null) {
			namespace = DEFAULT_NAMESPACE;
		}
		
		// Convert Query string to App Engine Text Search format
		List<Token> tokens = tokenizeUtil.tokenize(text);
		// TODO: also try to search on n-gram.
		
		String queryString = "namespcae:" + namespace + " AND (";
		for (int i = 0; i < tokens.size(); i++) {
			queryString += "content:\"" + tokens.get(i)+"\"";
		    if(i < tokens.size() -1) {
		   	 queryString += " OR ";
		    }
	   }
		queryString += ")";
		
		// Creates the query, in the option force to show sort score.
		Query queryObject = Query.newBuilder()
				.setOptions(QueryOptions.newBuilder().setSortOptions(
						SortOptions.newBuilder().setMatchScorer
						(MatchScorer.newBuilder().build()))).build(queryString);
	   return index.search(queryObject);
	}
	
}
