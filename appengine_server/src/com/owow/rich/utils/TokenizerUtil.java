package com.owow.rich.utils;

import java.util.LinkedList;
import java.util.List;

import com.owow.rich.items.NGram;
import com.owow.rich.items.Token;

public class TokenizerUtil {

	// NOTE: can be changed to array
	public List<Token> tokenize(String text) {
		// text = text.replaceAll("[/<>!@#$%^&*-_=+~`?,\";.:'(){}\\[\\]|\\\\]",
		// "");
		LinkedList<Token> tokens = new LinkedList<Token>();

		StringTokenizer st = new StringTokenizer(text, "\"%(){}\\[\\]~,/<>!$&.|_+;:@&#*= -");
		String[] strs = st.tokenize();

		// tokens = new Token[strs.length];

		for (int i = 0; i < strs.length; i++)
			tokens.add(new Token(strs[i], i));
		// tokens[i] = new Token(strs[i],i);

		return tokens;
	}
	public List<NGram> combineToNGrams(List<Token> tokens, int size) {
		LinkedList<NGram> ngrams = new LinkedList<NGram>();
		LinkedList<Token> ngram = new LinkedList<Token>();
		for (int i = size - 1; i >= 0; i--) {
			int count = 0;
			ngram.clear();
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

	public List<NGram> getAllNgram(String query, int ngramLen) {
		List<Token> tokens = tokenize(query);
		List<NGram> nGrams = combineToNGrams(tokens, ngramLen);
		return nGrams;
	}
}
