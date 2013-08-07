package com.owow.rich.items;

import java.util.LinkedList;
import java.util.List;

public class NGram {
	public List<Token>	tokens;
	public String	    searchTerm;
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return toString().equals(((NGram) obj).toString());
	}

	public NGram(List<Token> ngram) {
		tokens = new LinkedList<Token>();
		for (Token t : ngram)
			tokens.add(t);
	}

	public String getSearchTerm() {
		if (searchTerm != null) return searchTerm;
		StringBuilder sb = new StringBuilder();
		for (Token t : tokens)
			sb.append(t + " ");
		return searchTerm = sb.toString().trim();
	}
	@Override
	public String toString() {
		return getSearchTerm();
	}
}
