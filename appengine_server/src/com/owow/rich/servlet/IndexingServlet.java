package com.owow.rich.servlet;

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.owow.rich.RichLogger;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.apiHandler.FreebaseHandler;
import com.owow.rich.apiHandler.WikipediaHandler;
import com.owow.rich.utils.HtmlUtil;
import com.owow.rich.utils.SearchUtils;

@SuppressWarnings("serial")
public class IndexingServlet  extends HttpServlet {
	
	SearchUtils searchUtils = new SearchUtils();
	
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		try{
			String countString = req.getParameter("count");
			if(countString == null) {
				countString = "1";
			}
			int count = Integer.parseInt(countString);
			for (int i = 0; i < count; i++) {
				JSONObject Json = HtmlUtil.getJSON("http://en.wikipedia.org/w/api.php?action=query&format=json&list=random&rnlimit=10&rnnamespace=0");
				JSONArray randomTitles = Json.getJSONObject("query").getJSONArray("random");
				for (int j = 0; j < randomTitles.length(); j++) {
					int randomId = randomTitles.getJSONObject(j).getInt("id");
					String title = randomTitles.getJSONObject(j).getString("title");
					ApiResponse apiResponse = new FreebaseHandler().getFirstResponse(""+randomId, ApiType.freebase);
					if (apiResponse == null) {
						apiResponse = new FreebaseHandler().getFirstResponse(title, ApiType.freebase);
					}
					if (apiResponse == null) {
						RichLogger.log.log(Level.INFO, "couldn'd find " + title + " id:" + randomId);
						continue;
					}
					searchUtils.index("wiki_"+randomId, apiResponse.text, null);
	         }
			}
			
		} catch(Exception ex) {
			RichLogger.log.log(Level.SEVERE, "error in indexing", ex);
		}
	}

}
