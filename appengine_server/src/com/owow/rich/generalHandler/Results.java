package com.owow.rich.generalHandler;

import java.util.ArrayList;

public class Results extends ArrayList<Result> {
	String	   ngram;
	GeneralType	type;

	public Results(String ngram, GeneralType generalType) {
		this.ngram = ngram;
		type = generalType;
	}

	public boolean add(String view, int score) {
		// TODO Auto-generated method stub
		return add(new Result(view, score));
	}
}
