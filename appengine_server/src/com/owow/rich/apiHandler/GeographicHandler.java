package com.owow.rich.apiHandler;

import org.json.JSONObject;

import com.owow.rich.utils.HtmlUtil;

public class GeographicHandler extends ApiHandler {

	@Override
	public ApiResponse getFirstResponse(String query, ApiType type) throws Exception {
		final String server = "http://api.geonames.org/searchJSON?maxRows=10&username=owowsp&q=";

		final JSONObject serverResponse = HtmlUtil.getJSONFromServerAndTitle(server, query);

		if (serverResponse.getJSONArray("geonames").length() == 0) throw new Exception("no result");

		final JSONObject json = new JSONObject();
		json.put("data", serverResponse.getJSONArray("geonames"));
		return new ApiResponse(query, json, type);
	}

	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
