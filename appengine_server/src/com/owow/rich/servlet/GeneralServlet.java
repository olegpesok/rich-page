package com.owow.rich.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.owow.rich.Manager;
import com.owow.rich.generalHandler.BingHandler;
import com.owow.rich.utils.HtmlUtil;

public class GeneralServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String highlight = req.getParameter("q");
		String requestType = req.getParameter("type");
		if (highlight.isEmpty()) {
			resp.getWriter().write("no highlight");
			return;
		}
		try {

			BingHandler bingHandler = new BingHandler();
			Manager m = new Manager();
			resp.setContentType("application/json");
			highlight = URLEncoder.encode(highlight, "UTF-8");
			JSONObject json = bingHandler.getResults(highlight, m.storage);
			String display;
			if (requestType == null || requestType.isEmpty())
			{
				display = json.toString();
			}
			else {
				display = json.getJSONArray(requestType).toString();
			}

			// display = HtmlUtil.encodeHTML(display, new HashSet<char[]>());
			display = bingHandler.fixBing(display);
			HashSet<Character> hs = new HashSet<Character>();
			String ok = "{}\"'<>";
			for (int i = 0; i < ok.length(); i++) {
				hs.add(ok.charAt(i));
			}
			display = HtmlUtil.encodeHTML(display, hs);
			resp.getWriter().write(display);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
