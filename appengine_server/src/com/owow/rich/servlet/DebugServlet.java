package com.owow.rich.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.owow.rich.RichLogger;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiResponsePicker;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.apiHandler.FreebaseHandler;
import com.owow.rich.items.WebPage;
import com.owow.rich.storage.PersistentCahce;
import com.owow.rich.utils.ComparisonUtils;
import com.owow.rich.utils.NlpUtils;
import com.owow.rich.utils.NlpUtils.ScoredResult;

@SuppressWarnings("serial")
public class DebugServlet  extends HttpServlet {

	private ComparisonUtils tfIdfUtil = new ComparisonUtils();
	private NlpUtils nlpUtils = new NlpUtils();
	private ApiResponsePicker apiResponsePicker = new ApiResponsePicker();
	
	@Override
   public void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
		try {
			PersistentCahce.set("key1", "value1", "namespace1");
			Object res = PersistentCahce.get("key1", "namespace1");
			res = res;
			if(true) {
				return;
			}
			
			final String highlight = req.getParameter("highlight");
			final String url = req.getParameter("url");
			Document doc = Jsoup.connect(url).get();
			String pageText = doc.body().text();
			resp.getWriter().write("highglihgt:" + highlight +  "\r\n text:" + pageText + "\r\n\r\n");
			List<ApiResponse> apiResponseList = new FreebaseHandler().getAllApiResponses(highlight, ApiType.freebase);
			
//			List<ApiResponse> apiResponseList = new WikipediaHandler().getAllApiResponses(highlight, ApiType.wiki);
			
			ApiResponse chosenApiResponse = apiResponsePicker.choseResult(apiResponseList, new WebPage(pageText, pageText, url) , highlight);
			
			if (chosenApiResponse != null) {
				resp.getWriter().write("chose: " + chosenApiResponse.title + " " + chosenApiResponse.id + "\r\n\r\n");
			} else {
				resp.getWriter().write("chose: Nothing \r\n\r\n");
			}
			
			// print for debug.
			for (ApiResponse apiResponse : apiResponseList) {
				ScoredResult score = nlpUtils.compare(pageText,apiResponse.text);
//				ScoredResult highlight_score = nlpUtils.compare(highlight,apiResponse.text);
//				ScoredResult title_score = nlpUtils.compare(pageText,apiResponse.title);
				ScoredResult highlight_title_score = nlpUtils.compare(highlight,apiResponse.title);
				
				resp.getWriter().write(
						"id: " + apiResponse.id +
//						"\r\n h_score:" + highlight_score +
						"\r\n ht_score:" + highlight_title_score +
//						"\r\n t_score:" + title_score +
						"\r\n score:" + score +
						"\r\n internal score:" + apiResponse.apiInternalScore +
						"\r\n title:" + apiResponse.title +
						"\r\n alias:" + apiResponse.alias +
						"\r\n text:" + apiResponse.text + "\r\n\r\n");
         }
	
      } catch (Exception e) {
      	RichLogger.log.log(Level.SEVERE, "error in debuge servlet", e);
      }
	}
	
	public void runAllTest() {
	}
	
	public void runSingleTest(String text, String url, String resultid) {
	}
}
