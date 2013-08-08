package com.owow.rich;

import java.util.List;
import java.util.Map;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiRetriver;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.apiHandler.ApiView;
import com.owow.rich.entity.EntityRetriever;
import com.owow.rich.items.NGram;
import com.owow.rich.items.Token;
import com.owow.rich.items.WebPage;
import com.owow.rich.storage.PreviousResultsCache;
import com.owow.rich.storage.Storage;
import com.owow.rich.utils.NameExtractor;
import com.owow.rich.utils.TokenizerUtil;

public class Manager {

	private TokenizerUtil	     tokenizer;
	private EntityRetriever	     entityRetriever;
	private NameExtractor	     nameExtractor	= new NameExtractor();
	public Storage	              storage;

	public final static int	     NGRAM_LEN	    = 2;
	private PreviousResultsCache	cache;

	public Manager( ) {
		this(new TokenizerUtil(), new EntityRetriever(), new Storage(), new PreviousResultsCache());
	}

	public Manager(TokenizerUtil tokenizer, EntityRetriever entityRetriever, Storage storage, PreviousResultsCache cache) {
		this.tokenizer = tokenizer;
		this.entityRetriever = entityRetriever;
		this.storage = storage;
		this.cache = cache;
	}

	/**
	 * Try to find a matching result to the query, look in the cache, and
	 * previous results in the db before sending request to external services
	 * (e.g Free-base) in order to save time and money.
	 * 
	 * @param webPage
	 *           - The context of the query
	 * @param query
	 *           - The highlight to which we are looking for a match
	 * @param method
	 *           - What is the method for retrieving the response.
	 */
	public ApiResponse getApiResponse(WebPage webPage, String query, String method)
	{
		query = TokenizerUtil.cleanUnwantedChars(query);
		// Looks for we have the full query(highlight) in the cache.
		ApiView apiView = cache.queryMemcacheForApiView(query);

		if (apiView != null) return new ApiResponse(query, null, apiView, null);

		List<NGram> nGrams = tokenizer.getAllNgram(query, NGRAM_LEN);

		ApiResponse apiResponse = null;
		if (nGrams.size() > 3)
		{
			// Looks if we have any of the ngrams of the query in the cache.
			apiResponse = cache.getFirstMatchingNgram(nGrams);

			// db queries
			if (apiResponse == null) {
	         apiResponse = storage.getFirstMatchingNgram(webPage, nGrams);
         }
		}
		// Do live retrieve.
		if (apiResponse == null) {
	      apiResponse = ApiRetriver.getApiResponse(query, method, webPage);
      }

		if (apiResponse != null) {
	      cache.save(query, apiResponse.view.toString());
      }
		return apiResponse;
	}

	public Map<NGram, ApiResponse> processPage(WebPage webPage) throws Exception {

		// We extract the names:
		List<List<String>> namesLists = nameExtractor.getNameExtractor(webPage.url);
		List<NGram> allNGrams = Lists.newArrayList();
		for (List<String> namesList : namesLists) {
			String names = Joiner.on(" ").join(namesList);
			List<Token> tokens = tokenizer.tokenize(names);
			List<NGram> nGrams = tokenizer.combineToNGrams(tokens, NGRAM_LEN);
			allNGrams.addAll(nGrams);
		}

		Map<NGram, ApiResponse> entitesMap = Maps.newHashMap();
		for (NGram ngram : allNGrams) {
			if (entitesMap.containsKey(ngram) || storage.containsKey(ngram.toString())) {
	         continue;
         }
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
