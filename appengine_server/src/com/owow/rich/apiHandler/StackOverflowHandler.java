package com.owow.rich.apiHandler;

import org.json.JSONObject;

import com.owow.rich.helper.HtmlHelper;

public class StackOverflowHandler implements ApiHandler {

	@Override
	public ApiResponse getData(String title, ApiType at) throws Exception {
		// TODO Not Working

		final String server = "https://api.stackexchange.com/2.1/similar?order=desc&sort=relevance&site=stackoverflow&title=";
		String extra = HtmlHelper.getHttpsUrlSource(server + title);
		final JSONObject ret = new JSONObject(extra);
		if (ret.getJSONArray("items").length() == 0) throw new Exception("no items");
		final JSONObject jo = new JSONObject();
		jo.put("data", ret.getJSONArray("items"));

		throw new UnsupportedOperationException();
		//return new ApiResponse(jo, at);
	}

	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
