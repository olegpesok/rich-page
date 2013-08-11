package com.owow.rich.items;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Embed;
import com.owow.rich.apiHandler.ApiResponse;

@Embed
public class Result{
	
	public String title;
	public Text view;
	public int score = 0;
	public Text text;
	public long apiScore;
	
	public Result(){
	}
	
	public Result(String title, String view, String text, long apiScore) {
		this.title = title;
		this.view = new Text(view);
		this.text = new Text(text);
		this.apiScore = apiScore;
   }
	
	public Result(Query query, ApiResponse source, String view) {
	}	
}

