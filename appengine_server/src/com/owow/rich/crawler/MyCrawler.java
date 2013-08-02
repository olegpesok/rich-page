package com.owow.rich.crawler;

import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {

	private final static Pattern	FILTERS	= Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
	                                            + "|png|tiff?|mid|mp2|mp3|mp4"
	                                            + "|wav|avi|mov|mpeg|ram|m4v|pdf"
	                                            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	String	                    mHost;
	@Override
	public void onStart() {
		super.onStart();
		mHost = (String) myController.getCustomData();
	}
	/**
	 * You should implement this function to specify whether the given url should
	 * be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches() && href.startsWith(mHost);
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		try {
	      sendToAppEngine(url);
      } catch (UnsupportedEncodingException e) {
	      e.printStackTrace();
      }

		/*
		 * System.out.println("URL: " + url);
		 *
		 * if (page.getParseData() instanceof HtmlParseData) { HtmlParseData
		 * htmlParseData = (HtmlParseData) page.getParseData(); String text =
		 * htmlParseData.getText(); String html = htmlParseData.getHtml();
		 * List<WebURL> links = htmlParseData.getOutgoingUrls();
		 *
		 * System.out.println("Text length: " + text.length());
		 * System.out.println("Html length: " + html.length());
		 * System.out.println("Number of outgoing links: " + links.size()); }
		 */
	}
	private void sendToAppEngine(String url) throws UnsupportedEncodingException {
		excutePost("http://rich-page.appspot.com/QueueToPageProc", "url=" + URLEncoder.encode(url, "UTF-8"));
	}
	public static void excutePost(String targetURL, String urlParameters)
	{
		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
			      "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", "" +
			      Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(false);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(
			      connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			return;

		} catch (Exception e) {

			e.printStackTrace();
			return;

		} finally {
			if (connection != null) connection.disconnect();
		}
	}
}