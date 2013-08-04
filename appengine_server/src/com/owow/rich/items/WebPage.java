package com.owow.rich.items;

import java.util.logging.Level;

import org.w3c.dom.Document;

import com.alchemyapi.api.AlchemyAPI;
import com.owow.rich.RichLogger;

public class WebPage {

	public String	html;
	public String	text;
	public String	url;
	public WebPage(String html, String text, String url) {
		this.html = html;
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
				if (d.getDocumentElement().getElementsByTagName("status").item(0).getTextContent().equals("ERROR")) { 
					RichLogger.log.log(Level.SEVERE,"Error, API alchemy failed at retriving the text");
				} else {
					text = d.getDocumentElement().getElementsByTagName("text").item(0).getTextContent();
				}
			}
			return text;

		} catch (Exception e) {
			return "error";
			// e.printStackTrace();
		}
	}

}
