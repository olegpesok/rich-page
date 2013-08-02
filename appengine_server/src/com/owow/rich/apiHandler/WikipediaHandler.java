package com.owow.rich.apiHandler;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.appengine.labs.repackaged.com.google.common.annotations.VisibleForTesting;
import com.owow.rich.items.WikiLink;
import com.owow.rich.utils.HtmlUtil;

public class WikipediaHandler implements ApiHandler {

	public enum WikiHost {
		AllEng("en.wikipedia.org/w"),
		Books("en.wikibooks.org/w"),
		Eko("en.ekopedia.org/w"),
		Judge("judgepedia.org"),
		Citizendium("en.citizendium.org"),
		Enciclopedia("enciclopedia.us.es"),
		Garden("www.gardenology.org/w");
		private final String	host;
		WikiHost(final String mHost) {
			host = mHost;
		}
		public String getUrl() {
			return host;
		}

		public static class WikiHostFactory {

			final static WikiHost[]	prefrence	= new WikiHost[]
			                                  {
			                                        AllEng, Judge, Books
			                                  };
			final static WikiHost	end			= null;
			public static List<WikiHost> getWikiHostSequence(WikiHost at)
			{
				LinkedList<WikiHost> ret = new LinkedList<WikiHost>();
				int i;
				for (i = 0; i < prefrence.length && prefrence[i] != at; i++)
				{}
				ret.add(at);
				i++;
				for (; i < prefrence.length && prefrence[i] != end; i++)
					ret.add(prefrence[i]);

				return ret;
			}
		}
	}

	private WikiHost	mHost	= WikiHost.AllEng;
	private int	     depth	= 0;
	@Override
	public ApiResponse getData(String searchTerm, ApiType at) throws Exception {
		final JSONArray pagesData = new JSONArray();
		depth++;
		// deciding whether to download all content, or just the first section
		String wikiServer;
		if (!searchTerm.contains("(disambiguation)")) wikiServer = "http://" + mHost.getUrl()
		      + "/api.php?format=json&action=query&prop=revisions&rvprop=content&rvsection=0&rvparse=true&titles=";
		else wikiServer = "http://" + mHost.getUrl()
		      + "/api.php?format=json&action=query&prop=revisions&rvprop=content&rvparse=true&titles=";

		final String data = HtmlUtil.getUrlSource(wikiServer + searchTerm);
		final JSONObject pages = new JSONObject(data).getJSONObject("query")
		      .getJSONObject("pages");
		final String[] pagesID = JSONObject.getNames(pages);
		if (pagesID[0].equals("-1")) throw new Exception("pageID = -1 on " + searchTerm);
		for (final String s : pagesID) {
			final JSONObject pageData = new JSONObject();
			final JSONObject currentPage = pages.getJSONObject(s);
			final String[] whatIWant = new String[]
			{"title", "revisions"};

			pageData.put(whatIWant[0], currentPage.get(whatIWant[0]));

			final String revision = currentPage.getJSONArray(whatIWant[1])
			      .getJSONObject(0).getString("*");

			Document revDocument = Jsoup.parse(revision);

			Elements es = revDocument.body().children();
			JSONArray ps = new JSONArray();
			JSONArray tables = new JSONArray();
			for (Element e : es)
				if (e.tagName() == "table") tables.put(e.html());
				else if (e.tagName().equals("p")) ps.put(e.html());

			boolean flag = true;
			for (int i = 0; flag && i < ps.length(); i++) {
				final String myp = ps.getString(i);
				if (HtmlUtil.getTextFromHtml(myp).split(" ").length >= 9) flag = false;
			}
			if (!flag) flag = searchTerm.contains("(disambiguation)");
			if (flag) {

				LinkedList<DisambugitionElement> elements = getElements(revDocument, searchTerm);

				// TODO Enable handler for Wiktionary

				WikiLink myNextLink = elements.getFirst().links.getFirst();
				// new Title
				pageData.put("title", myNextLink.searchTerm);
				if (myNextLink.href.contains("wiktionary")) {
					ApiType dictAt = ApiType.dictionary;

					return dictAt.createHandler().getData(myNextLink.searchTerm, dictAt);
				}
				else
				{
					if (depth >= 5) throw new Exception("DEPTH OVERFLOW " + searchTerm);
					ApiResponse ar = getData(myNextLink.searchTerm, at);
					depth--;
					return ar;
				}
			} else {
				pageData.put("p", ps);
				pageData.put("table", tables);
			}
			depth--;
			pagesData.put(pageData);
		}
		final JSONObject jo = new JSONObject();
		jo.put("data", pagesData);
		jo.put("title", searchTerm);
		jo.put("host_nickname", mHost.toString());
		jo.put("host_url", mHost.getUrl());
		jo.put("score", 100);
		return new ApiResponse(jo, at);
	}
	public WikiHost getHost()
	{
		return mHost;
	}
	public void setHost(WikiHost host)
	{
		mHost = host;
	}
	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public class DisambugitionElement
	{
		public DisambugitionElement(String msubject) {
			links = new LinkedList<WikiLink>();
			subject = msubject;
		}
		public String		          subject;
		public LinkedList<WikiLink>	links;
	}
	@VisibleForTesting
	LinkedList<DisambugitionElement> getElements(Document rev, String searchTerm)
	{
		LinkedList<DisambugitionElement> elements = new LinkedList<WikipediaHandler.DisambugitionElement>();
		DisambugitionElement neutral = new DisambugitionElement("Neutral");
		DisambugitionElement currentElement = neutral;

		Elements childrens = rev.body().children();
		for (Element e : childrens)
			if (e.tagName().equals("h2")) {
				elements.add(currentElement);
				currentElement = new DisambugitionElement(e.child(0).text());
			}
			else if (e.tagName().equals("ul")) for (Element li : e.getElementsByTag("li"))
			{
				WikiLink wl = WikiLink.createFromliElement(li);
				if (wl.isValid())
				{
					wl.createSearchTerm(searchTerm);
					if (wl.canContinue(searchTerm)) currentElement.links.add(wl);
				}
			}
			else for (Element atags : e.getElementsByTag("a"))
			{
				WikiLink wl = WikiLink.createFromATagElement(atags);
				if (wl.isValid())
				{
					wl.createSearchTerm(searchTerm);
					if (wl.canContinue(searchTerm)) neutral.links.add(wl);
				}
			}
		elements.add(currentElement);
		return elements;
	}
	/*
	 * desired output: Array< String subject; Array<WikiLink>; >
	 */
}
// NLP
// Java - intresting
// angular js
// machine learning
// freebase
// information retriver
// app engine
// HTML5