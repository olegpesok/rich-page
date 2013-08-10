package com.owow.rich.storage;

import java.util.Date;
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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.apiHandler.ApiView;
import com.owow.rich.items.NGram;
import com.owow.rich.items.WebPage;

public class Storage {

	public DatastoreService	   datastore;

	final static String	      ENTITY_KIND	  = "Entity";
	final static String	      MANUAL_KIND	  = "Manual";
	final static String	      DOMAIN_PREFIX	= "From_";
	final static String	      LOG_KIND	     = "logy";
	public final static String	HOST_KEY	     = "Hosty";
	public final static String	PAGE_URL	     = "Page_Url";
	public final static String	CREATED	     = "created";
	public final static String	FILTERED	     = "filtered";
	public final static String	FILTER_REASON	= "filtered";

	// xxX Make new DB API that might be bad
	// xxX Delete from that DB
	// xxX Couple of Views per query, ApiResponse
	// xxX Delete json

	public Storage( )
	{
		datastore = DatastoreServiceFactory.getDatastoreService();
	}
	public void dontShowNGram(String ngram, String reason)
	{
		ApiResponse ar = new ApiResponse(ngram, null, new ApiView(""), ApiType.custom);
		saveApiResponse(new WebPage(), ngram, ar, false, true);
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

	public void saveCustomDomainView(String domain, String ngram, int pref) throws EntityNotFoundException
	{
		Key key = KeyFactory.createKey(ENTITY_KIND, ngram);
		Entity entity = datastore.get(key);
		entity.setProperty(DOMAIN_PREFIX + domain, pref);
		datastore.put(entity);
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
		entity.setProperty(PAGE_URL, webpage.url);
		entity.setProperty(CREATED, new Date());
		datastore.put(entity);
		if (!arguable) return;
		saveArguableApiResponse(webpage, ngram, entity);
	}

	public void saveApiResponse(WebPage webpage, String ngram, ApiResponse apiResponse, boolean arguable, boolean saveCurrentAsBackup)
	{
		// TODO new
		if (apiResponse == null) return;
		final Key k = KeyFactory.createKey(ENTITY_KIND, ngram);
		Entity entity = null;

		if (saveCurrentAsBackup && containsKey(k))
		{
			try {
				entity = datastore.get(k);
			} catch (EntityNotFoundException entityNotFoundException)
			{}
			EmbeddedEntity embeddedEntity;
			long length;
			if (entity.hasProperty(ApiResponse.BACKUPRESPONSEKEY)) {
				embeddedEntity = (EmbeddedEntity) entity.getProperty(ApiResponse.BACKUPRESPONSEKEY);
				length = (Long) embeddedEntity.getProperty("length");
			}
			else {
				embeddedEntity = new EmbeddedEntity();
				embeddedEntity.setProperty("length", 0);
				length = 0;
			}

			embeddedEntity.setProperty("length", ++length);
			PropertyContainer pc = ApiResponse.getApiResponseFromEntity(entity).getPropertyContainerFromApiResponse();
			EmbeddedEntity ee = new EmbeddedEntity();
			ee.setPropertiesFrom(pc);
			embeddedEntity.setProperty(ApiResponse.BACKUPRESPONSEKEY + length, pc);

			entity.setProperty(ApiResponse.BACKUPRESPONSEKEY, embeddedEntity);
		}
		else {
			entity = new Entity(k);
		}

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
		// final Filter filteredFilter = new FilterPredicate("filtered",
		// FilterOperator.NOT_EQUAL, true);
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
		if (listEntities.get(0).hasProperty("dont")) return null;
		return listEntities.get(0);
	}
	public boolean containsKey(Key key)
	{
		final Query query = new Query(key).setKeysOnly();
		return !datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1)).isEmpty();
	}
	public boolean containsKey(String kind, String ngram) {
		return containsKey(KeyFactory.createKey(kind, ngram));
	}

	public List<Entity> queryTheDB(int offset, int length, String host)
	{
		final Query q = new Query(ENTITY_KIND)/* .addSort("created") */.addSort(Entity.KEY_RESERVED_PROPERTY);
		if (host != null && !host.isEmpty())
		{
			Filter hostFilter = new FilterPredicate(HOST_KEY,
			      FilterOperator.EQUAL, host);
			q.setFilter(hostFilter);
		}
		final List<Entity> l = datastore.prepare(q).asList(FetchOptions.Builder.withOffset(offset).limit(length));
		return l;
	}

	// Bing
	public Entity getNewEntity(String kind, String key)
	{
		return new Entity(KeyFactory.createKey(kind, key));
	}
	public Entity getSavedEntity(String kind, String key) throws EntityNotFoundException
	{
		return datastore.get(KeyFactory.createKey(kind, key));
	}
	public boolean containsNgramKey(String ngram) {
		return containsKey(ENTITY_KIND, ngram);
	}
	public void save(Entity entity) {
		datastore.put(entity);
	}
}
