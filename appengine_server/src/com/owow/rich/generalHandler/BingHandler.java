package com.owow.rich.generalHandler;

import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.client.util.Base64;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.owow.rich.storage.Storage;
import com.owow.rich.utils.HtmlUtil;

public class BingHandler {

	final static String	appkey	= "XsXRBpgRfreCe/rmjpQNWpm5LgZTubxMPj/Yshr+xGw=";
	final static String	bingKIND	= "Bing";
	private JSONObject parseJson(JSONObject bingData) throws JSONException {
		JSONObject bingResults = bingData.getJSONObject("d").getJSONArray("results").getJSONObject(0);

		return bingResults;
	}

	public JSONObject getResults(String highlight, Storage storage) throws Exception {
		if (storage.containsKey(bingKIND, highlight))
		{
			Entity e = storage.getSavedEntity(bingKIND, highlight);
			return new JSONObject(((Text) e.getProperty("json")).getValue());
		}
		String server = "https://api.datamarket.azure.com/Bing/Search/v1/Composite?$format=json&Sources=%27web%2Bnews%2Bimage%2Bnews%27&Query=%27"
		      + highlight
		      + "%27&Options=%27EnableHighlighting%27&Market=%27en-US%27&Adult=%27Strict%27&ImageFilters=%27Size%3ASmall%2BAspect%3ASquare%27&NewsSortBy=%27Relevance%27";
		byte[] keyBytes = Base64.encodeBase64((appkey + ":" + appkey).getBytes());
		String accountKeyEnc = new String(keyBytes);

		URL url = new URL(server);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
		String data = HtmlUtil.getUrlSource(urlConnection);
		JSONObject jo = new JSONObject(data);
		jo = parseJson(jo);
		saveJson(highlight, jo, storage);
		return jo;
	}

	private void saveJson(String highlight, JSONObject jo, Storage storage) {
		Entity entity = storage.getNewEntity(bingKIND, highlight);
		entity.setProperty("json", new Text(jo.toString()));
		storage.save(entity);
	}
	public String fixBing(String data)
	{
		int length = data.length();

		return data.replace("", "</strong>").replace("", "<strong>");
		// StringBuilder sb = new StringBuilder();
		// boolean seen = false;
		// for (int i = 0; i < length; i++)
		// {
		// if (data.charAt(i) != '') {
		// sb.append(data.charAt(i));
		// } else if (!seen)
		// {
		// seen = true;
		// sb.append("<strong>");
		// } else
		// {
		// sb.append("</strong>");
		// seen = false;
		// }
		// }
		//	return sb.toString();
	}

	public String getString(String highlight) throws Exception {
		String server = "https://api.datamarket.azure.com/Bing/Search/v1/Composite?$format=json&Sources=%27web%2Bnews%2Bimage%2Bnews%27&Query=%27"
		      + highlight
		      + "%27&Options=%27EnableHighlighting%27&Market=%27en-US%27&Adult=%27Strict%27&ImageFilters=%27Size%3ASmall%2BAspect%3ASquare%27&NewsSortBy=%27Relevance%27";
		byte[] keyBytes = Base64.encodeBase64((appkey + ":" + appkey).getBytes());
		String accountKeyEnc = new String(keyBytes);

		URL url = new URL(server);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
		urlConnection.setRequestProperty("Accept-Charset", "UTF-16");
		String data = HtmlUtil.getUrlSource(urlConnection);
		return data;
	}
}
