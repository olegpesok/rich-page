package com.owow.rich.storage;

import java.sql.Date;
import java.util.logging.Level;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class PersistentCahce {
	private static MemcacheService memcahceService;
	private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static boolean disable = false;
	private final static String DEFAULT_NAME_SPACE = "DEFAULT_NS";
	private final static String KIND = "CACHE_KIND";
	
	static {
		memcahceService = MemcacheServiceFactory.getMemcacheService();
		memcahceService.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	}
	
	public static Object get(String key, String namespace) throws EntityNotFoundException
	{
		if (!disable) {
			if(namespace == null) {
				namespace = DEFAULT_NAME_SPACE;
			}
			
			String full_key = namespace + "_" + key;
			// Look for the key in cahce
			Object cahcedObject = memcahceService.get(full_key);
			if (cahcedObject != null) {
				return cahcedObject;
			}
			
			// look for the key in the db
	      Key DataStoreKey = KeyFactory.createKey(KIND, full_key);
	      Entity entity = datastore.get(DataStoreKey);
	      if (entity != null) {
	      	Object value = entity.getProperty("value");
	      	if (value != null) {
	      		set(key, value, namespace);
	      		return value;
	      	}
	      }
	      return null;
		} else {
			return null;
		}
	}

	public static void set(String key, Object value, String namespace)
	{
		if (!disable) {
			if(namespace == null) {
				namespace = DEFAULT_NAME_SPACE;
			}
			String full_key = namespace + "_" + key;
			
			memcahceService.put(full_key, value);
			
			// look for the key in the db
	      Key dataStoreKey = KeyFactory.createKey(KIND, full_key);
	      Entity entity = new Entity(dataStoreKey);
	      entity.setProperty("value", value);
	      datastore.put(entity);
		}
	}

}
