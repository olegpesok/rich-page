package com.owow.rich.apiHandler;

import java.net.URLDecoder;
import java.util.List;

import com.google.common.collect.Iterables;
import com.owow.rich.items.WebPage;
import com.owow.rich.utils.NlpUtils;
import com.owow.rich.utils.NlpUtils.ScoredResult;

public class ApiResponsePicker {

	private double TITLE_HIGHLIGHT_SCORE_THRESHOLD = 0.5;
	private double SCORE_THRESHOLD = 0.15;
	private NlpUtils nlpUtils = new NlpUtils();
	
	public ApiResponse choseResult(List<ApiResponse> apiResponseList, WebPage webPage, String highlight) {
		try{
			for (ApiResponse apiResponse : apiResponseList) {
				try {
					highlight = URLDecoder.decode(highlight, "UTF-8");
					apiResponse.title = URLDecoder.decode(apiResponse.title, "UTF-8");
				} catch(Exception e) {
					e = e;
				}
				
				if (apiResponse.goodEnough) {
					return apiResponse;
				}
				
				// This is going to have problem with.
				if (highlight.toLowerCase().equals(apiResponse.title.toLowerCase())) {
					return apiResponse;
				}
//				// TODO: adhoc morphology
//				if (highlight.toLowerCase().equals(apiResponse.title.toLowerCase())) {
//					return apiResponse;
//				}
				
				
				// For now skip dictionary.
				if(apiResponse.myType.equals(ApiType.dictionary)) {
					return apiResponse;
				}
				
				ScoredResult highlight_title_score = nlpUtils.compare(highlight, apiResponse.title);
				if (highlight_title_score.score > TITLE_HIGHLIGHT_SCORE_THRESHOLD) {
					return apiResponse;
				}
				
				if (webPage.getText() != null) {
					ScoredResult score = nlpUtils.compare(webPage.getText(), apiResponse.title + ". " +apiResponse.text);
					if (highlight_title_score.score > SCORE_THRESHOLD) {
						return apiResponse;
					}
				}
	      }
		   return null;
		} catch(Exception e){
			return Iterables.getFirst(apiResponseList, null);
		}
   }
	
//	public static ApiResponse findBestMatchAccordingToContext(List<ApiResponse> apiResponseList, WebPage webPage, String highlight) {
//		// If there not more then one result just returns the first result:
//		if (apiResponseList.size() <= 1) return Iterables.getFirst(apiResponseList, null);
//      else {
//			Function<ApiResponse, String> getTextFunction = new Function<ApiResponse, String>(){
//				@Override
//				public String apply(ApiResponse response) {
//					return response.text;
//				}
//			};
//
//			ScoredObjectList<ApiResponse> rankedDcoumets = tfIdfUtil.getRankList(highlight, highlight, apiResponseList, getTextFunction);
//			if (rankedDcoumets.isEmpty()) return Iterables.getFirst(apiResponseList, null);
//         else return rankedDcoumets.getBest();
//		}
//	}

}
