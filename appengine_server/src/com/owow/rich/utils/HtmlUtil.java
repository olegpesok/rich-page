package com.owow.rich.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.xml.sax.SAXException;

public class HtmlUtil {

	HtmlUtil( ) {}
	public static String getTextFromHtml(final String html) {
		return Jsoup.parse(html).text();
	}

	public static JSONArray pullElementsFromHtml(String tag, String data) {
		final JSONArray ja = new JSONArray();
		final String starttag = "<" + tag;
		final String endtag = "</" + tag + ">";
		int s = data.indexOf(starttag);
		int f = data.indexOf(endtag);
		while (s >= 0 && f > s) {
			ja.put(data.substring(s, f + endtag.length()));

			s = data.indexOf(starttag, f + 1);
			f = data.indexOf(endtag, f + 1);
		}
		return ja;
	}
	public static org.w3c.dom.Document getDocumentFromUrl(final String url)
	      throws ParserConfigurationException, MalformedURLException,
	      SAXException, IOException {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		final DocumentBuilder db = dbf.newDocumentBuilder();
		final org.w3c.dom.Document doc = db.parse(new URL(url).openStream());
		return doc;
	}

	public static String getUrlSource(final String url) throws IOException {
		final URL yahoo = new URL(url);
		final URLConnection yc = yahoo.openConnection();
		yc.setConnectTimeout(60 * 1000);

		return getUrlSource(yc);
	}
	public static String getUrlSource(URLConnection urlconnection) throws UnsupportedEncodingException, IOException
	{
		final BufferedReader in = new BufferedReader(new InputStreamReader(
		      urlconnection.getInputStream(), "UTF-8"));

		String inputLine;
		final StringBuilder a = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			a.append(inputLine);
		}
		in.close();

		return a.toString();
	}

	public static String getHttpsUrlSource(final String url) throws Exception
	{
		final URL yahoo = new URL(url);

		final BufferedReader in = new BufferedReader(new InputStreamReader(
		      yahoo.openStream()));

		String inputLine;
		final StringBuilder a = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			a.append(inputLine);
		}
		in.close();

		return a.toString();
	}

	public static String encodeHTML(String s, Set<Character> chars)
	{
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if ((c > 127 || c == '"' || c == '<' || c == '>') && !chars.contains(c))
			{
				out.append("&#" + (int) c + ";");
			}
			else
			{
				out.append(c);
			}
		}
		return out.toString();
	}

	public static JSONObject getJSONFromServerAndTitle(final String server, final String title)
	      throws JSONException, IOException {
		return getJSON(server + title);
	}
	public static JSONObject getJSON(final String url)
	      throws JSONException, IOException {
		String res = getUrlSource(url);
		return new JSONObject(res);
	}

}
