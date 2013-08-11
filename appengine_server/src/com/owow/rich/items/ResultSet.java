package com.owow.rich.items;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class ResultSet {
	@Id public String id;
	
	public List<Result> results;
	public boolean containsGeneralResults = false;
	public boolean containsFastEntityResults = false;
	public boolean containsDeepEntityResults = false;
	
	public ResultSet() {
	}
	
	public ResultSet(Query query) {
		this(new ArrayList<Result>(), query);
	}
	
	public ResultSet(List<Result> results, Query query) {
		this.results = results;
		this.id = query.getId();
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

