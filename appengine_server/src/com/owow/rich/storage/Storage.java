package com.owow.rich.storage;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiResponseFactory;
import com.owow.rich.items.NGram;
import com.owow.rich.items.WebPage;

public class Storage {

	public DatastoreService	datastore;

	final static String	   ENTITY_KIND	= "Entity";
	final static String	   LOG_KIND	   = "logy";
	public Storage( )
	{
		datastore = DatastoreServiceFactory.getDatastoreService();
	}
	public void saveEntitesMap(WebPage context, Map<NGram, ApiResponse> entitesMap) {
		// TODO Make context differ
		Set<NGram> s = entitesMap.keySet();
		for (NGram n : s)
		{
			Key k = KeyFactory.createKey(ENTITY_KIND, n.toString());
			Entity e = new Entity(k);
			ApiResponse ar = entitesMap.get(n);
			if (ar != null) e.setPropertiesFrom(ApiResponseFactory.getPropertyContainerFromApiResponse(ar));

			datastore.put(e);
		}
	}

	public void saveLog(String user, String ip, String query, String url, boolean resultOk)
	{
		Key key = KeyFactory.createKey(LOG_KIND, user + "." + query + "." + new java.util.Date().getTime());
		Entity e = new Entity(key);
		e.setProperty("user", user);
		e.setProperty("ip", ip);
		e.setProperty("q", query);
		e.setProperty("ok", resultOk);
		e.setProperty("url", url);

		datastore.put(e);
	}

	public ApiResponse loadEntity(WebPage context, NGram n)
	{
		Query query = new Query(KeyFactory.createKey(ENTITY_KIND, n.toString()));
		List<Entity> liste = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1));

		if (liste.size() == 0) return null;
		return ApiResponseFactory.getApiResponseFromEntity(liste.get(0));
	}

	public boolean containsKey(NGram ngram) {

		try {
			datastore.get(KeyFactory.createKey(ENTITY_KIND, ngram.toString()));
		} catch (EntityNotFoundException e)
		{
			return false;
		}
		return true;
	}
}
