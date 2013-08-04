package com.owow.rich.apiHandler;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.client.http.GenericUrl;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.owow.rich.RichLogger;
import com.owow.rich.utils.HtmlUtil;

public class FreebaseHandler extends ApiHandler {

	String	   GOOGLE_API_KEY	          = "AIzaSyBjIW5540wkFEpZE2D3fx-TrLykSJ9MAiU";
	private int	FREEBASE_SCORE_LOW_THRESHOLD	= 0;
	private int	FREEBASE_SCORE_CAN_SKIP_CONTEXT_SCORE_THRESHOLD	= 500;

	/**
	 * Return the first result from freebase.
	 */
	@Override
	public ApiResponse getFirstResponse(String highlight, ApiType apiType) throws Exception {
		JSONArray searchResponse = getFreebaseSearchResponse(highlight);
		if (searchResponse.length() > 0) return getSingleResponse(searchResponse.getJSONObject(0), apiType);
		else return null;
	}

	/**
	 * Return all the results for this highlight.
	 */
	@Override
	public List<ApiResponse> getAllApiResponses(String highlight, ApiType apiType) throws Exception {
		JSONArray searchResponse = getFreebaseSearchResponse(highlight);

		List<ApiResponse> responses = Lists.newArrayList();
		for (int i = 0; i < searchResponse.length(); i++) {
			
			ApiResponse apiResponse = getSingleResponse(searchResponse.getJSONObject(i), apiType);
			
			if (apiResponse != null) {
				RichLogger.log.log(Level.INFO, "----------FB------------");
		   	RichLogger.log.log(Level.INFO, "Score: [ " + apiResponse.apiInternalScore + " ] object: " + apiResponse.text);
		   	
				responses.add(apiResponse);
			}
		}
		return responses;
	}

	/**
	 * Give a query we return all the results for this query.
	 */
	private JSONArray getFreebaseSearchResponse(String title) throws IOException, JSONException {
		GenericUrl searchUrl = new GenericUrl("https://www.googleapis.com/freebase/v1/search");
		searchUrl.put("query", title);
		searchUrl.put("key", GOOGLE_API_KEY);
		final String searchData = HtmlUtil.getUrlSource(searchUrl.toString());
		JSONArray searchResponse = new JSONObject(searchData).getJSONArray("result");

		return searchResponse;
	}

	/**
	 * Retrieve the result and create the ApiResponse from it.
	 *
	 * Return null if exception is thrown or if Freebase's score is below the
	 * FREEBASE_SCORE_THRESHOLD.
	 */
	private ApiResponse getSingleResponse(JSONObject searchResult, ApiType apiType) {
		try {
			int score = searchResult.getInt("score");
<<<<<<< HEAD
			String title = searchResult.getString("name");
			if (score >= FREEBASE_SCORE_THRESHOLD) {
=======
			if (score >= FREEBASE_SCORE_LOW_THRESHOLD) {
>>>>>>> c3f18e85158fb399daa2f33a23576bdc2f2da156
				String mid = searchResult.getString("mid");
				JSONObject topicResponse = getFreebseTopic(mid, apiType);

				if (!topicResponse.has("property")) // TODO: log PropertyNotFound.
				return null;

				String description = topicResponse.getJSONObject("property").getJSONObject("/common/topic/description").getJSONArray("values").getJSONObject(0)
				      .getString("value");
				String html = "<p>" + description.replace(". ", ". </p><p>") + "</p>";

				ApiResponse apiResponse = new ApiResponse(topicResponse, html, apiType, score, description);
				if(apiResponse.apiInternalScore >= FREEBASE_SCORE_CAN_SKIP_CONTEXT_SCORE_THRESHOLD) {
					apiResponse.goodEnough = true;
				}
				return apiResponse;

			}
		} catch (Exception ex) {
			return null;
		}
		return null;
	}

	/**
	 * Reterive the result from free base according to the mid.
	 */
	private JSONObject getFreebseTopic(String mid, ApiType apiType) {
		try {
			GenericUrl topicUrl = new GenericUrl("https://www.googleapis.com/freebase/v1/topic" + mid);
			topicUrl.put("filter", "/common/topic/description");
			topicUrl.put("key", GOOGLE_API_KEY);
			String topicData;

			topicData = HtmlUtil.getUrlSource(topicUrl.toString());

			return new JSONObject(topicData);
		} catch (Exception e) {
			// TODO: log exception.
			return null;
		}
	}

	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		
		return null;
	}
}
