package com.owow.rich.apiHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.api.client.http.GenericUrl;
import com.owow.rich.utils.HtmlUtil;

public class FreebaseHandler implements ApiHandler {

	String	GOOGLE_API_KEY	= "AIzaSyBjIW5540wkFEpZE2D3fx-TrLykSJ9MAiU";
	@Override
	public ApiResponse getData(String title, ApiType at) throws Exception {

		// Get the search results: (snipts)
		GenericUrl searchUrl = new GenericUrl("https://www.googleapis.com/freebase/v1/search");
		searchUrl.put("query", title);
		searchUrl.put("key", GOOGLE_API_KEY);
		final String searchData = HtmlUtil.getUrlSource(searchUrl.toString());
		JSONArray searchResponse = new JSONObject(searchData).getJSONArray("result");
		if (searchResponse.length() == 0) throw new Exception("no result for " + title);
		String mid = searchResponse.getJSONObject(0).getString("mid");
		int score = searchResponse.getJSONObject(0).getInt("score");
		boolean s = searchResponse.getJSONObject(0).has("id");

		// Get the topic: (get the full description).
		GenericUrl topicUrl = new GenericUrl("https://www.googleapis.com/freebase/v1/topic" + mid);
		topicUrl.put("filter", "/common/topic/description");
		topicUrl.put("key", GOOGLE_API_KEY);
		final String topicData = HtmlUtil.getUrlSource(topicUrl.toString());
		JSONObject topicResponse = new JSONObject(topicData);
		// PropertyNotFound
		if (!topicResponse.has("property")) throw new Exception("topic not found for " + mid + "." + s + "." + title);
		String html = topicResponse.getJSONObject("property").getJSONObject("/common/topic/description").getJSONArray("values").getJSONObject(0)
		      .getString("value");
		final JSONObject jo = topicResponse;

		return new ApiResponse(jo, /* "score: " + score + ". " + */html, at);
	}

	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
