package com.owow.rich.servlet;

import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.owow.rich.RichLogger;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.apiHandler.FreebaseHandler;
import com.owow.rich.utils.ComparisonUtils;
import com.owow.rich.utils.NlpUtils;
import com.owow.rich.utils.NlpUtils.ScoredResult;

@SuppressWarnings("serial")
public class DebugServlet  extends HttpServlet {

	private ComparisonUtils tfIdfUtil = new ComparisonUtils();
	private NlpUtils nlpUtils = new NlpUtils();

	@Override
   public void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
		try {
			final String highlight = req.getParameter("highlight");
			final String url = req.getParameter("url");
			Document doc = Jsoup.connect(url).get();
			String pageText = doc.body().text();
			resp.getWriter().write("highglihgt:" + highlight +  " text:" + pageText + "\r\n\r\n");
			List<ApiResponse> apiResponseList = new FreebaseHandler().getAllApiResponses(highlight, ApiType.freebase);
			
			ApiResponse chosenApiResponse = chose(apiResponseList, highlight, pageText);
			
			// print for debug.
			for (ApiResponse apiResponse : apiResponseList) {
				ScoredResult score = nlpUtils.compare(pageText,apiResponse.text);
				ScoredResult highlight_score = nlpUtils.compare(highlight,apiResponse.text);
				ScoredResult title_score = nlpUtils.compare(pageText,apiResponse.title);
				ScoredResult highlight_title_score = nlpUtils.compare(highlight,apiResponse.title);
				
				resp.getWriter().write(
						"id: " + apiResponse.id +
						"\r\n h_score:" + highlight_score +
						"\r\n ht_score:" + highlight_title_score +
						"\r\n t_score:" + title_score +
						"\r\n score:" + score +
						"\r\n internal score:" + apiResponse.apiInternalScore +
						"\r\n title:" + apiResponse.title +
						"\r\n text:" + apiResponse.text + "\r\n\r\n");
         }
	
      } catch (Exception e) {
      	RichLogger.log.log(Level.SEVERE, "error in debuge servlet", e);
      }
	}
	
	private int	FREEBASE_SCORE_LOW_THRESHOLD	= 0;
	private int	FREEBASE_SCORE_CAN_SKIP_CONTEXT_SCORE_THRESHOLD	= 500;
	
	private int MAX_SEARCH_RESPONSE = 3;
	
	private double TITLE_HIGHLIGHT_SCORE_THRESHOLD = 0.5;
	private double SCORE_THRESHOLD = 0.15;
	
	private ApiResponse chose(List<ApiResponse> apiResponseList, String highlight, String pageText) {
		
		for (ApiResponse apiResponse : apiResponseList) {
			// IF freebase score is high enough just return.
			if (apiResponse.apiInternalScore >= FREEBASE_SCORE_CAN_SKIP_CONTEXT_SCORE_THRESHOLD) {
				return apiResponse;
			}
			ScoredResult highlight_title_score = nlpUtils.compare(highlight,apiResponse.title);
			if (highlight_title_score.score > TITLE_HIGHLIGHT_SCORE_THRESHOLD) {
				return apiResponse;
			}
			
			ScoredResult score = nlpUtils.compare(pageText,apiResponse.text);
			if (highlight_title_score.score > SCORE_THRESHOLD) {
				return apiResponse;
			}			
      }
	   return null;
   }
	
	
	public void runAllTest() {
		
	}
	
	public void runSingleTest(String text, String url, String resultid) {
		
	}

}
