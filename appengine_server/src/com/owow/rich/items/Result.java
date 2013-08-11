package com.owow.rich.items;

import java.io.Serializable;
import java.util.List;

import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.googlecode.objectify.annotation.Entity;
import com.owow.rich.apiHandler.ApiResponse;

@Entity
public class Result implements Serializable{
	
	public Query query;
	public String title;
	public String view;
	public int score = 0;
	public String text;
	public long apiScore;
	
	public Result(Query query, String title, String view, String text, long apiScore) {
		this.query = query;
		this.title = title;
		this.view = view;
		this.text = text;
		this.apiScore = apiScore;
   }
	
	public Result(Query query, ApiResponse source, String view) {
	}
	
	public static class Results implements Serializable{
		public List<Result> results;
		public boolean containsGeneralResults = false;
		public boolean containsFastEntityResults = false;
		public boolean containsDeepEntityResults = false;
		
		public Results(){
			this.results = Lists.newArrayList();
		}
		public Results(List<Result> results){
			this.results = results;
		}

		public void addGeneralResults(List<Result> results) {
			containsGeneralResults = true;
			this.results.addAll(results);
      }

		public void addFastEntityResults(List<Result> results) {
			containsFastEntityResults = true;
			this.results.addAll(results);
      }
		public void addDeepEntityResults(List<Result> results) {
			containsFastEntityResults = true;
			containsDeepEntityResults = true;
			this.results.addAll(results);
      }
	}
}
