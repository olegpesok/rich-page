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
	public static ApiResponse getApiResponse(String highlight, ApiType at)
	{
		try {
			highlight = URLEncoder.encode(highlight, "UTF-8");
		} catch (UnsupportedEncodingException e1) {}

		ApiView v = queryMemcacheForView(highlight, Memcache.getInstance());

		if (v != null) return new ApiResponse(null, v, null);

		ApiResponse ret = null;

		List<ApiType> seq = ApiTypeManager.getApiSequence(at);
		for (ApiType apit : seq)
			if (apit != null)
			{
				ApiHandler handler = apit.createHandler();
				try {
					ret = handler.getData(highlight, at);
					if (ret != null) break;
				} catch (Exception e) {}
			}
		if (ret != null) pushMemcache(highlight, ret.view, Memcache.getInstance());
		return ret;
	}

	public static void pushMemcache(String query, ApiView view, Memcache mem)
	{
		mem.set(MEMPREFIX + query, view.getView());
	}
	/**
	 * Query memcache for previous entries for the queries. Returns an
	 * ApiResponse object with only view
	 *
	 * @param query
	 * @param mem
	 * @return
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
