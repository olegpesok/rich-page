package com.owow.rich.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.owow.rich.Manager;
import com.owow.rich.items.WebPage;

/**
 * Get request for the pre-processing of a page.
 * Get a page URL and pre-process the page.
 */
@SuppressWarnings("serial")
public class PageProccessServlet extends HttpServlet {
	
	private Manager manger = new Manager();
	private static final Logger log = Logger.getLogger("Rich");
	
	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
	      throws IOException {
		
		final String url = req.getParameter("url");
		WebPage webPage = new WebPage(null, null, url);
		try {
			log.info("Processing page " + url);
			manger.processPage(webPage);
		} catch (Exception e) {
			log.warning("Could not process page " + url + ", " + e.getMessage());
		}
		return;
		
	}
}
