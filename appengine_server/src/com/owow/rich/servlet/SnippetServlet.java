package com.owow.rich.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.template.soy.data.SoyMapData;
import com.owow.rich.Manager;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.image.ImageRetriver;
import com.owow.rich.items.WebPage;
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
	private Manager	          manger	         = new Manager();

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
	      throws IOException {

		final String showView = req.getParameter("v");
		final String method = req.getParameter("m");
		String query = req.getParameter("q");
		final String url = req.getParameter("url");

		// delete this {
		JSONObject s = null;
		try {
			s = ImageRetriver.getImages(query);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		resp.setContentType("application/json");
		resp.getWriter().write(s.toString());
		if (true) return;
		// }

		if (query != null &&  query != "") {
			// TODO get rid of that.
			query = query.toLowerCase();
			WebPage webpage = new WebPage(null, null, url);

			ApiResponse apiResponse = manger.getApiResponse(webpage, query, method);

			// Send the response in json/html format:
			if (apiResponse != null) // Send html:
			if (showView != null) {
				printApiResposeView(apiResponse, resp);
				manger.storage.saveLog(req.getHeader("User-Agent"), req.getRemoteAddr(), query, url, apiResponse != null);
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
		}
	}
	private void printApiResposeView(ApiResponse ar, HttpServletResponse res) throws IOException
	{
		res.setContentType("text/html");
		res.getWriter().write(TemplateUtil.getHtml("common.soy", new SoyMapData("p", ar.view.getView())));
	}
}
