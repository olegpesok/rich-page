package com.owow.rich.apiHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.owow.rich.storage.Memcache;

/**
 * @author a
 *
 */
public class ApiRetriver {
	final static String	MEMPREFIX	     = "apiFactory/";
	final static ApiType	DEFAULT_API_TYPE	= ApiType.freebase;
	ApiRetriver( ) {}

	public static ApiResponse getApiResponse(String highlight)
	{
		return getApiResponse(highlight, DEFAULT_API_TYPE);
	}
	public static ApiResponse getApiResponse(String highlight, String method)
	{
		if (method == null) return getApiResponse(highlight);
		return getApiResponse(highlight, ApiType.create(method));
	}
	public static ApiResponse getApiResponse(String highlight, ApiType mainApiType)
	{
		try {
			highlight = URLEncoder.encode(highlight, "UTF-8");
		} catch (UnsupportedEncodingException e1) {}

		ApiView v = queryMemcacheForView(highlight, Memcache.getInstance());

		if (v != null) return new ApiResponse(null, v, null);


		List<ApiType> apiTypeList = ApiTypeManager.getApiSequence(mainApiType);
		for (ApiType apiType : apiTypeList) {
			if (apiType != null)
			{
				ApiHandler handler = apiType.createHandler();
				try {
					List<ApiResponse> apiResponseList = handler.getAllApiResponses(highlight, mainApiType);
					if (apiResponseList != null && apiResponseList.size() > 0) {
						// TODO(guti): take the best one according to the content:
						ApiResponse apiResponse = apiResponseList.get(0);
						pushMemcache(highlight, apiResponse.view, Memcache.getInstance());
						return apiResponse;
					}
				} catch (Exception e) {}
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
