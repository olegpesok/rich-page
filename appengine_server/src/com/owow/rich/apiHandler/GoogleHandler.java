package com.owow.rich.apiHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.owow.rich.utils.HtmlUtil;

public class GoogleHandler extends ApiHandler {

	@Override
	public ApiResponse getFirstResponse(String query, ApiType type) throws Exception {
		final JSONArray data = new JSONArray();
		final String google = "https://www.googleapis.com/customsearch/v1?key=AIzaSyCR2RdAX0PFHXargEsInerEc-RiFkYTWPE&cx=005640612292887937759:1reryqwjvhi&q=";

		final JSONObject serverData = HtmlUtil.getJSONFromServerAndTitle(google, query);
		final JSONObject ret = new JSONObject();
		try {
			final JSONArray items = serverData.getJSONArray("items");
			for (int i = 0; i < items.length(); i++) {
				final JSONObject anItem = items.getJSONObject(i);
				data.put(anItem.getString("htmlSnippet"));
			}
		} catch (final JSONException jsone) {
			throw new Exception("no results");
		}
		ret.put("data", data);

		return new ApiResponse(ret, type);
	}

	@Override
   public ApiView getView(ApiResponse fromGetData) throws Exception {
	   // TODO Auto-generated method stub
	   return null;
   }

}
