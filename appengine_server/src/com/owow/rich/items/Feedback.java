package com.owow.rich.items;

public class Feedback {
	
	enum FeedbackType {
		ShowFirst,
	}
	
	public Result result;
	public FeedbackType feedbackType;
	
	public Feedback(Result result, FeedbackType type) {
		this.result = result;
		this.feedbackType = type;
	}
}
