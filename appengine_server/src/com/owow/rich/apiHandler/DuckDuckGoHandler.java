package com.owow.rich.apiHandler;

import org.json.JSONObject;

import com.owow.rich.helper.HtmlHelper;

public class DuckDuckGoHandler implements ApiHandler {

	@Override
	public ApiResponse getData(String title, ApiType at) throws Exception {


		final String server = "http://api.duckduckgo.com/?format=json&t=owow&q=";

		final JSONObject ret = HtmlHelper.getJSONFromServerAndTitle(server, title);

		if (ret.getJSONArray("RelatedTopics").length() == 0
		      && ret.getString("Answer").length() == 0
		      && ret.getJSONArray("Results").length() == 0) throw new Exception("no results");

		final JSONObject jo = new JSONObject();
		jo.put("data", ret);


		return new ApiResponse(jo, at);
	}

	@Override
   public ApiView getView(ApiResponse fromGetData) throws Exception {
	   // TODO Auto-generated method stub
	   return null;
   }

}
