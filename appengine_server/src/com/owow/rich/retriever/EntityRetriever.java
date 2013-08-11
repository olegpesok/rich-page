package com.owow.rich.retriever;

import java.util.Collections;
import java.util.List;

import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.owow.rich.RichLogger;
import com.owow.rich.apiHandler.ApiHandler;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.items.Query;
import com.owow.rich.items.Result;
import com.owow.rich.utils.FrequencyUtil;
import com.owow.rich.utils.StringCompareUtils;

public class EntityRetriever {


	public static ApiResponse getTopApiResponse(String query, ApiType at) {
		try {
			ApiHandler apiHandler = at.createHandler();
			ApiResponse response = apiHandler.getFirstResponse(query, at);
			shouldFilter(query, response);
			return response;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static void shouldFilter(String query, ApiResponse apiResponse) {
		// filter if miss-match-title.
		filterIfTooFrequent(query, apiResponse);
	}

	private static void filterIfTooFrequent(String query, ApiResponse apiResponse) {
	   int frequency = FrequencyUtil.getFrequency(query);
	   if (frequency > 0) {
	   	apiResponse.filterReason = "Too Frequent " + frequency;
	   }
   }

	public static List<Result> deepRetrieve(Query query) {
		// TODO: add crunch base and other
//		List<Result> crunchResutls = getAllApiResponse(query, ApiType.crunch);
//		if (crunchResutls.size() > 0) {
//			crunchResutls = crunchResutls;
//		}
		
		List<Result> results = getAllApiResponse(query, ApiType.freebase);
		for (Result result : results) {
	      scoreResult(result, query);
      }
		Collections.sort(results);
		
		// Temporary for debug:
		if (results.size() <= 0) {
			return results;
		}
		
		return results;
	}

	private static void scoreResult(Result result, Query query) {
		for (String title : result.getTitleAndAliases()) {
			result.score =  StringCompareUtils.getTitleSimilarityScore(title, query.highlight);
			//TODO: add more scores, tfidf, wi
		}
   }
	
	public static List<Result> getAllApiResponse(Query query, ApiType apiType) {
		List<Result> results = Lists.newArrayList();
		try {
			List<ApiResponse> responses = apiType.createHandler().getAllApiResponses(query.highlight, apiType);
			for (ApiResponse response : responses) {
				results.addAll( response.getResults(query) );
         }
		} catch (Exception ex) {
			RichLogger.logException("in get all api response", ex);
		}
		return results;
	}

	public static List<Result> fastRetrieve(Query query) {
		ApiResponse apiResponse = getTopApiResponse(query.highlight, ApiType.freebase);
		if (apiResponse != null) {
			return apiResponse.getResults(query);
		} else {
			return Lists.newArrayList();
		}
	}
//
//	public ApiResponse getApiResponseFromStorage(NGram ngram, ApiType apitype, Storage storage, WebPage wp)
//	{
//		return storage.loadEntity(wp, ngram.toString());
//	}
}
