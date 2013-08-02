package com.owow.rich.apiHandler;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.client.http.GenericUrl;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.owow.rich.utils.HtmlUtil;

public class FreebaseHandler extends ApiHandler {

	String	GOOGLE_API_KEY	= "AIzaSyBjIW5540wkFEpZE2D3fx-TrLykSJ9MAiU";
	private int FREEBASE_SCORE_THRESHOLD = 200;

	@Override
	public ApiResponse getFirstResponse(String highlight, ApiType apiType) throws Exception {
		JSONArray searchResponse = getFreebaseSearchResponse(highlight);
		if (searchResponse.length() > 0) {
			return getSingleResponse(searchResponse, 0, apiType);
		} else {
			return null;
		}
	}
	
	@Override
	public List<ApiResponse> getAllApiResponses(String highlight, ApiType apiType) throws Exception {
		JSONArray searchResponse = getFreebaseSearchResponse(highlight);
		List<ApiResponse> responses = Lists.newArrayList();
		for (int i = 0; i < searchResponse.length(); i++) {
			ApiResponse apiResponse = getSingleResponse(searchResponse, i, apiType);
			if (apiResponse != null) {
				responses.add(apiResponse);
			}
      }
		return responses;
	}

	private ApiResponse getSingleResponse(JSONArray searchResponse, int i, ApiType apiType) {
		try {
			int score = searchResponse.getJSONObject(i).getInt("score");
			if (score >= FREEBASE_SCORE_THRESHOLD) {
				String mid = searchResponse.getJSONObject(0).getString("mid");
				JSONObject topicResponse = getFreebseTopic(mid, apiType);
				
				if (!topicResponse.has("property")){
					//TODO: log PropertyNotFound.
					return null;
				}
				
				String description = topicResponse.getJSONObject("property").getJSONObject("/common/topic/description").getJSONArray("values").getJSONObject(0)
				      .getString("value");
				String html = "<p>" + description.replace(". ", ". </p><p>") + "</p>";
		
				return new ApiResponse(topicResponse, html, apiType, score, description);
				
			}
		} catch(Exception ex) {
			return null;
		}
	   return null;
   }

	private JSONArray getFreebaseSearchResponse(String title) throws IOException, JSONException {
	   GenericUrl searchUrl = new GenericUrl("https://www.googleapis.com/freebase/v1/search");
		searchUrl.put("query", title);
		searchUrl.put("key", GOOGLE_API_KEY);
		final String searchData = HtmlUtil.getUrlSource(searchUrl.toString());
		JSONArray searchResponse = new JSONObject(searchData).getJSONArray("result");
	   return searchResponse;
   }
	
	private JSONObject getFreebseTopic(String mid, ApiType apiType) {
		try{	
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
		// TODO Auto-generated method stub
		return null;
	}
}
