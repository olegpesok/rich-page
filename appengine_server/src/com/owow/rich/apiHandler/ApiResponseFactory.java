package com.owow.rich.apiHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONObject;

import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PropertyContainer;
import com.google.appengine.api.datastore.Text;
import com.owow.rich.memcache.Memcache;

public class ApiResponseFactory {

	final static String	JSONKEY	        = "json";
	final static String	VIEWKEY	        = "view";
	final static String	APITYPEKEY	     = "apitype";
	final static String	MEMPREFIX	     = "apiFactory/";
	final static ApiType	DEFAULT_API_TYPE	= ApiType.freebase;
	ApiResponseFactory( ) {}

	public static PropertyContainer getPropertyContainerFromApiResponse(ApiResponse ar)
	{
		PropertyContainer pc = new EmbeddedEntity();
		pc.setProperty(JSONKEY, new Text(ar.json.toString()));

		pc.setProperty(VIEWKEY, new Text(ar.view.getView().toString()));
		pc.setProperty(APITYPEKEY, ar.myType.getIdentifyer());

		return pc;
	}

	public static ApiResponse getApiResponseFromEntity(Entity e)
	{
		if (!e.hasProperty(APITYPEKEY)) return null;
		JSONObject jo = new JSONObject(e.getProperty(JSONKEY));
		ApiView av = new ApiView((String) e.getProperty(VIEWKEY));
		ApiType at = ApiType.create((String) e.getProperty(APITYPEKEY));
		return new ApiResponse(jo, av, at);
	}

	public static ApiResponse getApiResponse(String query)
	{
		return getApiResponse(query, DEFAULT_API_TYPE);
	}
	public static ApiResponse getApiResponse(String query, String method)
	{
		if (method == null) return getApiResponse(query);
		return getApiResponse(query, ApiType.create(method));
	}
	public static ApiResponse getApiResponse(String query, ApiType at)
	{
		try {
			query = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e1) {}

		ApiResponse fin = queryMemcache(query, Memcache.getInstance());
		if (fin != null) return fin;

		List<ApiType> seq = ApiTypeFactory.getApiSequence(at);
		for (ApiType apit : seq)
			if (apit != null)
			{
				ApiHandler ah = apit.createHandler();
				ApiResponse ar;
				try {
					ar = ah.getData(query, at);
					fin = ar;
					break;
				} catch (Exception e) {}
			}
		if (fin != null) pushMemcache(query, fin.view.toString(), Memcache.getInstance());
		return fin;
	}

	public static void pushMemcache(String query, String view, Memcache m)
	{
		m.set(MEMPREFIX + query, view);
	}
	public static ApiResponse queryMemcache(String query, Memcache m)
	{
		try {
			query = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e1) {}
		String view = (String) m.get(MEMPREFIX + query);
		if (view == null) return null;
		return new ApiResponse(new JSONObject(), view, DEFAULT_API_TYPE);
	}
}
