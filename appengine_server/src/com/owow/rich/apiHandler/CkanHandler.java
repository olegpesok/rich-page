package com.owow.rich.apiHandler;

import org.json.JSONObject;

import com.owow.rich.utils.HtmlUtil;

public class CkanHandler extends ApiHandler {

	@Override
	public ApiResponse getFirstResponse(String title, ApiType type) throws Exception {

		final String server = "http://demo.ckan.org/api/3/action/package_search?q=";
		JSONObject ret = HtmlUtil.getJSONFromServerAndTitle(server, title);

		if (!ret.getBoolean("success")) throw new Exception("failed to get results from ckan");

		ret = ret.getJSONObject("result");
		if (ret.getJSONArray("results").length() == 0) throw new Exception("No results");

		final JSONObject jo = new JSONObject();
		jo.put("data", ret);

		return new ApiResponse(title, jo, type);
	}

	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		throw new UnsupportedOperationException();
	}
}
