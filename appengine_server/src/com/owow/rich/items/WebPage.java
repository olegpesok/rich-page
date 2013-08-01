package com.owow.rich.items;

import org.w3c.dom.Document;

import com.alchemyapi.api.AlchemyAPI;

public class WebPage {

	public String	html;
	public String	text;
	public String	url;
	public WebPage(String html, String text, String url) {
		this.text = text;
		this.url = url;
	}

	public String getText()
	{
		try {
			if (text == null)
			{
				AlchemyAPI a = AlchemyAPI
				      .GetInstanceFromString("dc86318ce4f5cf5ae2872376afe43940938d7edf");
				Document d = a.URLGetText(url);
				if (d.getDocumentElement().getElementsByTagName("status").item(0).getTextContent().equals("ERROR")) throw new Exception(
				      "Error, API alchemy failed at retriving the text");
				text = d.getDocumentElement().getElementsByTagName("text").item(0).getTextContent();
			}
			return text;

		} catch (Exception e) {
			return "error";
			// e.printStackTrace();
		}
	}

}
