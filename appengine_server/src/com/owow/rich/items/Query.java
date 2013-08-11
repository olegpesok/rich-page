package com.owow.rich.items;

import com.googlecode.objectify.annotation.Embed;

@Embed
public class Query{
	public String highlight;
	public WebPage page;
	
	public Query(String highlight, WebPage page){
		this.highlight = highlight;
		this.page = page;
	}

	public String getId() {
	   return highlight + "_" + page.url;
   }
}
