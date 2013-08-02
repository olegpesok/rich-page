package com.owow.rich.apiHandler;

import org.json.JSONObject;

import com.owow.rich.utils.HtmlUtil;

public class StackOverflowHandler implements ApiHandler {

	@Override
	public ApiResponse getData(String query, ApiType type) throws Exception {
		// TODO Not Working

		final String server = "https://api.stackexchange.com/2.1/similar?order=desc&sort=relevance&site=stackoverflow&title=";
		String serverResponse = HtmlUtil.getHttpsUrlSource(server + query);
		final JSONObject data = new JSONObject(serverResponse);
		if (data.getJSONArray("items").length() == 0) throw new Exception("no items");
		final JSONObject json = new JSONObject();
		json.put("data", data.getJSONArray("items"));

		throw new UnsupportedOperationException();
		//return new ApiResponse(jo, at);
	}

	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
