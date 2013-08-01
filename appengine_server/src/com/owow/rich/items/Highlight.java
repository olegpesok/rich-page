package com.owow.rich.items;

public class Highlight {
	public String	highlight;
	public String	text;
	public int	  startIndex;
	public int	  endIndex;

	public Highlight(final String highlight) {
		this(highlight, null);
	}

	public Highlight(final String highlight, final String text) {
		this.highlight = highlight.trim();
		this.text = text != null ? text : highlight;
		startIndex = this.text.indexOf(this.highlight);
		endIndex = startIndex + this.highlight.length();
	}
}