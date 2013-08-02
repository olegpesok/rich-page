package com.owow.rich.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import com.owow.rich.items.NGram;
import com.owow.rich.items.Token;

public class TokenizerUtil {

	public List<Token> tokenize(String text) {
		// text = text.replaceAll("[/<>!@#$%^&*-_=+~`?,\";.:'(){}\\[\\]|\\\\]",
		// "");
		text = text.replaceAll("[\"%(){}\\[\\]~,/<>!$&.]", "");
		text = text.toLowerCase();
		LinkedList<Token> tokens = new LinkedList<Token>();

		StringTokenizer st = new StringTokenizer(text, "|-_+;:@&#*= ");

		for (int i = 0; st.hasMoreTokens(); i++) {
			String s = st.nextToken();
			if (!s.trim().isEmpty()) tokens.add(new Token(s, i));
		}
		return tokens;
	}
	public List<NGram> combineToNGrams(List<Token> tokens, int size) {
		LinkedList<NGram> ngrams = new LinkedList<NGram>();
		LinkedList<Token> ngram = new LinkedList<Token>();
		for (int i = 0; i < size; i++) {
			int count = 0;
			for (Token t : tokens)
			{
				ngram.add(t);
				if (count < i) count++;
				else {
					ngrams.add(new NGram(ngram));
					ngram.remove();
				}
			}
		}

		return ngrams;
	}
	/**
	 * Return tokens to the substring with index according to the fullString
	 */
	public List<Token> tokenizeSubstring(String substring, String fullString) {

		return null;
	}
}
