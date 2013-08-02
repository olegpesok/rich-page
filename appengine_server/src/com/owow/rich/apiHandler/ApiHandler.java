package com.owow.rich.apiHandler;

import java.util.List;

import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;

public abstract class ApiHandler {
	
	/**
	 * Retruns the best response from the api, 
	 * (best just according  to api specific internal scoring, and not the context scoring). 
	 */
	public abstract ApiResponse getFirstResponse(String title, ApiType apiType) throws Exception;
	
	/**
	 * Get one or more response from the api matching the highlight,
	 * e.g Freebase can return more then one entity for a given query.
	 * 
	 * Default implementation just returns one result. can be overriden by classes that return more then one response.  
	 */
	public List<ApiResponse> getAllApiResponses(String highlight, ApiType mainApiType) throws Exception {
		return Lists.newArrayList(getFirstResponse(highlight, mainApiType));
	}
	
	/**
	 * @param fromGetData
	 * @return
	 * @throws Exception
	 */
	public abstract ApiView getView(ApiResponse fromGetData) throws Exception;
	
}
