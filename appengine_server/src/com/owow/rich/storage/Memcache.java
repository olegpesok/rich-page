package com.owow.rich.storage;

import java.util.logging.Level;

import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class Memcache {
	MemcacheService	syncCache;
	static Memcache	instance;
	Memcache( ) {
		syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	}
	

	public static Memcache getInstance()
	{
		if (instance == null) return instance = new Memcache();
		return instance;
	}
	public Object get(String key)
	{
		return syncCache.get(key); // read from cache
	}

	public void set(String key, Object value)
	{
		syncCache.put(key, value); // populate cache
	}

	public void set(String key, Object value, Expiration e)
	{
		syncCache.put(key, value, e); // populate cache
	}
}
