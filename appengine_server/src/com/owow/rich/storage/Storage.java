package com.owow.rich.storage;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PropertyContainer;
import com.google.appengine.api.datastore.Query;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.items.NGram;
import com.owow.rich.items.WebPage;

public class Storage {

	public DatastoreService	   datastore;

	final static String	      ENTITY_KIND	  = "Entity";
	final static String	      MANUAL_KIND	  = "Manual";
	final static String	      DOMAIN_PREFIX	= "From_";
	final static String	      LOG_KIND	     = "logy";
	public final static String	HOST_KEY	     = "Hosty";

	// xxX Make new DB API that might be bad
	// xxX Delete from that DB
	// xxX Couple of Views per query, ApiResponse
	// xxX Delete json

	public Storage( )
	{
		datastore = DatastoreServiceFactory.getDatastoreService();
	}
	public void saveEntitesMap(WebPage context, Map<NGram, ApiResponse> entitesMap) {
		final Set<NGram> s = entitesMap.keySet();
		for (final NGram n : s) {
			final ApiResponse ar = entitesMap.get(n);
			saveApiResponse(context, n.toString(), ar);
		}
	}
	public void saveArguableApiResponse(WebPage wp, String ngram, Entity apiResEntity) {
		// TODO new
		final Key key = KeyFactory.createKey(MANUAL_KIND, ngram);
		final Entity entity = new Entity(key);
		entity.setPropertiesFrom(apiResEntity);
		datastore.put(entity);
	}

	public void deleteFromManualDB(String... ngrams)
	{
		// TODO new
		deleteFromDB(MANUAL_KIND, ngrams);
	}

	public void deleteFromDB(String kind, String... ngrams)
	{
		// TODO new
		final Key[] keys = new Key[ngrams.length];
		for (int i = 0; i < ngrams.length; i++) {
			keys[i] = KeyFactory.createKey(kind, ngrams[i]);
		}
		datastore.delete(keys);
	}

	public void saveCustomDomainView(String domain, String ngram, ApiResponse newApiResponse) throws EntityNotFoundException
	{
		// TODO new
		Key key = KeyFactory.createKey(ENTITY_KIND, ngram);
		Entity entity = datastore.get(key);
		entity.setProperty(DOMAIN_PREFIX + domain, newApiResponse.getPropertyContainerFromApiResponse());
		datastore.put(entity);
	}

	public void saveCustomView(String domain, String ngram, int pref) throws EntityNotFoundException
	{
		Key key = KeyFactory.createKey(ENTITY_KIND, ngram);
		Entity entity = datastore.get(key);

	}

	public void saveApiResponse(WebPage webpage, String ngram, ApiResponse apiResponse)
	{
		saveApiResponse(webpage, ngram, apiResponse, false);
	}

	public void saveApiResponse(WebPage webpage, String ngram, ApiResponse apiResponse, boolean arguable)
	{
		// TODO new
		if (apiResponse == null) return;
		final Key k = KeyFactory.createKey(ENTITY_KIND, ngram);
		final Entity entity = new Entity(k);
		entity.setPropertiesFrom(apiResponse.getPropertyContainerFromApiResponse());
		entity.setProperty(HOST_KEY, webpage.getHost());
		datastore.put(entity);
		if (!arguable) return;
		saveArguableApiResponse(webpage, ngram, entity);
	}
	
	public ApiResponse getFirstMatchingNgram(WebPage context, List<NGram> ngrams)
	{
		ApiResponse apiResponse = null;
		for (final NGram nGram : ngrams) {
			apiResponse = loadEntity(context, nGram.toString());
			if (apiResponse != null) {
				break;
			}
		}
		return apiResponse;
	}

	public ApiResponse loadEntity(WebPage context, String ngram)
	{
		final PropertyContainer entity = loadPropertyContainer(context, ngram);
		if (entity != null) return ApiResponse.getApiResponseFromEntity(ngram, entity);
		return null;
	}
	public PropertyContainer loadPropertyContainer(WebPage context, String ngram)
	{
		// TODO new, used
		// TODO hanle default
		final Query query = new Query(KeyFactory.createKey(ENTITY_KIND, ngram));
		final List<Entity> listEntities = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1));

		if (listEntities.size() == 0) return null;
		Entity entity = listEntities.get(0);
		if (entity.hasProperty(DOMAIN_PREFIX + context.getHost()))
		{
			Object domainPref = entity.getProperty(DOMAIN_PREFIX + context.getHost());
			if (domainPref instanceof Long)
			{
				EmbeddedEntity emb = (EmbeddedEntity) entity.getProperty(ApiResponse.BACKUPRESPONSEKEY);
				return (EmbeddedEntity) emb.getProperty(ApiResponse.BACKUPRESPONSEKEY + String.valueOf(domainPref));
			}
			else if (domainPref instanceof EmbeddedEntity) return (PropertyContainer) domainPref;
		}
		return listEntities.get(0);
	}
	public boolean containsKey(NGram ngram) {

		try {
			Entity res = datastore.get(KeyFactory.createKey(ENTITY_KIND, ngram.toString()));
			return true;
		} catch (final EntityNotFoundException e) {
			return false;
		}
	}

	public List<Entity> queryTheDB(int offset, int length)
	{
		final Query q = new Query(ENTITY_KIND).addSort(Entity.KEY_RESERVED_PROPERTY);
		final List<Entity> l = datastore.prepare(q).asList(FetchOptions.Builder.withOffset(offset).limit(length));

		return l;
	}
}
