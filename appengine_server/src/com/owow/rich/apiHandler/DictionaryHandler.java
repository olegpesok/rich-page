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
			} else if (tuc.getJSONObject(i).has("phrase")) texts.put(tuc.getJSONObject(i).getJSONObject("phrase").getString("text"));
		ret.put("data", texts);
		ret.put("query", title);
		ApiResponse ar = new ApiResponse(ret, at);
		ar.view = getView(ar, title);
		return ar;
	}

	public ApiView getView(ApiResponse fromGetData, String title) throws Exception
	{
		String s = title + " may refer to: <br />";
		JSONArray texts = fromGetData.json.getJSONArray("data");
		int len = texts.length() , len2 = 0;
		int criticalIndex = -1;
		for (int i = 0; i < len; i++)
		{
			Object o = texts.get(i);
			if (o instanceof JSONArray)
			{
				criticalIndex = i;
				JSONArray textArray = (JSONArray) o;
				len2 = textArray.length();
				s += "<ul>";
				for (i = 0; i < len2; i++)
					s += "<li>" + textArray.getString(i) + "</li>";
				s += "</ul>";
				break;
			}
		}
		if (criticalIndex == 0 && len == 1) return new ApiView(s);
		if (criticalIndex != -1) s += title + " may also refer to: <br />";

		s += "<ul>";
		for (int i = 0; i < len; i++)
			if (i != criticalIndex)
			{
				Object o = texts.get(i);
				if (o instanceof JSONArray)
				{
					JSONArray textArray = (JSONArray) o;
					len2 = textArray.length();
					for (i = 0; i < len2; i++)
						// s += "\t•" + textArray.getString(i) + "\n";
						s += "<li>" + textArray.getString(i) + "</li>";
				} else s += "<li>" + (String) o + "</li>";
			}

		s += "</ul>";
		return new ApiView(s);
	}
	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		String s = fromGetData.json.getString("query") + " may refer to: <br />";
		JSONArray texts = fromGetData.json.getJSONArray("data");
		int len = texts.length() , len2 = 0;
		int criticalIndex = -1;
		for (int i = 0; i < len; i++)
		{
			Object o = texts.get(i);
			if (o instanceof JSONArray)
			{
				criticalIndex = i;
				JSONArray textArray = (JSONArray) o;
				len2 = textArray.length();
				s += "<ul>";
				for (i = 0; i < len2; i++)
					s += "<li>" + textArray.getString(i) + "</li>";
				s += "</ul>";
				break;
			}
		}
		if (criticalIndex == 0 && len == 1) return new ApiView(s);
		if (criticalIndex != -1) s += String.format("{0} may also refer to: <br />", fromGetData.json.getString("query"));

		s += "<ul>";
		for (int i = 0; i < len; i++)
			if (i != criticalIndex)
			{
				Object o = texts.get(i);
				if (o instanceof JSONArray)
				{
					JSONArray textArray = (JSONArray) o;
					len2 = textArray.length();
					for (i = 0; i < len2; i++)
						s += "<li>" + textArray.getString(i) + "</li>";
				} else s += "<li>" + (String) o + "</li>";
			}

		s += "</ul>";
		return new ApiView(s);
	}
}