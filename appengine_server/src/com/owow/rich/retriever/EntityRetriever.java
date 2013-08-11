package com.owow.rich.retriever;

import java.util.List;

import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.owow.rich.apiHandler.ApiHandler;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.items.Query;
import com.owow.rich.items.Result;
import com.owow.rich.utils.StringCompareUtils;

public class EntityRetriever {

	public static List<Result> retrieve(Query query) {
		List<Result> resultList = Lists.newArrayList();
		return resultList;
	}

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
		filterIfNGramDontMatchTitle(query, apiResponse);
	}

	private static void filterIfNGramDontMatchTitle(String query, ApiResponse apiResponse) {
		for (String title : apiResponse.getTitleAndAliases()) {
			if (StringCompareUtils.isMatch(title, query)) { return; }
		}
		apiResponse.filterReason = "NGram and title don't match";
	}

	public static List<Result> deepRetrieve(Query query) {
		// TODO: get more results in deep retieve:
		return fastRetrieve(query);
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
