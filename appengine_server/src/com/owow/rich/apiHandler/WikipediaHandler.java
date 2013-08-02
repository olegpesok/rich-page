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
	public ApiResponse getData(String query, ApiType type) throws Exception {
		final JSONArray pagesData = new JSONArray();
		depth++;
		// deciding whether to download all content, or just the first section
		String wikiServer;
		if (!query.contains("(disambiguation)")) wikiServer = "http://" + mHost.getUrl()
		      + "/api.php?format=json&action=query&prop=revisions&rvprop=content&rvsection=0&rvparse=true&titles=";
		else wikiServer = "http://" + mHost.getUrl()
		      + "/api.php?format=json&action=query&prop=revisions&rvprop=content&rvparse=true&titles=";

		final String data = HtmlUtil.getUrlSource(wikiServer + query);
		final JSONObject pages = new JSONObject(data).getJSONObject("query")
		      .getJSONObject("pages");
		final String[] pagesID = JSONObject.getNames(pages);
		if (pagesID[0].equals("-1")) throw new Exception("pageID = -1 on " + query);
		for (final String s : pagesID) {
			final JSONObject pageData = new JSONObject();
			final JSONObject currentPage = pages.getJSONObject(s);
			final String[] jsonKeys = new String[]{"title", "revisions"};
			pageData.put(jsonKeys[0], currentPage.get(jsonKeys[0]));
			final String revision = currentPage.getJSONArray(jsonKeys[1]).getJSONObject(0).getString("*");
			Document revDocument = Jsoup.parse(revision);
			Elements documentElems = revDocument.body().children();
			JSONArray pElems = new JSONArray();
			JSONArray tableElems = new JSONArray();
			for (Element elem : documentElems)
				if (elem.tagName() == "table") tableElems.put(elem.html());
				else if (elem.tagName().equals("p")) pElems.put(elem.html());

			boolean hasImportantData = true;
			for (int i = 0; hasImportantData && i < pElems.length(); i++) {
				final String myp = pElems.getString(i);
				if (HtmlUtil.getTextFromHtml(myp).split(" ").length >= 9) hasImportantData = false;
			}
			if (!hasImportantData) hasImportantData = query.contains("(disambiguation)");
			if (hasImportantData) {

				LinkedList<DisambugitionElement> elements = getElements(revDocument, query);

				WikiLink myNextLink = elements.getFirst().links.getFirst();
				pageData.put("title", myNextLink.searchTerm);
				if (myNextLink.href.contains("wiktionary")) {
					// Redirect to a dictionary
					ApiType dictionaryAT = ApiType.dictionary;
					return dictionaryAT.createHandler().getData(myNextLink.searchTerm, dictionaryAT);
				}
				else
				{
					if (depth >= 5) {
						java.util.logging.Logger.getLogger(WikipediaHandler.class.toString())
						      .warning("Overflow searches on query" + query);
						return null;
					}
					ApiResponse ar = getData(myNextLink.searchTerm, type);
					depth--;
					return ar;
				}
			} else {
				pageData.put("p", pElems);
				pageData.put("table", tableElems);
			}
			depth--;
			pagesData.put(pageData);
		}
		final JSONObject json = new JSONObject();
		json.put("data", pagesData);
		json.put("query", query);
		json.put("host_nickname", mHost.toString());
		json.put("host_url", mHost.getUrl());
		//json.put("score", 100);
		return new ApiResponse(json, type);
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
	LinkedList<DisambugitionElement> getElements(Document revision, String query)
	{
		LinkedList<DisambugitionElement> elements = new LinkedList<WikipediaHandler.DisambugitionElement>();
		DisambugitionElement neutral = new DisambugitionElement("Neutral");
		DisambugitionElement currentElement = neutral;

		Elements childrens = revision.body().children();
		for (Element child : childrens)
			if (child.tagName().equals("h2")) {
				elements.add(currentElement);
				currentElement = new DisambugitionElement(child.child(0).text());
			}
			else if (child.tagName().equals("ul")) for (Element liTag : child.getElementsByTag("li"))
			{
				WikiLink wl = WikiLink.createFromliElement(liTag);
				if (wl.isValid())
				{
					wl.createSearchTerm(query);
					if (wl.canContinue(query)) currentElement.links.add(wl);
				}
			}
			else for (Element aTags : child.getElementsByTag("a"))
			{
				WikiLink wikiLink = WikiLink.createFromATagElement(aTags);
				if (wikiLink.isValid())
				{
					wikiLink.createSearchTerm(query);
					if (wikiLink.canContinue(query)) neutral.links.add(wikiLink);
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