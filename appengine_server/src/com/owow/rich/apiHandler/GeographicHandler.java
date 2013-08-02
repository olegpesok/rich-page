package com.owow.rich.apiHandler;

import org.json.JSONObject;

import com.owow.rich.utils.HtmlUtil;

public class GeographicHandler implements ApiHandler {

	@Override
	public ApiResponse getData(String title, ApiType at) throws Exception {

		final String server = "http://api.geonames.org/searchJSON?maxRows=10&username=owowsp&q=";

		final JSONObject ret = HtmlUtil.getJSONFromServerAndTitle(server, title);

		if (ret.getJSONArray("geonames").length() == 0) throw new Exception("no result");

		final JSONObject jo = new JSONObject();
		jo.put("data", ret.getJSONArray("geonames"));
		return new ApiResponse(jo, at);
	}

	@Override
   public ApiView getView(ApiResponse fromGetData) throws Exception {
	   // TODO Auto-generated method stub
	   return null;
   }

}
