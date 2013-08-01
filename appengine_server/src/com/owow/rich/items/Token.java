package com.owow.rich.items;

public class Token {
	public String	word;
	public int	  index;

	public Token(String word, int index) {
		this.word = word;
		this.index = index;
	}

	@Override
	public String toString() {
		return word;
	}
}
