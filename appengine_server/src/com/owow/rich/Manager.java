package com.owow.rich;

import java.util.List;
import java.util.Map;

import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiRetriver;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.apiHandler.ApiView;
import com.owow.rich.entity.EntityRetriever;
import com.owow.rich.items.NGram;
import com.owow.rich.items.Token;
import com.owow.rich.items.WebPage;
import com.owow.rich.storage.Memcache;
import com.owow.rich.storage.Storage;
import com.owow.rich.utils.TokenizerUtil;

public class Manager {

	private TokenizerUtil	tokenizer;
	private EntityRetriever	entityRetriever;
	public Storage	         storage;
	private Memcache	      memcache;
	final static String	   MEMCACHE_PREFIX	= "manager/";
	public final static int	NGRAM_LEN	    = 2;
	public Manager( ) {
		this(new TokenizerUtil(), new EntityRetriever(), new Storage(), Memcache.getInstance());
	}

	public Manager(TokenizerUtil tokenizer, EntityRetriever entityRetriever, Storage storage, Memcache memcache) {
		this.tokenizer = tokenizer;
		this.entityRetriever = entityRetriever;
		this.storage = storage;
		this.memcache = memcache;
	}

	public ApiResponse queryTheDBAndMemcache(WebPage webPage, String query)
	{

		ApiView av = queryMemcacheForApiView(query);
		if (av != null) return new ApiResponse(null, av, null);

		ApiResponse apiResponse = null;
		List<Token> tokens = tokenizer.tokenize(query);
		List<NGram> nGrams = tokenizer.combineToNGrams(tokens, NGRAM_LEN);
		java.util.Collections.reverse(nGrams);
		String qNgram;
		// Memcache queries
		for (NGram nGram : nGrams)
		{
			qNgram = nGram.toString();
			av = queryMemcacheForApiView(qNgram);
			if (av == null) av = ApiRetriver.queryMemcacheForView(qNgram, memcache);

			if (av != null) {
				apiResponse = new ApiResponse(null, av, null);
				break;
			}
		}
		boolean getIntoMem = av == null;

		// db queries
		if (av == null) for (NGram nGram : nGrams)
		{
			qNgram = nGram.toString();
			apiResponse = storage.loadEntity(webPage, nGram);

			if (apiResponse != null) break;
		}
		// TODO Save somewhere if null for future notice and not repeating useless
		// opertion. - no result
		// TODO Preproccessing managment.
		// TODO Remove unwanted phrases from getting a view
		// TODO NLP to be usefull
		// TODO Highlight breaker - break a phrase to NGRAM and search in the
		// memcache and db
		// TODO TF/IDF
		if (apiResponse != null && getIntoMem) memcache.set(MEMCACHE_PREFIX + query, apiResponse.view.toString());
		return apiResponse;
	}
	private ApiView queryMemcacheForApiView(String query)
	{
		Object viewString = memcache.get(MEMCACHE_PREFIX + query);
		if (viewString == null) return null;
		return new ApiView((String) viewString);
	}

	public Map<NGram, ApiResponse> processPage(WebPage webPage) throws Exception {

		// Toknaize Text (split):
		List<Token> tokens = tokenizer.tokenize(webPage.getText());
		List<NGram> nGrams = tokenizer.combineToNGrams(tokens, NGRAM_LEN);

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
