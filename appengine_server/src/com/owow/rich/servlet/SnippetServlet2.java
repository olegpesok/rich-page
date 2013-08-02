package com.owow.rich.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.template.soy.data.SoyMapData;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiRetriver;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.apiHandler.WikipediaHandler;
import com.owow.rich.apiHandler.WikipediaHandler.WikiHost;
import com.owow.rich.apiHandler.WikipediaHandler.WikiHost.WikiHostFactory;
import com.owow.rich.entity.SearchTermExtractor;
import com.owow.rich.items.Highlight;
import com.owow.rich.items.SearchTerm;
import com.owow.rich.utils.TemplateUtil;

@SuppressWarnings("serial")
public class SnippetServlet2 extends HttpServlet {

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
	      throws IOException {
		resp.setContentType("application/json");

		final String method = req.getParameter("m");
		final String showView = req.getParameter("v");

		String highlight = req.getParameter("highlight");
		if (highlight == null) {
			resp.getWriter().write("missing highlight");
			return;
		}

		try {
			ApiResponse ar = getApiResponseFromHighlight(req, resp, method, highlight, showView);
			resp.getWriter().write(ar.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// unused
	ApiResponse getApiResponseFromHighlight(final HttpServletRequest req, final HttpServletResponse resp, final String method, String highlight, String showView)
	      throws UnsupportedEncodingException, IOException, JSONException {
		if (method.equals("allw"))
		{
			String text = req.getParameter("text");
			text = text != null ? text : highlight;

			final List<SearchTerm> searchTerms = SearchTermExtractor.extractAllTerms(new
			      Highlight(highlight, text));

			final JSONArray ja = new JSONArray();
			WikipediaHandler myWikiHandler = new WikipediaHandler();
			for (final SearchTerm searchTerm : searchTerms) {
				if (searchTerm.term == null) continue;
				final String title = URLEncoder.encode(searchTerm.term, "UTF-8");

				WikiHost at = WikiHost.AllEng;
				List<WikiHost> seq = WikiHostFactory.getWikiHostSequence(at);
				for (WikiHost wh : seq)
					if (wh != null)
					{
						myWikiHandler.setHost(wh);
						ApiResponse ar;
						try {
							ar = myWikiHandler.getData(title, ApiType.wiki);
							ja.put(ar.json);
							break;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
			}
			JSONObject retJO = new JSONObject();
			retJO.put("data", ja);
			return new ApiResponse(retJO, ApiType.wiki);
		}
		else
		{
			ApiResponse fin = ApiRetriver.getApiResponse(highlight, method);
			if (fin != null) if (showView == null) resp.getWriter().write(fin.toString());
			else {
				resp.setContentType("text/html");
				resp.getWriter().write(TemplateUtil.getHtml("common.soy", new SoyMapData("p", fin.view.getView())));
			}
			return fin;
		}
	}
}
