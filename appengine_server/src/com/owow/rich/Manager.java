package com.owow.rich;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.entity.EntityRetriever;
import com.owow.rich.items.NGram;
import com.owow.rich.items.Token;
import com.owow.rich.items.WebPage;
import com.owow.rich.storage.Memcache;
import com.owow.rich.storage.Storage;
import com.owow.rich.utils.TokenizerUtil;

public class Manager {

	private TokenizerUtil	      tokenizer;
	private EntityRetriever	entityRetriever;
	public Storage	         storage;
	private Memcache	      mem;
	final static String	   MEMCACHE_PREFIX	= "manager/";
	public Manager( ) {
		this(new TokenizerUtil(), new EntityRetriever(), new Storage(), Memcache.getInstance());
	}

	public Manager(TokenizerUtil tokenizer, EntityRetriever entityRetriever, Storage storage, Memcache mem) {
		this.tokenizer = tokenizer;
		this.entityRetriever = entityRetriever;
		this.storage = storage;
		this.mem = mem;
	}

	public ApiResponse query(WebPage webPage, String query)
	{
		ApiResponse ar = queryMemcache(query);
		if (ar != null) return ar;

		List<Token> tokens = tokenizer.tokenize(webPage.getText());
		List<NGram> nGrams = tokenizer.combineToNGrams(tokens, 2);
		java.util.Collections.reverse(nGrams);
		for (NGram n : nGrams)
		{
			ar = storage.loadEntity(webPage, n);
			if (ar != null) break;
		}
		// TODO Save somewhere if null for future notice and not repeating useless
		// opertion. - no result
		// TODO Preproccessing managment.
		// TODO Remove unwanted phrases from getting a view
		// TODO NLP to be usefull
		// TODO Highlight breaker - break a phrase to NGRAM and search in the memcache and db
		// TODO TF/IDF
		if (ar != null) mem.set(MEMCACHE_PREFIX + query, ar.view.toString());
		return ar;
	}

	private ApiResponse queryMemcache(String query)
	{
		Object b = mem.get(MEMCACHE_PREFIX + query);
		if (b == null) return null;
		return new ApiResponse(new JSONObject(), (String) b, ApiType.freebase);
	}
	public Map<NGram, ApiResponse> processPage(WebPage webPage) throws Exception {

		// Toknaize Text (split):
		List<Token> tokens = tokenizer.tokenize(webPage.getText());
		List<NGram> nGrams = tokenizer.combineToNGrams(tokens, 2);

		Map<NGram, ApiResponse> entitesMap = Maps.newHashMap();
		for (NGram ngram : nGrams) {
			if (entitesMap.containsKey(ngram) || storage.containsKey(ngram)) continue;
			ApiResponse entity = entityRetriever.getTopEntity(ngram, ApiType.freebase);
			entitesMap.put(ngram, entity);
		}

		storage.saveEntitesMap(webPage, entitesMap);
		return entitesMap;
	}

	//
	// public com.google.appengine.api.datastore.Entity matchHighlight(WebPage
	// context, Highlight highlight) {
	// List<Token> textTokens = tokenizer.tokenize(context.text);
	// List<Token> highlightTokens = tokenizer.tokenizeSubstring(highlight.text,
	// context.text);
	// com.google.appengine.api.datastore.Entity entity =
	// storage.matchEntity(context, textTokens, highlightTokens);
	// return entity;
	// }

}
