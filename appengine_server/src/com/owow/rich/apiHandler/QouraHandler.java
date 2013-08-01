package com.owow.rich.apiHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class QouraHandler implements ApiHandler {

	@Override
	public ApiResponse getData(String title, ApiType at) throws Exception {
		final JSONObject jo = new JSONObject();
		final JSONArray ret = new JSONArray();
		final String server = "http://www.quora.com/search?q=";
		final String url = server + title;
		final Connection.Response res = Jsoup.connect(url).timeout(5000)
		      .ignoreHttpErrors(true).followRedirects(true).execute();

		final Document d = res.parse();
		Elements divs = d.getElementsByAttributeValueContaining(
		      "class", "question_link");
		jo.put("nums", divs.size());
		for (int i = 0; i < divs.size(); i++) {
			Element div = divs.get(i);
			final JSONObject question = new JSONObject();
			question.put("questionHTML", div.html());
			question.put("questionText", div.text());
			question.put("link",
			      "http://www.quora.com" + div.attr("href"));
			div = div
			      .parent()
			      .parent()
			      .parent()
			      .getElementsByAttributeValueContaining("class",
			            "row").first();
			question.put("snippetText", div.text());
			question.put("snippetHTML", div.html());
			ret.put(question);
		}

		divs = d.getElementsByAttributeValue("class",
		      "search_result_snippet");
		if (divs.size() != 0) {
			jo.put("snippet", divs.first().text());
			jo.put("snippetHTML", divs.first().html());
		}
		jo.put("data", ret);
		return new ApiResponse(jo, at);
	}

	@Override
   public ApiView getView(ApiResponse fromGetData) throws Exception {
	   // TODO Auto-generated method stub
	   return null;
   }

}
