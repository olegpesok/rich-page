package com.owow.rich.utils;

import java.util.LinkedList;

public class StringTokenizer {
	public String	origin;
	public String	delim;
	public StringTokenizer(String orig, String delimiters)
	{
		origin = orig;
		delim = delimiters;
	}
	public String[] tokenize()
	{
		LinkedList<String> ll = new LinkedList<String>();
		return origin.split("[" + delim + "]+");
	}
}
