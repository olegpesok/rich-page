package com.owow.rich.apiHandler;

import java.util.List;

import com.owow.rich.items.WebPage;
import com.owow.rich.utils.NlpUtils;
import com.owow.rich.utils.NlpUtils.ScoredResult;

public class ApiResponsePicker {

	private double TITLE_HIGHLIGHT_SCORE_THRESHOLD = 0.5;
	private double SCORE_THRESHOLD = 0.15;
	private NlpUtils nlpUtils = new NlpUtils();
	
	public ApiResponse choseResult(List<ApiResponse> apiResponseList, WebPage webPage, String highlight) {
		for (ApiResponse apiResponse : apiResponseList) {
			ScoredResult highlight_title_score = nlpUtils.compare(highlight,apiResponse.title);
			if (highlight_title_score.score > TITLE_HIGHLIGHT_SCORE_THRESHOLD) {
				return apiResponse;
			}
			
			ScoredResult score = nlpUtils.compare(webPage.text,apiResponse.text);
			if (highlight_title_score.score > SCORE_THRESHOLD) {
				return apiResponse;
			}			
      }
	   return null;

   }

}
