package com.owow.rich.apiHandler;

import java.util.logging.Logger;

import org.json.JSONObject;

import com.owow.rich.utils.HtmlUtil;

public class CkanHandler extends ApiHandler {

	@Override
	public ApiResponse getFirstResponse(String title, ApiType type) throws Exception {

		final String server = "http://demo.ckan.org/api/3/action/package_search?q=";
		JSONObject ret = HtmlUtil.getJSONFromServerAndTitle(server, title);

		if (!ret.getBoolean("success"))
		{
			Logger.getLogger(CkanHandler.class.toString()).warning("failed to get results. title = " + title);
			return null;
		}

		ret = ret.getJSONObject("result");
		if (ret.getJSONArray("results").length() == 0) {
			Logger.getLogger(CkanHandler.class.toString()).info("no results, title = " + title);
			return null;
		}

		final JSONObject jo = new JSONObject();
		jo.put("data", ret);

		return new ApiResponse(title, jo, type);
	}

	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		return null;
	}
}
