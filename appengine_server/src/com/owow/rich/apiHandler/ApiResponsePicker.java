package com.owow.rich.apiHandler;

import java.net.URLDecoder;
import java.util.List;

import com.google.common.collect.Iterables;
import com.owow.rich.items.ScoredResult;
import com.owow.rich.items.WebPage;
import com.owow.rich.utils.NlpUtils;
import com.owow.rich.utils.StringCompareUtils;

/**
 * Scores we use:
 * - Api specific score (e.g Freebase score). 
 * - Similarity of the highlight and the title of the entity(and it's aliases). (allowing edit distance of 2).
 * - Similarity of the tags(entities, concepts, key-words) in the page, and in the entity body. (using Aclhemy API)
 * - TFIDF similarity of the bodies.
 * - Latent Semantic Analysis.
 * 
 * - The class both order the resutls and decied if the best result is good enough to show or not.
 *
 */
public class ApiResponsePicker {

	private double TITLE_HIGHLIGHT_SCORE_THRESHOLD = 0.5;
	private double SCORE_THRESHOLD = 0.15;
	private NlpUtils nlpUtils = new NlpUtils();
	
	public ApiResponse choseResult(List<ApiResponse> apiResponseList, WebPage webPage, String highlight) {
		try {
			
			// according to the internal score, if good enough and in big gap over the others.
			ApiResponse goodEnoughResponse = isGoodEnough(apiResponseList, webPage, highlight);
			if (goodEnoughResponse != null) {
				return goodEnoughResponse;
			}
			
			// according to text tags comparison score:
			ApiResponse bestApiResponse = isAnyOneBetterByFar(apiResponseList, webPage, highlight);
			if (bestApiResponse != null) {
				return bestApiResponse;
			}
			
			for (ApiResponse apiResponse : apiResponseList) {
				try {
					highlight = URLDecoder.decode(highlight, "UTF-8");
					apiResponse.title = URLDecoder.decode(apiResponse.title, "UTF-8");
				} catch(Exception e) {
					e = e;
				}
				
				// This is going to have problem with.
				if (highlight.toLowerCase().equals(apiResponse.title.toLowerCase())) {
					return apiResponse;
				}
				
				int d = StringCompareUtils.computeLevenshteinDistance(highlight.toLowerCase(), apiResponse.title.toLowerCase());
				if (d <= 2) {
					return apiResponse;
				}
				
				// check for match to the aliases.
				for (String alias : apiResponse.alias) {
					if (StringCompareUtils.computeLevenshteinDistance(highlight.toLowerCase(), alias.toLowerCase()) <= 2) {
						return apiResponse;
					}
            }
				
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
	
	private ApiResponse isGoodEnough(List<ApiResponse> apiResponseList, WebPage webPage, String highlight) {
		ApiResponse bestResponse = null;
		ApiResponse secondBestResponse = null;
		for (ApiResponse apiResponse : apiResponseList) {
	   	ScoredResult score = nlpUtils.compare(webPage.getText(), apiResponse.title + ". " +apiResponse.text);
			if (bestResponse == null || bestResponse.apiScore < apiResponse.apiScore) {
				secondBestResponse = apiResponse;
				bestResponse = apiResponse;
			} else if(secondBestResponse == null || secondBestResponse.apiScore < apiResponse.apiScore){
				secondBestResponse = apiResponse; 
			}
		}
		
		if ( (bestResponse != null && bestResponse.goodEnough) && 
				(secondBestResponse == null || bestResponse.apiScore - secondBestResponse.apiScore > 400) ) {
			return bestResponse;
		} else {
			return null;
		}
	}
	
	
	
   private ApiResponse isAnyOneBetterByFar(List<ApiResponse> apiResponseList, WebPage webPage, String highlight) {
   	webPage.getText();
   	
   	ApiResponse chosenResponse = null;
   	double bestScore = 0.0;
   	double secondBestScore = 0.0;
   	for (ApiResponse apiResponse : apiResponseList) {
   		ScoredResult score = nlpUtils.compare(webPage.getText(), apiResponse.title + ". " +apiResponse.text);
   		if (score.score > bestScore) {
   			chosenResponse = apiResponse;
   			secondBestScore = bestScore;
   			bestScore = score.score;
   		} else if(score.score > secondBestScore){
   			secondBestScore = score.score; 
   		}
   	}
   	if (bestScore > 0.6 && (bestScore - secondBestScore) > 0.2) {
   		return chosenResponse;
   	} else {
   		return null;
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
