package com.owow.rich.items;

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;

import com.googlecode.objectify.annotation.Embed;

@Embed
public class WebPage{

	public String	html;
	public String	text;
	public String	title;
	public String	url;
	
	public WebPage(String html, String text, String url) {
		this.html = html;
		this.text = text;
		this.url = url;
	}

	public WebPage( ) {
		html = null;
		text = null;
		url = "";
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
	public String getTitle() {
		try {
			if (title == null)
			{
				org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
				title = doc.title();
			}
			return title;

		} catch (Exception e) {
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
			}
			return text;

		} catch (Exception e) {
			return "";
		}
	}

}
