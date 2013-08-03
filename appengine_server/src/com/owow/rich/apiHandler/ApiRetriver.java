package com.owow.rich.apiHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.google.appengine.datanucleus.Utils.Function;
import com.google.appengine.labs.repackaged.com.google.common.collect.Iterables;
import com.owow.rich.items.WebPage;
import com.owow.rich.storage.Memcache;
import com.owow.rich.utils.TFIDFUtil;
import com.owow.rich.utils.TFIDFUtil.ScoredObjectList;

public class ApiRetriver {
	final static String	    MEMPREFIX	      = "apiFactory/";
	final static ApiType	    DEFAULT_API_TYPE	= ApiType.freebase;
	private static TFIDFUtil	tfIdfUtil	   = new TFIDFUtil();
	ApiRetriver( ) {}

	public static ApiResponse getApiResponse(String highlight, String method, WebPage webPage)
	{
		ApiType apiType = method == null ? DEFAULT_API_TYPE : ApiType.create(method);
		return getApiResponse(highlight, apiType, webPage);
	}

	private static ApiResponse getApiResponse(String highlight, ApiType mainApiType, WebPage webPage)
	{
		try {
			highlight = URLEncoder.encode(highlight, "UTF-8");
		} catch (UnsupportedEncodingException e1) {}

		ApiView v = queryMemcacheForView(highlight, Memcache.getInstance());

		if (v != null) return new ApiResponse(null, v, null);

		List<ApiType> apiTypeList = ApiTypeManager.getApiSequence(mainApiType);
		for (ApiType apiType : apiTypeList)
			if (apiType != null)
			{
				ApiHandler handler = apiType.createHandler();
				try {
					List<ApiResponse> apiResponseList = handler.getAllApiResponses(highlight, mainApiType);
					if (apiResponseList != null && apiResponseList.size() > 0) {
						ApiResponse apiResponse = findBestMatchAccordingToContext(apiResponseList, webPage, highlight);
						pushMemcache(highlight, apiResponse.view, Memcache.getInstance());
						return apiResponse;
					}
				} catch (Exception e) {
					// TODO: log
				}
			}
		return null;
	}

	public static ApiResponse findBestMatchAccordingToContext(List<ApiResponse> apiResponseList, WebPage webPage, String highlight) {
		// If there not more then one result just returns the first result:
		if (apiResponseList.size() <= 1) {
				return Iterables.getFirst(apiResponseList, null);
		} else {
			Function<ApiResponse, String> getTextFunction = new Function<ApiResponse, String>() {
				@Override public String apply(ApiResponse response) {
					return response.text;
				}};
				
			// TODO(guti): highlight. with webpage.text
			ScoredObjectList<ApiResponse> rankedDcoumets = tfIdfUtil.getRankList(highlight, highlight, apiResponseList, getTextFunction);
			return rankedDcoumets.getBest();
		}	
   }

	public static void pushMemcache(String query, ApiView view, Memcache mem)
	{
		mem.set(MEMPREFIX + query, view.getView());
	}
	/**
	 * Query memcache for previous entries for the queries. Returns an
	 * ApiResponse object with only view
	 */
	public static ApiView queryMemcacheForView(String query, Memcache mem)
	{
		try {
			query = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e1) {}

		String view = (String) mem.get(MEMPREFIX + query);
		if (view == null) return null;
		return new ApiView(view);
	}
}
