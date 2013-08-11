package com.owow.rich.items;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Embed;

@Embed
public class Result {
	
	//temp just for testing:
	public String titleHighlight;
	
	public String title;
	public Text view;
	public int score = 0;
	public Text text;
	public long apiScore;
	
	public Result() {}
	
	public Result(String title, String view, String text, long apiScore, String highlight) {
		this.title = title;
		this.view = new Text(view);
		this.text = new Text(text);
		this.apiScore = apiScore;
		this.titleHighlight = highlight;
   }
}

