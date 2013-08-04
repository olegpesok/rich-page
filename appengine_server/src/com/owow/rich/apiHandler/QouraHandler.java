package com.owow.rich.apiHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class QouraHandler extends ApiHandler {

	@Override
	public ApiResponse getFirstResponse(String query, ApiType type) throws Exception {
		final JSONObject json = new JSONObject();
		final JSONArray data = new JSONArray();
		final String server = "http://www.quora.com/search?q=";
		final String url = server + query;
		final Connection.Response res = Jsoup.connect(url).timeout(5000).ignoreHttpErrors(true).followRedirects(true).execute();

		final Document document = res.parse();
		Elements divs = document.getElementsByAttributeValueContaining("class", "question_link");
		json.put("nums", divs.size());
		for (int i = 0; i < divs.size(); i++) {
			Element div = divs.get(i);
			final JSONObject question = new JSONObject();
			question.put("questionHTML", div.html());
			question.put("questionText", div.text());
			question.put("link", "http://www.quora.com" + div.attr("href"));
			div = div.parent().parent().parent().getElementsByAttributeValueContaining("class", "row").first();
			question.put("snippetText", div.text());
			question.put("snippetHTML", div.html());
			data.put(question);
		}

		divs = document.getElementsByAttributeValue("class", "search_result_snippet");
		if (divs.size() != 0) {
			json.put("snippet", divs.first().text());
			json.put("snippetHTML", divs.first().html());
		}
		json.put("data", data);
		return new ApiResponse(query, json, type);
	}

	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
