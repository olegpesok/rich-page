package com.owow.rich.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.owow.rich.utils.HtmlUtil;

@SuppressWarnings("serial")
public class CrawlerServlet extends HttpServlet {
	private final static Pattern	FILTERS	= Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
	                                            + "|png|tiff?|mid|mp2|mp3|mp4"
	                                            + "|wav|avi|mov|mpeg|ram|m4v|pdf"
	                                            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	String	                    mHost;
	HashSet<String>	           usedUrls	= new HashSet<String>();
	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
	      throws IOException {
		URL startingUrl = new URL(req.getParameter("url"));

		mHost = startingUrl.getHost();
		crawlInto(startingUrl, 0, 1);
		usedUrls.add(startingUrl.toString());
		int damn = fs.size();
		List<Future<?>> deleted = new LinkedList<Future<?>>();
		while (!fs.isEmpty()) {
			for (Future<?> f : fs)
				if (f.isDone()) deleted.add(f);
			for (Future<?> f : deleted)
				fs.remove(f);
		}
		resp.getWriter().write("OK - " + damn);
	}
	public void crawlInto(URL url, int depth, int maxDepth) throws IOException
	{
		Document document = Jsoup.parse(HtmlUtil.getUrlSource(url.toString()));
		Elements a = document.getElementsByTag("a");
		for (Element a2 : a)
			try {
				String s = a2.attr("href");
				URL newUrl = new URL(url, s);
				if (shouldVisit(newUrl))
				{
					usedUrls.add(newUrl.toString());
					sendToAppEngine(newUrl);
					if (depth < maxDepth) crawlInto(newUrl, depth + 1, maxDepth);
				}
			} catch (Exception e) {}
	}
	private void sendToAppEngine(URL url) throws UnsupportedEncodingException, MalformedURLException {
		excuteAsyncRequest("http://rich-page.appspot.com/QueueToPageProc", "url=" + URLEncoder.encode(url.toString(), "UTF-8"));
	}

	private void excuteAsyncRequest(String server, String params) throws MalformedURLException {
		makeAsyncRequest(new URL(server + "?" + params));

	}
	public boolean shouldVisit(URL u)
	{
		String href = u.toString().toLowerCase();
		return !usedUrls.contains(u.toString()) && !FILTERS.matcher(href).matches() && u.getHost().contains(mHost);
	}

	List<Future<?>>	fs	= new LinkedList<Future<?>>();
	protected void makeAsyncRequest(URL url) throws MalformedURLException {
		URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
		fs.add(fetcher.fetchAsync(url));
	}
}
