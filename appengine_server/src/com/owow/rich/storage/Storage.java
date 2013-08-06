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
import com.owow.rich.items.NGram;
import com.owow.rich.items.NGramDatastoreEntity;
import com.owow.rich.items.WebPage;

public class Storage {

	public DatastoreService	datastore;

	final static String	   ENTITY_KIND	= "Entity";
	final static String	   LOG_KIND	   = "logy";
	public final static String	   HOST_KEY	   = "Hosty";
	public Storage( )
	{
		datastore = DatastoreServiceFactory.getDatastoreService();
	}
	public void saveEntitesMap(WebPage context, Map<NGram, ApiResponse> entitesMap) {
		// TODO Make context differ
		Set<NGram> s = entitesMap.keySet();
		for (NGram n : s) {
			ApiResponse ar = entitesMap.get(n);
			saveApiResponse(context, n.toString(), ar);
		}
	}
	public void saveApiResponse(WebPage wp, String ngram, ApiResponse apiResponse)
	{
		if (apiResponse == null) return;
		Key k = KeyFactory.createKey(ENTITY_KIND, ngram);
		Entity e = new Entity(k);
		e.setPropertiesFrom(apiResponse.getPropertyContainerFromApiResponse());
		e.setProperty(HOST_KEY, wp.getHost());
		datastore.put(e);

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

	public ApiResponse getFirstMatchingNgram(WebPage context, List<NGram> ngrams)
	{
		ApiResponse apiResponse = null;
		for (NGram nGram : ngrams) {
			apiResponse = loadEntity(context, nGram.toString());
			if (apiResponse != null) break;
		}
		return apiResponse;

	}
	public ApiResponse loadEntity(WebPage context, String ngram)
	{
		Entity entity = loadEntityJustEntity(context, ngram);
		if (entity != null) return ApiResponse.getApiResponseFromEntity(ngram, entity);
		return null;
	}
	public Entity loadEntityJustEntity(WebPage context, String ngram)
	{
		Query query = new Query(KeyFactory.createKey(ENTITY_KIND, ngram));
		List<Entity> liste = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1));

		if (liste.size() == 0) return null;
		return liste.get(0);
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

	public List<Entity> queryTheDB(int offset, int length)
	{
		Query q = new Query(ENTITY_KIND).addSort(Entity.KEY_RESERVED_PROPERTY);
		List<Entity> l = datastore.prepare(q).asList(FetchOptions.Builder.withOffset(offset).limit(length));

		return l;
		// Unimplemented
	}
	public void saveNGramDatastoreEntity(NGramDatastoreEntity entity)
	{

		// Key key = KeyFactory.createKey(AG_KIND, entity.ngram + "." +
		// entity.domain);
		// Entity e = new Entity(key);
		// // e.setProperty("entity", entity);
		// datastore.put(e);
	}

}
