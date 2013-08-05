package com.owow.rich.apiHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Level;

import com.google.appengine.datanucleus.Utils.Function;
import com.google.appengine.labs.repackaged.com.google.common.collect.Iterables;
import com.owow.rich.RichLogger;
import com.owow.rich.items.WebPage;
import com.owow.rich.storage.Memcache;
import com.owow.rich.utils.ComparisonUtils;
import com.owow.rich.utils.NlpUtils;
import com.owow.rich.utils.ComparisonUtils.ScoredObjectList;
import com.owow.rich.utils.NlpUtils.ScoredResult;

public class ApiRetriver {
	final static String	          MEMPREFIX	      = "apiFactory/";
	final static ApiType	          DEFAULT_API_TYPE	= ApiType.freebase;
	private static ComparisonUtils	tfIdfUtil	   = new ComparisonUtils();
	private static ApiResponsePicker apiResponsePicker = new ApiResponsePicker();
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
		} catch (UnsupportedEncodingException e) {
			RichLogger.log.log(Level.SEVERE, "Can't encode highlight: " + highlight, e);
		}

		ApiView v = queryMemcacheForView(highlight, Memcache.getInstance());

		if (v != null) return new ApiResponse(highlight, null, v, null);

		List<ApiType> apiTypeList = ApiTypeManager.getApiSequence(mainApiType);
		for (ApiType apiType : apiTypeList)
			if (apiType != null)
			{
				ApiHandler handler = apiType.createHandler();
				try {
					List<ApiResponse> apiResponseList = handler.getAllApiResponses(highlight, mainApiType);
					if (apiResponseList != null && apiResponseList.size() > 0) {
						if (apiResponseList.get(0).goodEnough == true) return apiResponseList.get(0);

						ApiResponse apiResponse = apiResponsePicker.choseResult(apiResponseList, webPage, highlight);
						if (apiResponse != null) pushMemcache(highlight, apiResponse.view, Memcache.getInstance());
						return apiResponse;
					}
				} catch (Exception e) {
					RichLogger.log.log(Level.SEVERE, "Fail to process handler: " + apiType.nickname + " for query: " + highlight, e);
				}
			}
		return null;
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
