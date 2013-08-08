package com.owow.rich.entity;

import com.owow.rich.apiHandler.ApiHandler;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.items.NGram;
import com.owow.rich.items.WebPage;
import com.owow.rich.storage.Storage;
import com.owow.rich.utils.StringCompareUtils;

public class EntityRetriever {
	public ApiResponse getTopEntity(NGram ngram, ApiType at) {
		try {
			ApiHandler ah = at.createHandler();
			ApiResponse response = ah.getFirstResponse(ngram.getSearchTerm(), at);
			shouldFilter(ngram,response);
			return response;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void shouldFilter(NGram ngram, ApiResponse apiResponse) {
		// filter if miss-match-title.
		filterIfNGramDontMatchTitle(ngram, apiResponse);
		
	}
	
	public void filterIfNGramDontMatchTitle(NGram ngram, ApiResponse apiResponse) {
		for (String title : apiResponse.getTitleAndAliases()) {
	      if (StringCompareUtils.isMatch(title, ngram.getSearchTerm())) {
	      	return;
	      }
      }
		apiResponse.filterReason = "NGram and title don't match";
	}

	public ApiResponse getApiResponseFromStorage(NGram ngram, ApiType apitype, Storage storage, WebPage wp)
	{
		return storage.loadEntity(wp, ngram.toString());
	}
}
