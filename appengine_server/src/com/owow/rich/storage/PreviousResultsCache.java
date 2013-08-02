package com.owow.rich.storage;

import java.util.List;

import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiRetriver;
import com.owow.rich.apiHandler.ApiView;
import com.owow.rich.items.NGram;

public class PreviousResultsCache {
	private Memcache memcache = Memcache.getInstance();
	final static String	   MEMCACHE_PREFIX	= "manager/";
	
	public ApiResponse getFirstMatchingNgram(List<NGram> nGrams) {
		for (NGram nGram : nGrams)
		{
			String nGramString = nGram.toString();
			ApiView apiView = queryMemcacheForApiView(nGramString);
			if (apiView == null) apiView = ApiRetriver.queryMemcacheForView(nGramString, memcache);

			if (apiView != null) {
				return new ApiResponse(null, apiView, null);
			}
		}
		return null;
   }
	
	public ApiView queryMemcacheForApiView(String query)
	{
		Object viewString = memcache.get(MEMCACHE_PREFIX + query);
		if (viewString == null) return null;
		return new ApiView((String) viewString);
	}

	public void save(String query, String apiView) {
		memcache.set(MEMCACHE_PREFIX + query, apiView); 	
   }
}
