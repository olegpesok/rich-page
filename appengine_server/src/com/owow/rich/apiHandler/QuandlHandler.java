package com.owow.rich.apiHandler;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.owow.rich.utils.HtmlUtil;

public class QuandlHandler extends ApiHandler {

	@Override
	public ApiResponse getFirstResponse(String query, ApiType type) throws Exception {
		JSONObject serverResponse;
		final String server = "http://www.quandl.com/api/v1/datasets.json?query=";
		serverResponse = HtmlUtil.getJSONFromServerAndTitle(server, query);
		final JSONArray data = serverResponse.getJSONArray("docs");
		if (data.length() == 0) throw new Exception("no results");

		final JSONObject json = new JSONObject();
		json.put("data", data);
		return new ApiResponse(query, json, type);
	}

	// TODO understand what todo
	public JSONObject scrapeQuandl(final String sourceCode, final String code) throws JSONException, IOException
	{
		return new JSONObject(HtmlUtil.getUrlSource("http://www.quandl.com/api/v1/datasets/" + sourceCode + "/" + code + ".json"));
	}

	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
