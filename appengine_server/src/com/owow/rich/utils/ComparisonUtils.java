package com.owow.rich.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.datanucleus.Utils.Function;
import com.google.appengine.labs.repackaged.com.google.common.collect.Iterables;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;
import com.owow.rich.RichLogger;

public class ComparisonUtils {
	public static class ScoredObjectList<T> {
		public List<ScoredObject<T>> scoredObjectList = Lists.newArrayList();
		public ScoredObjectList(List<ScoredObject<T>> scoredObjectList) {
			this.scoredObjectList = scoredObjectList;
		}
		public T getBest() {
			if (this.scoredObjectList.size() > 0) {
				return this.scoredObjectList.get(0).object;
			} else {
				return null;
			}
      }
		public boolean isEmpty() {
	      return scoredObjectList.size() == 0;
      }
	}
	public static class ScoredObject<T> {
		public T object;
		public String documentId;
		public String documentText;
		public double score;
		public ScoredObject(T object, String documentId, String documentText) {
			this.object = object;
			this.documentId = documentId;
			this.documentText = documentText;
			this.score = 0.0;
		}
	}
	public SearchUtils searchUtils = new SearchUtils();

	
	public <T> ScoredObjectList<String> simpleFindBestMatch(String text, List<String> TextList) {
		Map<String, ScoredObject<String>> map = Maps.newHashMap();
		for (String otherText : TextList) {
			String randomId = "" + Math.round(Math.random()*1000000);
			map.put(randomId, new ScoredObject<String>(otherText, randomId, otherText));
      }
		String namespace = "" + Math.round(Math.random()*1000000);
		return rankDocumentsSimilarityToText(text, map, namespace);
   }
	
	public <T> ScoredObjectList<T> getRankList(String text, String highlight, List<T> objectsList, Function<T, String> getTextFunction) {
		RichLogger.log.log(Level.INFO, "------------ " + highlight + " --------------------------------");
		
		Map<String, ScoredObject<T>> map = Maps.newHashMap();
		
		for (T object : objectsList) {
			String randomId = "" + Math.round(Math.random()*1000000);
			map.put(randomId, new ScoredObject<T>(object, randomId, getTextFunction.apply(object)));
      }
		String namespace = "" + Math.round(Math.random()*1000000);
		return rankDocumentsSimilarityToText(text, map, namespace);
   }
	
	public <T> ScoredObjectList<T> rankDocumentsSimilarityToText(String text, Map<String, ScoredObject<T>> documentIdObject, String namespace) {
		try {
			// put all the document in index
			for (String documentId : documentIdObject.keySet()) {
				searchUtils.index(documentId, documentIdObject.get(documentId).documentText, namespace);
	      }
		
			Results<ScoredDocument> searchResults = searchUtils.search(text, namespace);
		   
		   // Convert ScoredDocument to ScoredObject.
		   List<ScoredObject<T>> processedResults = Lists.newArrayList();
		   for (ScoredDocument document : searchResults) {
		   	ScoredObject<T> scoredObject = documentIdObject.get(document.getId());
		   	scoredObject.score = Iterables.getFirst(document.getSortScores(), 0.0);
		   	RichLogger.log.log(Level.INFO, "-------------------------");
		   	RichLogger.log.log(Level.INFO, "Score: [ " + scoredObject.score + " ] object: " + document.getFields("content"));
		   	processedResults.add(scoredObject);  
		   }
		   return new ScoredObjectList<T>(processedResults);
		} catch(Exception e) {
			
			RichLogger.log.log(Level.SEVERE, "fail scoring: " +  text);
			return new ScoredObjectList<T>(new ArrayList<ScoredObject<T>>());
		}
	}
}
