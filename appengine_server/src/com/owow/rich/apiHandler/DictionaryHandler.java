package com.owow.rich.apiHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import com.owow.rich.helper.HtmlHelper;

public class DictionaryHandler implements ApiHandler {

	@Override
	public ApiResponse getData(String title, ApiType at) throws Exception {
		// can add &pretty=true server for checking
		final String server = "http://glosbe.com/gapi/translate?from=eng&dest=eng&format=json&phrase=";
		final JSONObject data = HtmlHelper.getJSONFromServerAndTitle(server, title);
		final JSONObject ret = new JSONObject();
		final JSONArray texts = new JSONArray();
		final JSONArray tuc = data.getJSONArray("tuc");
		for (int i = 0; i < tuc.length(); i++)
			if (tuc.getJSONObject(i).has("meanings")) {
				final JSONArray meanings = tuc.getJSONObject(i).getJSONArray(
				      "meanings");
				final JSONArray meanings2 = new JSONArray();
				for (int j = 0; j < meanings.length(); j++)
					meanings2.put(meanings.getJSONObject(j).getString(
					      "text"));
				texts.put(meanings2);
			} else if (tuc.getJSONObject(i).has("phrase")) texts.put(tuc.getJSONObject(i).getJSONObject("phrase")
			      .getString("text"));
		ret.put("data", texts);

		return new ApiResponse(ret, at);

	}

	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
