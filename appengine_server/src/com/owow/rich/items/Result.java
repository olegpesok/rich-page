package com.owow.rich.items;

import java.util.List;

import com.google.appengine.api.datastore.Text;
import com.google.common.collect.Lists;
import com.googlecode.objectify.annotation.Embed;

@Embed
public class Result implements Comparable<Result>{
	
	// Temp just for testing:
	public String titleHighlight;
	public ScoredResult nlpScore;
	
	
	public String title;
	public Text view;
	public int score = 0;
	public Text text;
	public long apiScore;
	public String filterReason;
	public List<String> alias;
	
	public Result() {
	}
	
	public Result(String title, String view, String text, long apiScore, String highlight, String filterReason, List<String> alias) {
		this.title = title;
		this.view = new Text(view);
		this.text = new Text(text);
		this.apiScore = apiScore;
		this.titleHighlight = highlight;
		this.filterReason = filterReason;
		this.alias = alias;
   }

	@Override
   public int compareTo(Result other) {
	   if(other.score != this.score){
	   	return other.score - this.score;
	   } else {
	   	return (int)(other.apiScore - this.apiScore);
	   }
   }
	
	public List<String> getTitleAndAliases() {
		List<String> aliasAndTitle = Lists.newArrayList(alias);
		aliasAndTitle.add(title);
		return aliasAndTitle;
   }
	
}

