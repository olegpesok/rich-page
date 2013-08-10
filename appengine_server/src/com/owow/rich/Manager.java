package com.owow.rich;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.datanucleus.util.StringUtils;

import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;
import com.google.appengine.labs.repackaged.com.google.common.collect.Sets;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiRetriver;
import com.owow.rich.apiHandler.ApiView;
import com.owow.rich.items.Feedback;
import com.owow.rich.items.NGram;
import com.owow.rich.items.Query;
import com.owow.rich.items.Result.Results;
import com.owow.rich.items.WebPage;
import com.owow.rich.retriever.EntityRetriever;
import com.owow.rich.retriever.GeneralRetriever;
import com.owow.rich.retriever.LocalRetriver;
import com.owow.rich.storage.PreviousResultsCache;
import com.owow.rich.storage.Storage;
import com.owow.rich.utils.NameExtractor;
import com.owow.rich.utils.RelatedLinkSearch;
import com.owow.rich.utils.TokenizerUtil;

public class Manager {

	private TokenizerUtil	     tokenizer;
	private NameExtractor nameExtractor = new NameExtractor();
	public Storage	              storage;


	public final static int	    NGRAM_LEN	   = 2;
	public PreviousResultsCache	cache;

	public Manager( ) {
		this(new TokenizerUtil(), new Storage(), new PreviousResultsCache());
	}

	public Manager(TokenizerUtil tokenizer, Storage storage, PreviousResultsCache cache) {
		this.tokenizer = tokenizer;
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
		// Looks for we have the full query(highlight) in the cache
		ApiView apiView = cache.queryMemcacheForApiView(query);

		if (apiView != null) return new ApiResponse(query, null, apiView, null);
		// TODO Change this {
		ApiResponse ar = storage.loadEntity(webPage, query);
		if (ar != null) return ar; // ApiView can be an empty string, inorder to
		                           // eliminate
		// }
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
	
	/**
	 * Get results, first try to see if query was process, and can find previous results,
	 * if not gets live results from the external services.
	 */
	public Results getFastResults(Query query) {
		// If not new query bring the previous results:
		Results results = LocalRetriver.retirve(query);
		if (!results.containsFastEntityResults) {
			results.addFastEntityResults(EntityRetriever.fastRetrieve(query));
		}
		if(!results.containsGeneralResults) {
			results.addGeneralResults(GeneralRetriever.retrieve(query));
		}
		LocalRetriver.saveResults(query, results);
		return results;
	}
	
	/**
	 * Do deep retrieval for the query (e.g gets a lot of results from Freebase etc.)
	 * And then  
	 */
	public void deepQueryProcess(Query query, boolean incudeGeneralResults) {
		Results results = LocalRetriver.retirve(query);
		if (!results.containsDeepEntityResults) {
			results.addDeepEntityResults(EntityRetriever.deepRetrieve(query));
		}
		if (incudeGeneralResults && !results.containsGeneralResults) {
			results.addGeneralResults(GeneralRetriever.retrieve(query));
		}
		// TODO: additional processing.
		
		LocalRetriver.saveResults(query, results);
	}
	
	/**
	 * according to feedback from the user implicit or explicit we update the ranking of the results.
	 */
	public void updateResults(List<Feedback> feedbackList) {
		LocalRetriver.learnFromFeedback(feedbackList);
	}
	
	public void processPage(WebPage webPage) throws Exception {
		// We extract the names, as the query we want to pre-process in advance:
		List<List<String>> namesLists = nameExtractor.getNameExtractor(webPage.url);
		Set<NGram> allNGrams = Sets.newHashSet();
		for (List<String> namesList : namesLists) {
			NGram nGram = tokenizer.toNgram(namesList);
			allNGrams.add(nGram);
		}

		for (NGram ngram : allNGrams) {
			if (StringUtils.isEmpty(ngram.searchTerm)) {
				continue;
			}
			deepQueryProcess(new Query(ngram.searchTerm,webPage), false) ;
		}

		RelatedLinkSearch.index(webPage);
	}

}
