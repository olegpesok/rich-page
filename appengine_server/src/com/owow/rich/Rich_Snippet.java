package com.owow.rich;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.template.soy.data.SoyMapData;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiResponseFactory;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.apiHandler.WikipediaHandler;
import com.owow.rich.apiHandler.WikipediaHandler.WikiHost;
import com.owow.rich.apiHandler.WikipediaHandler.WikiHost.WikiHostFactory;
import com.owow.rich.entity.EntityExtractor;
import com.owow.rich.entity.SearchTermExtractor;
import com.owow.rich.items.Highlight;
import com.owow.rich.items.SearchTerm;
import com.owow.rich.items.WebPage;
import com.owow.rich.memcache.Memcache;
import com.owow.rich.view.TemplateUtil;

@SuppressWarnings("serial")
public class Rich_Snippet extends HttpServlet {

	final SearchTermExtractor	 termExtractor	   = new SearchTermExtractor(new EntityExtractor());
	@SuppressWarnings("unused")
	private ServletRequest	    req;
	final boolean	             debug	         = true;
	final static ApiType	       DEFAULT_API_TYPE	= ApiType.freebase;
	private static final Logger	log	         = Logger.getLogger("Rich");
	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
	      throws IOException {

		req.getServerName();
		this.req = req;
		resp.setContentType("application/json");

		final String showView = req.getParameter("v");
		final String method = req.getParameter("m");

		if (req.getParameter("q") != null) {

			Manager m = new Manager();

			final String url = req.getParameter("url");
			WebPage wp = new WebPage(null, null, url);
			String query = req.getParameter("q");

			ApiResponse ar;
			ar = ApiResponseFactory.queryMemcache(query, Memcache.getInstance());
			if (ar == null)
			{
				ar = m.query(wp, query);
				if (ar == null) ar = ApiResponseFactory.getApiResponse(query, method);
			}

			if (ar != null && showView != null) {
				printApiResposeView(ar, resp);
				m.storage.saveLog(req.getHeader("User-Agent"), req.getRemoteAddr(), query, url, ar != null);
			}
			else if (showView == null)
			{
				JSONObject j = new JSONObject();
				try {
					j.put("resultOK", ar != null);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				resp.getWriter().write(j.toString());

			}
			return;
		}

		if (method.equals("procPage"))
		{
			Manager m = new Manager();

			final String url = req.getParameter("url");

			WebPage wp = new WebPage(null, null, url);
			try {
				log.info("Processing page " + url);
				m.processPage(wp);
			} catch (Exception e) {
				log.warning("Could not process page " + url + ", " + e.getMessage());
			}
			return;
		}

		final String ngram = req.getParameter("ngram");

		String highlight = getParam(req, "highlight");
		if (highlight == null) {
			resp.getWriter().write("missing highlight");
			return;
		}

		// if (ngram != null)
		// {
		// //NOTICE! unused {
		// String[] tokens;
		//
		// StringTokenizer st = new StringTokenizer(highlight);
		// tokens = new String[st.countTokens()];
		//
		// for (int i = 0; st.hasMoreTokens(); i++)
		// tokens[i] = st.nextToken();
		//
		// LinkedList<ApiResponse> arList = new LinkedList<ApiResponse>();
		//
		// int maxn = Math.max(Integer.parseInt(ngram), tokens.length);
		// for (int start = 0; start < tokens.length; start++)
		// for (int i = maxn; i > 0; i--) {
		// String test = "";
		// for (int offset = 0; offset < maxn; offset++)
		// test += tokens[start + offset];
		// try {
		// ApiResponse ar = getApiResponseFromHighlight(req, resp, method, test,
		// showView);
		// arList.add(ar);
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// }
		// return;
		// // }
		// }

		try {
			ApiResponse ar = getApiResponseFromHighlight(req, resp, method, highlight, showView);
			// resp.getWriter().write(ar.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	private void printApiResposeView(ApiResponse ar, HttpServletResponse res) throws IOException
	{
		res.setContentType("text/html");
		res.getWriter().write(TemplateUtil.getHtml("common.soy", new SoyMapData("p", ar.view.getView())));
	}

	ApiResponse getApiResponseFromHighlight(final HttpServletRequest req, final HttpServletResponse resp, final String method, String highlight, String showView)
	      throws UnsupportedEncodingException, IOException, JSONException {
		if (method.equals("allw"))
		{
			String text = getParam(req, "text");
			text = text != null ? text : highlight;

			final List<SearchTerm> searchTerms = termExtractor.extractTerms(new
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
			ApiResponse fin = ApiResponseFactory.getApiResponse(highlight, method);
			if (fin != null) if (showView == null) resp.getWriter().write(fin.toString());
			else {
				resp.setContentType("text/html");
				resp.getWriter().write(TemplateUtil.getHtml("common.soy", new SoyMapData("p", fin.view.getView())));
			}
			return fin;
		}
	}
	private String getParam(final HttpServletRequest req, final String param) throws UnsupportedEncodingException {
		final String paramValue = req.getParameter(param);

		return paramValue;
	}

}
