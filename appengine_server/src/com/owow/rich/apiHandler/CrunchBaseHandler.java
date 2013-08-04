package com.owow.rich.apiHandler;

import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.owow.rich.utils.HtmlUtil;

public class CrunchBaseHandler extends ApiHandler {

	@Override
	public ApiResponse getFirstResponse(String title, ApiType type) throws Exception {

		final String server = "http://api.crunchbase.com/v/1/search.js?api_key=5bfua4j796x3dthwr9sqcv4r&query=";
		final String serverDataResponse = HtmlUtil.getUrlSource(server + title);

		JSONArray jsonData = new JSONObject(serverDataResponse).getJSONArray("results");

		if (jsonData.length() == 0) {
			Logger.getLogger(CrunchBaseHandler.class.toString()).info("no results. title = " + title);
			return null;
		}

		final JSONObject ret = new JSONObject();
		ret.put("data", jsonData);

		return new ApiResponse(title, ret, type);
	}

	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
