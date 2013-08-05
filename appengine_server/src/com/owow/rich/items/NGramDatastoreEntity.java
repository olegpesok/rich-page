package com.owow.rich.items;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.google.appengine.api.datastore.Text;
import com.owow.rich.apiHandler.ApiResponse;

public class NGramDatastoreEntity implements Serializable {
	/**
	 *
	 */
	private static final long	            serialVersionUID	= -7941614767910191021L;
	// Muse: domain url ngram {list of freebase - title, id, score} {wikipidea}
	public String	                        domain;
	public String	                        url;
	public String	                        ngram;
	public ArrayList<FreebaseSimpleResult>	freebaseResults;
	public WikipediaSimpleResult	         wikiresult	     = null;
	/**
	 * Sets a new instance
	 *
	 * @param uri
	 *           -URL you want to save
	 * @param ngram
	 *           - the ngram
	 * @param freebase
	 *           - the ApiResponses you got from the ngram by freebase
	 * @param wiki
	 *           - the ApiResponse you got from the ngram by wikipedia
	 */
	public NGramDatastoreEntity(URL uri, String ngram, List<ApiResponse> freebase, ApiResponse wiki)
	{
		domain = uri.getHost();
		url = uri.toString();
		this.ngram = ngram;
		freebaseResults = new ArrayList<FreebaseSimpleResult>();
		for (ApiResponse ar : freebase)
			freebaseResults.add(FreebaseSimpleResult.createFromApiResponse(ar));
		if (wiki != null) wikiresult = WikipediaSimpleResult.createFromApiResponse(wiki);
	}

	/**
	 * @author Shalti
	 *
	 *         The data we want to save from single freebase apiresponse
	 */
	public static class FreebaseSimpleResult
	{
		public String	id;
		public int		score;
		// For long strings, appengine uses Text object
		public Text		text;
		public static FreebaseSimpleResult createFromApiResponse(ApiResponse ar)
		{
			FreebaseSimpleResult result = new FreebaseSimpleResult();
			result.text = new Text(ar.view.toString());
			result.score = ar.apiInternalScore;
			try {
				result.id = ar.json.getString("id");
			} catch (JSONException e) {}
			return result;
		}
	}

	/**
	 * @author Shalti
	 *
	 *         The data we want to save about wikipedia
	 */
	public static class WikipediaSimpleResult
	{
		public String	title;
		// For long strings, appengine uses Text to save
		public Text		text;
		public static WikipediaSimpleResult createFromApiResponse(ApiResponse ar)
		{
			WikipediaSimpleResult result = new WikipediaSimpleResult();
			result.text = new Text(ar.view.toString());
			result.title = ar.title;
			return result;
		}
	}
}
