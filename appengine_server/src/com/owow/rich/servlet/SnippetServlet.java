package com.owow.rich.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.labs.repackaged.com.google.common.collect.ImmutableList;
import com.google.appengine.labs.repackaged.com.google.common.collect.ImmutableMap;
import com.google.appengine.repackaged.com.google.api.client.util.Lists;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.owow.rich.Manager;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.items.WebPage;
import com.owow.rich.storage.AnaliticsManager;
import com.owow.rich.utils.RelatedLinkSearch;
import com.owow.rich.utils.TemplateUtil;

/**
 * Handle a request for an highlight. Returns the view, or JSON mathcing this
 * highlight.
 */
@SuppressWarnings("serial")
public class SnippetServlet extends HttpServlet {

	@SuppressWarnings("unused")
	final boolean	             debug	         = true;
	final static ApiType	       DEFAULT_API_TYPE	= ApiType.freebase;
	private static final Logger	log	         = Logger.getLogger("Rich");
	private Manager	          manager	         = new Manager();
	public final static boolean	AdminMode	   = true;
	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
	      throws IOException {

		final String showView = req.getParameter("v");
		final String method = req.getParameter("m");
		String query = req.getParameter("q");
		final String url = req.getParameter("url");
		if (query != null && query != "") {
			// TODO get rid of that.

			WebPage webpage = new WebPage(null, null, url);

			ApiResponse apiResponse = manager.getApiResponse(webpage, query, method);
			
			// Send the response in json/html format:
			if (apiResponse != null)
			{// Send html:
				if (showView != null) {
					List<WebPage> relatedLinks = Lists.newArrayList();
					if (url != null){
						relatedLinks = RelatedLinkSearch.search(webpage, query);
					}
					printApiResposeView(apiResponse, query, resp, relatedLinks);
					AnaliticsManager am = new AnaliticsManager(manager.storage);

					am.saveLog(req.getHeader("User-Agent"), req.getRemoteAddr(), query, webpage, apiResponse != null);
					// Send Json format:
				} else {
					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put("resultOK", apiResponse != null && !apiResponse.view.getView().isEmpty());
					} catch (JSONException e) {
						log.warning("json problem in simple resultOK");
					}
					resp.setContentType("application/json");
					resp.getWriter().write(jsonObject.toString());
				}
			} else {
				AnaliticsManager am = new AnaliticsManager(manager.storage);
				am.saveLog(req.getHeader("User-Agent"), req.getRemoteAddr(), query, webpage, apiResponse != null);
			}
		}
	}
	private void printApiResposeView(ApiResponse ar, String ngram, HttpServletResponse res, List<WebPage> relatedLinks) throws IOException
	{
		SoyListData soyList = new SoyListData();
		for (WebPage webPage : relatedLinks) {
			SoyData soyData = new SoyMapData("link", webPage.url, "title", webPage.getTitle());
			soyList.add(soyData);
			
      }
		res.setContentType("text/html");
		res.getWriter().write(TemplateUtil.getHtml("common.soy", new SoyMapData("p", ar.view.getView(), "admin", AdminMode, "ngram", ngram, "links", soyList)));
	}
}
