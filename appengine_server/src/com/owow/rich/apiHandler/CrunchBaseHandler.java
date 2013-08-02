package com.owow.rich.apiHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import com.owow.rich.utils.HtmlUtil;

public class CrunchBaseHandler implements ApiHandler {

	@Override
	public ApiResponse getData(String title, ApiType type) throws Exception {

		final String server = "http://api.crunchbase.com/v/1/search.js?api_key=5bfua4j796x3dthwr9sqcv4r&query=";
		final String serverDataResponse = HtmlUtil.getUrlSource(server + title);

		JSONArray jsonData = new JSONObject(serverDataResponse).getJSONArray("results");

		if (jsonData.length() == 0) throw new Exception("no crunch");

		final JSONObject ret = new JSONObject();
		ret.put("data", jsonData);

		return new ApiResponse(ret, type);
	}

	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
