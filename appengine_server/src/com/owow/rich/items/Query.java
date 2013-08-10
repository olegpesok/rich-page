package com.owow.rich.items;

import java.io.Serializable;


public class Query implements Serializable{
	public String highlight;
	public WebPage page;
	public String method = null;
	
	public Query(String highlight, WebPage page){
		this.highlight = highlight;
		this.page = page;
	}

	public String getId() {
	   return highlight + page.url;
   }
}
