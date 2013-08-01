package com.owow.rich.items;


public class SearchTerm {
	public static SearchTerm create(String term) {
		return new SearchTerm(term);
	}
	private SearchTerm(String term) {
		this.term = term;
	}
	public String term;
}
