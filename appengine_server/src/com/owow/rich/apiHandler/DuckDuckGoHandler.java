package com.owow.rich.apiHandler;

import java.util.logging.Logger;

import org.json.JSONObject;

import com.owow.rich.utils.HtmlUtil;

public class DuckDuckGoHandler extends ApiHandler {

	@Override
	public ApiResponse getFirstResponse(String title, ApiType type) throws Exception {
		final String server = "http://api.duckduckgo.com/?format=json&t=owow&q=";
		final JSONObject data = HtmlUtil.getJSONFromServerAndTitle(server, title);

		if (data.getJSONArray("RelatedTopics").length() == 0
		      && data.getString("Answer").length() == 0
		      && data.getJSONArray("Results").length() == 0) {
			Logger.getLogger(DuckDuckGoHandler.class.toString()).info("no results. title = " + title);
			return null;
		}

		final JSONObject ret = new JSONObject();
		ret.put("data", data);

		return new ApiResponse(title, ret, type);
	}

	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
