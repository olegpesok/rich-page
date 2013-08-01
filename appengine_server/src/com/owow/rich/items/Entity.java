package com.owow.rich.items;

import java.net.URL;

import com.owow.rich.apiHandler.ApiType;

public class Entity {

	public String	title;
	public ApiType	source;
	public URL	   url;
	public String	html;
	public String	entireText;
	public int	   score;

	public Entity(String title, ApiType source, URL url, String html, String entireText, int score) {
		this.title = title;
		this.source = source;
		this.url = url;
		this.html = html;
		this.entireText = entireText;
		this.score = score;
	}
}
