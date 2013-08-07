package com.owow.rich.items;

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;

public class WebPage {

	public String	html;
	public String	text;
	public String	url;
	public WebPage(String html, String text, String url) {
		this.html = html;
		this.text = text;
		this.url = url;
	}

	public String getHost()
	{
		try {
			return new URL(url).getHost();
		} catch (MalformedURLException exception)
		{
			return "";
		}
	}
	public String getText()
	{
		try {
			if (text == null)
			{
				org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
				text = doc.body().text();
				//
				// AlchemyAPI a = AlchemyAPI
				// .GetInstanceFromString("dc86318ce4f5cf5ae2872376afe43940938d7edf");
				// Document d = a.URLGetText(url);
				// if
				// (d.getDocumentElement().getElementsByTagName("status").item(0).getTextContent().equals("ERROR"))
				// {
				// RichLogger.log.log(Level.SEVERE,"Error, API alchemy failed at retriving the text");
				// } else {
				// text =
				// d.getDocumentElement().getElementsByTagName("text").item(0).getTextContent();
				// }
			}
			return text;

		} catch (Exception e) {
			return "error";
			// e.printStackTrace();
		}
	}

}
