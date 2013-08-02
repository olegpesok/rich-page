package com.owow.rich.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class NGramServlet extends HttpServlet {

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
	      throws IOException {
		resp.setContentType("application/json");
	
	// final String ngram = req.getParameter("ngram");
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
	}
}
