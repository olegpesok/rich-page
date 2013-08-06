package com.owow.rich.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Date;
import java.util.logging.Level;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
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
			try{
				if(namespace == null) {
					namespace = DEFAULT_NAME_SPACE;
				}
				
				String full_key = namespace + "_" + key;
				// Look for the key in cahce
				Object cahcedObject = memcahceService.get(full_key);
				if (cahcedObject != null) {
					Object deserilizedCahcedObject = deserlize((String)cahcedObject);
					return deserilizedCahcedObject;
				}
				
				// look for the key in the db
		      Key DataStoreKey = KeyFactory.createKey(KIND, full_key);
		      Entity entity = datastore.get(DataStoreKey);
		      if (entity != null) {
		      	Object value = entity.getProperty("value");
		      	Text valueText = (Text) value;
		      	String valueString = valueText.getValue(); 
		      	Object deserilizedValue = deserlize(valueString);
		      	
		      	if (value != null) {
		      		set(key, deserilizedValue, namespace);
		      		return deserilizedValue;
		      	}
		      }
			} catch(Exception ex) {
				return null;
			}
	      
		}
			return null;
	}	

	private static String serlize(Object object) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(buffer);
		oos.writeObject(object);
		oos.flush();
		oos.close();
		return new String(buffer.toByteArray());
	}
	
	private static Object deserlize(String byteArray) throws IOException, ClassNotFoundException {
		
		ByteArrayInputStream in = new ByteArrayInputStream(byteArray.getBytes());
		ObjectInputStream ois = new ObjectInputStream(in);
		Object obj = ois.readObject();
		return obj;
	}

	public static void set(String key, Object value, String namespace)
	{
		try { 
			if (!disable) {
				if(namespace == null) {
					namespace = DEFAULT_NAME_SPACE;
				}
				String full_key = namespace + "_" + key;
				String serlizeValue = serlize(value);
				
				memcahceService.put(full_key, serlizeValue);
				
				// look for the key in the db
		      Key dataStoreKey = KeyFactory.createKey(KIND, full_key);
		      Entity entity = new Entity(dataStoreKey);
		      
		      entity.setUnindexedProperty("value", new Text(serlizeValue));
		      datastore.put(entity);
			}
		} catch(Exception e) {
			e = e;
		}
	}

}
