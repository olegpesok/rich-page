package com.owow.rich.utils;

import java.util.List;
import java.util.Map;

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
import com.google.appengine.labs.repackaged.com.google.common.collect.Iterables;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.items.Token;
import com.owow.rich.utils.TFIDFUtil.Documents;

public class TFIDFUtil {
	private final static  String INDEX_NAME = "INDEX1";
   private Index index = SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder().setName(INDEX_NAME).build());
	private TokenizerUtil tokenizeUtil = new TokenizerUtil();
   
	public static class Documents<T> {

		public void add(ApiResponse apiResponse, String text) {
	      // TODO Auto-generated method stub
	      
      }

		public ApiResponse getBest() {
	      // TODO Auto-generated method stub
	      return null;
      }
	}
	
	public class DocumentScore {
		public String documentId;
		public String documentText;
		public double score;
		public DocumentScore(String documentId, String documentText, double score) {
			this.documentId = documentId;
			this.documentText = documentText;
			this.score = score;
		}
	}
	
	public Documents<ApiResponse> getRankList(String text, String highlight, Documents<ApiResponse> documents) {
	   // TODO Auto-generated method stub
	   return null;
   }
	
	public List<DocumentScore> rankDocumentsSimilarityToText(String text, Map<String, String> documentIdToText, String namespace) {
		// put all the document in index
		for (String documentId : documentIdToText.keySet()) {
			Document document = Document.newBuilder().setId(documentId)
					.addField(Field.newBuilder().setName("content").setText(documentIdToText.get(documentId)))
					.addField(Field.newBuilder().setName("namespcae").setText(namespace))
					.build();
			index.put(document);
      }
	
		// Convert Query string to App Engine Text Search format
		List<Token> tokens = tokenizeUtil.tokenize(text);
		String queryString = "";
		for (int i = 0; i < tokens.size(); i++) {
			queryString += "content:" + tokens.get(i);
		    if(i < tokens.size() -1) {
		   	 queryString += " OR ";
		    }
	      }
		
		// Creates the query, in the option force to show sort score.
		Query queryObject = Query.newBuilder()
				.setOptions(QueryOptions.newBuilder().setSortOptions(
						SortOptions.newBuilder().setMatchScorer
						(MatchScorer.newBuilder().build()))).build(queryString);
	   Results<ScoredDocument> searchResults = index.search(queryObject);
	   
	   List<DocumentScore> processedResults = Lists.newArrayList();
	   for (ScoredDocument document : searchResults) {
	   	String documentText = documentIdToText.get(document.getId());
	   	Double score = Iterables.getFirst(document.getSortScores(), 0.0);
	   	processedResults.add(new DocumentScore(document.getId(), documentText, score));  
	   }
	   return processedResults;
	}

	
}
