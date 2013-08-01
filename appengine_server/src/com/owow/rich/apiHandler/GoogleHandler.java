package com.owow.rich.apiHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.owow.rich.helper.HtmlHelper;

public class GoogleHandler implements ApiHandler {

	@Override
	public ApiResponse getData(String title, ApiType at) throws Exception {
		final JSONArray ret = new JSONArray();
		final String google = "https://www.googleapis.com/customsearch/v1?key=AIzaSyCR2RdAX0PFHXargEsInerEc-RiFkYTWPE&cx=005640612292887937759:1reryqwjvhi&q=";

		final JSONObject j = HtmlHelper.getJSONFromServerAndTitle(google, title);
		final JSONObject jo = new JSONObject();
		try {
			final JSONArray ja = j.getJSONArray("items");
			for (int i = 0; i < ja.length(); i++) {
				final JSONObject jTemp = ja.getJSONObject(i);
				ret.put(jTemp.getString("htmlSnippet"));
			}
		} catch (final JSONException jsone) {
			throw new Exception("no results");
		}
		jo.put("data", ret);

		return new ApiResponse(jo, at);
	}

	@Override
   public ApiView getView(ApiResponse fromGetData) throws Exception {
	   // TODO Auto-generated method stub
	   return null;
   }

}
