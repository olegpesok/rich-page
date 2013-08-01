package com.owow.rich.apiHandler;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.owow.rich.helper.HtmlHelper;

public class QuandlHandler implements ApiHandler {

	@Override
	public ApiResponse getData(String title, ApiType at) throws Exception {
		JSONObject ret;
		final String server = "http://www.quandl.com/api/v1/datasets.json?query=";
		ret = HtmlHelper.getJSONFromServerAndTitle(server, title);
		final JSONArray ja = ret.getJSONArray("docs");
		if (ja.length() == 0) throw new Exception("no results");

		final JSONObject jo = new JSONObject();
		jo.put("data", ja);
		return new ApiResponse(jo, at);
	}

	// TODO understand what todo
	public JSONObject scrapeQuandl(final String sourceCode, final String code) throws JSONException, IOException
	{
		return new JSONObject(HtmlHelper.getUrlSource("http://www.quandl.com/api/v1/datasets/" + sourceCode + "/" + code + ".json"));
	}

	@Override
   public ApiView getView(ApiResponse fromGetData) throws Exception {
	   // TODO Auto-generated method stub
	   return null;
   }

}
