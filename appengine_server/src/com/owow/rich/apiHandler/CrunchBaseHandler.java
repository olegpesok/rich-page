package com.owow.rich.apiHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import com.owow.rich.utils.HtmlUtil;

public class CrunchBaseHandler implements ApiHandler {

	@Override
	public ApiResponse getData(String title, ApiType at) throws Exception {

		final String server = "http://api.crunchbase.com/v/1/search.js?api_key=5bfua4j796x3dthwr9sqcv4r&query=";
		final String data = HtmlUtil.getUrlSource(server + title);

		JSONArray ret = new JSONObject(data).getJSONArray("results");

		if (ret.length() == 0) throw new Exception("no crunch");

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
