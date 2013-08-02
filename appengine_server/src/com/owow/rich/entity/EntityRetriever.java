package com.owow.rich.entity;

import com.owow.rich.apiHandler.ApiHandler;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.items.NGram;
import com.owow.rich.items.WebPage;
import com.owow.rich.storage.Storage;

public class EntityRetriever {
	public ApiResponse getTopEntity(NGram ngram, ApiType at) {
		try {
			ApiHandler ah = at.createHandler();
			return ah.getData(ngram.getSearchTerm(), at);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public ApiResponse getTopEntity2(NGram ngram, ApiType at) {
		try {
			ApiHandler ah = at.createHandler();
			return ah.getData(ngram.getSearchTerm(), at);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	
	public ApiResponse getApiResponseFromStorage(NGram ngram, ApiType apitype, Storage storage, WebPage wp)
	{
		return storage.loadEntity(wp, ngram);
	}
}
