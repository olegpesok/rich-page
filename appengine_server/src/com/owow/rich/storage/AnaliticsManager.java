package com.owow.rich.storage;

import java.util.List;

import com.google.api.client.util.Base64;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.owow.rich.items.WebPage;

public class AnaliticsManager {
	public DatastoreService	datastore;
	public Storage	         storage;
	public final static int	DEFAULTLENGTH	= 1000;
	public AnaliticsManager(Storage storage) {
		datastore = storage.datastore;
		this.storage = storage;
	}
	public String saveLog(String user, String ip, String query, WebPage wp, boolean resultOk)
	{
		String keyval = query + "." + ip + "." + wp.getHost() + "." + new java.util.Date().getTime();
		final Key key = KeyFactory.createKey(Storage.LOG_KIND, keyval);
		final Entity e = new Entity(key);
		e.setProperty("user", user);
		e.setProperty("ip", ip);
		e.setProperty("q", query);
		e.setProperty("ok", resultOk);
		e.setProperty("url", wp.url);
		e.setProperty("host", wp.getHost());
		e.setProperty("time", new java.util.Date().getTime());
		datastore.put(e);
		keyval = Base64.encodeBase64String(keyval.getBytes());
		return keyval;
	}
	public void addData(String keyvalue, boolean didHeClicked, boolean didHeScrolled, long timeSpent) throws EntityNotFoundException
	{
		keyvalue = new String(Base64.decodeBase64(keyvalue));
		Key key = KeyFactory.createKey(Storage.LOG_KIND, keyvalue);
		Entity entity = datastore.get(key);
		entity.setProperty("clicked", didHeClicked);
		entity.setProperty("scolled", didHeScrolled);
		entity.setProperty("timespent", timeSpent);
		datastore.put(entity);
	}

	public List<Entity> queryTheDB(WebPage domain, int offset, int length)
	{
		Query query = getDefaultQuerySorting().setFilter(getDomainFilter(domain));
		return queryTheDB(query, offset, length);
	}
	private Filter getDomainFilter(WebPage domain)
	{
		return new FilterPredicate("domain", FilterOperator.EQUAL, domain.getHost());
	}

	private Filter getTimeFilter(long minTime, long maxTime)
	{
		final Filter minTimeFilter = new FilterPredicate("time", FilterOperator.GREATER_THAN_OR_EQUAL, minTime);
		final Filter maxTimeFilter = new FilterPredicate("time", FilterOperator.LESS_THAN_OR_EQUAL, maxTime);
		return CompositeFilterOperator.and(minTimeFilter, maxTimeFilter);
	}

	public List<Entity> queryTheDB(Query query, int offset, int length)
	{
		return datastore.prepare(query).asList(FetchOptions.Builder.withOffset(offset).limit(length));
	}

	public List<Entity> queryTheDB(WebPage domain, String highligh, long minTimestamp, long maxTimestamp, int offset, int length)
	{
		Filter highlightFilter = new FilterPredicate("q", FilterOperator.EQUAL, highligh);
		Query query = getComplexQuery(domain, minTimestamp, maxTimestamp, highlightFilter);
		return queryTheDB(query, offset, length);
	}
	public List<Entity> queryTheDB(WebPage domain, String ip, String url, long minTimestamp, long maxTimestamp, int offset, int length)
	{
		final Filter ipFilter = new FilterPredicate("ip", FilterOperator.EQUAL, ip);
		final Filter urlFilter = new FilterPredicate("url", FilterOperator.EQUAL, url);
		Query query = getComplexQuery(domain, minTimestamp, maxTimestamp, CompositeFilterOperator.and(ipFilter, urlFilter));
		return queryTheDB(query, offset, length);
	}
	private Query getComplexQuery(WebPage domain, long minTimestamp, long maxTimestamp, Filter special)
	{
		Query query = getDefaultQuerySorting();
		Filter combinedFilter = CompositeFilterOperator.and(getDomainFilter(domain), getTimeFilter(minTimestamp, maxTimestamp), special);
		query.setFilter(combinedFilter);
		return query;
	}
	private Query getDefaultQuerySorting()
	{
		return new Query(Storage.LOG_KIND).addSort(Entity.KEY_RESERVED_PROPERTY).addSort("q");
	}
	public int countAmount(WebPage domain, String ip, String url, long minTimestamp, long maxTimestamp) {
		final Filter ipFilter = new FilterPredicate("ip", FilterOperator.EQUAL, ip);
		final Filter urlFilter = new FilterPredicate("url", FilterOperator.EQUAL, url);
		return countResults(getComplexQuery(domain, minTimestamp, maxTimestamp, CompositeFilterOperator.and(ipFilter, urlFilter)));
	}
	public int countAmount(WebPage domain, String ip, long minTimestamp, long maxTimestamp) {
		final Filter ipFilter = new FilterPredicate("ip", FilterOperator.EQUAL, ip);
		return countResults(getComplexQuery(domain, minTimestamp, maxTimestamp, ipFilter));
	}
	/**
	 * Count how many result a query have. Caution! this might talk long time in
	 * case of lots of results.
	 * 
	 * @param query
	 * @return
	 */
	private int countResults(Query query)
	{
		query.setKeysOnly();
		final List<Entity> l = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		query.clearKeysOnly();
		return l.size();
	}
}
