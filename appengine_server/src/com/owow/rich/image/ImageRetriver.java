package com.owow.rich.image;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.client.util.Base64;
import com.owow.rich.items.Image;
import com.owow.rich.utils.HtmlUtil;

public class ImageRetriver {

	final static String	appkey	= "XsXRBpgRfreCe/rmjpQNWpm5LgZTubxMPj/Yshr+xGw=";
	public static List<Image> getImages(String highlight) throws JSONException, IOException {
		String server = "https://api.datamarket.azure.com/Bing/Search/v1/Image?$format=json&$top=5&Query=%27" + highlight + "%27";
		byte[] keyBytes = Base64.encodeBase64((appkey + ":" + appkey).getBytes());
		String accountKeyEnc = new String(keyBytes);

		URL url = new URL(server);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
		String data = HtmlUtil.getUrlSource(urlConnection);
		JSONObject jo = new JSONObject(data);
		return parseJson(jo);
	}

	private static LinkedList<Image> parseJson(JSONObject bingData) throws JSONException
	{
		LinkedList<Image> images = new LinkedList<Image>();
		JSONArray results = bingData.getJSONObject("d").getJSONArray("results");
		JSONObject result;
		for (int i = 0; i < results.length(); i++) {
			result = results.getJSONObject(i);
			String title = result.getString("Title");
			String src = result.getString("SourceUrl");
			int height = Integer.parseInt(result.getString("Height"));
			int width = Integer.parseInt(result.getString("Width"));
			images.add(new Image(title, null, src, height, width));
		}
		return images;
	}
}
