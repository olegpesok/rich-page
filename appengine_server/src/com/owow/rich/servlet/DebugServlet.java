package com.owow.rich.servlet;

import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.datanucleus.Utils.Function;
import com.owow.rich.RichLogger;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.apiHandler.FreebaseHandler;
import com.owow.rich.items.WebPage;
import com.owow.rich.utils.ComparisonUtils;
import com.owow.rich.utils.ComparisonUtils.ScoredObject;
import com.owow.rich.utils.ComparisonUtils.ScoredObjectList;
import com.owow.rich.utils.NlpUtils;
import com.owow.rich.utils.NlpUtils.Tag;

@SuppressWarnings("serial")
public class DebugServlet  extends HttpServlet {
	
	private ComparisonUtils tfIdfUtil = new ComparisonUtils();
	private NlpUtils nlpUtils = new NlpUtils();
	
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
		try {
			
//			ScoredObjectList<String> res = tfIdfUtil.simpleFindBestMatch(req.getParameter("t"), Lists.newArrayList(req.getParameter("t1"), req.getParameter("t2"), req.getParameter("t3")));
//			List<ScoredObject<String>> list = res.scoredObjectList;
//			for (ScoredObject<String> scoredObject : list) {
//				resp.getWriter().write(scoredObject.score + " " + scoredObject.documentText +" \r\n");
//         }
			
			final String highlight = req.getParameter("highlight");
			final String url = req.getParameter("url");
			
			List<ApiResponse> apiResponse = new FreebaseHandler().getAllApiResponses(highlight, ApiType.freebase);
			int counter = 0;
			for (ApiResponse scoredObject : apiResponse) {
				counter++;
				if (counter > 3 ){
					break;
				}
				
				List<Tag> concepts = nlpUtils.extractConcepts(scoredObject.text);
				List<Tag> entites = nlpUtils.extractEntities(scoredObject.text);
				List<Tag> keyWords = nlpUtils.extractKeyWords(scoredObject.text);
				String category = nlpUtils.categorizeText(scoredObject.text);
				
				resp.getWriter().write(scoredObject.apiInternalScore + " " + scoredObject.text +" \r\n");
				
				resp.getWriter().write("---------------- \r\n");
         }
			
//			resp.getWriter().write("---------------- \r\n");
//			resp.getWriter().write("---------------- \r\n");
//			
//			Function<ApiResponse, String> getTextFunction = new Function<ApiResponse, String>() {
//				@Override public String apply(ApiResponse response) {
//					return response.text;
//				}};
//			
//			String fullText = highlight;
//			if( url != null) {
//				WebPage page = new WebPage(null, null, url);
//				fullText = page.getText();
//			}
//			resp.getWriter().write("get more info");
//			
//				
//			ScoredObjectList<ApiResponse> rankedDcoumets = tfIdfUtil.getRankList(fullText, highlight, apiResponse, getTextFunction);
//			List<ScoredObject<ApiResponse>> list = rankedDcoumets.scoredObjectList;
//			for (ScoredObject<ApiResponse> scoredObject : list) {
//				resp.getWriter().write(scoredObject.score + " " + scoredObject.documentText +" \r\n");
//				resp.getWriter().write("---------------- \r\n");
//         }
			
			
						
      } catch (Exception e) {
      	RichLogger.log.log(Level.SEVERE, "error in debuge servlet", e);
      }
	}

}
