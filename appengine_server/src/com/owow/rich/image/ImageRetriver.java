package com.owow.rich.image;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.client.util.Base64;
import com.owow.rich.utils.HtmlUtil;

public class ImageRetriver {

	final static String	appkey	= "XsXRBpgRfreCe/rmjpQNWpm5LgZTubxMPj/Yshr+xGw=";
	public static JSONObject getImages(String highlight) throws JSONException, IOException {
		String server = "https://api.datamarket.azure.com/Bing/Search/v1/Image?$format=json&$top=5&Query=%27" + highlight + "%27";
		byte[] keyBytes = Base64.encodeBase64((appkey + ":" + appkey).getBytes());
		String accountKeyEnc = new String(keyBytes);

		URL url = new URL(server);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
		String data = HtmlUtil.getUrlSource(urlConnection);
		JSONObject jo = new JSONObject(data);
		return jo;
	}
}
