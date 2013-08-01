//package com.owow.rich;
//
//import java.util.Arrays;
//import java.util.LinkedList;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import com.owow.rich.items.WikiLink;
//
//public class CustomWikies {
//
//	public enum WikiHost {
//		AllEng("en.wikipedia.org/w"),
//		Books("en.wikibooks.org/w"),
//		Judge("judgepedia.org");
//		private final String	host;
//		WikiHost(final String host) {
//			this.host = host;
//		}
//		public String getUrl() {
//			return host;
//		}
//		public static final WikiHost[]	preference	= new WikiHost[]{AllEng, Judge, Books};
//		public static final int		    maxPosition	= Arrays.asList(preference).indexOf(Books);
//	}
//
//	public static JSONObject main(WikiHost start, final String title)
//	{
//		if (start == null) start = WikiHost.AllEng;
//
//		return getJSON(title, start);
//	}
//
//	/**
//	 * Returns a JSONObject with the params: host_nichname,host_url, title, data,
//	 * and score
//	 * 
//	 * @param title
//	 * @param host
//	 * @return
//	 */
//	private static JSONObject getJSON(final String title, final WikiHost host) {
//
//		try {
//			final JSONArray ret = new JSONArray();
//			final String wiki = "http://" + host.getUrl() + "/api.php?format=json&action=query&prop=revisions&rvprop=content&rvsection=0&rvparse=true&titles=";
//			final String data = Rich_Snippet.getUrlSource(wiki + title);
//			final JSONObject pages = new JSONObject(data).getJSONObject("query")
//			      .getJSONObject("pages");
//			final String[] pagesID = JSONObject.getNames(pages);
//			if (pagesID[0].equals("-1")) return handleError("nowiki" + host.toString(), title, host);
//			for (final String s : pagesID) {
//				final JSONObject jo2 = new JSONObject();
//				final JSONObject currentPage = pages.getJSONObject(s);
//				final String[] whatIWant = new String[]
//				{"title", "revisions"};
//
//				jo2.put(whatIWant[0], currentPage.get(whatIWant[0]));
//
//				final String revision = currentPage.getJSONArray(whatIWant[1])
//				      .getJSONObject(0).getString("*");
//
//				final JSONArray ps = Rich_Snippet.pullElementsFromHtml("p", revision);
//
//				boolean flag = true;
//				for (int i = 0; flag && i < ps.length(); i++) {
//					final String myp = ps.getString(i);
//					if (Rich_Snippet.getTextFromHtml(myp).split(" ").length >= 9) flag = false;
//				}
//
//				if (flag) {
//					final JSONArray ja = Rich_Snippet.pullElementsFromHtml("a", revision);
//					jo2.put("a", ja);
//					int index = 0;
//					WikiLink myNextLink;
//					final LinkedList<WikiLink> llWikiLink = new LinkedList<WikiLink>();
//
//					for (; index < ja.length();)
//					{
//						myNextLink = Rich_Snippet.getLink(ja.getString(index++));
//						do {
//							while (!myNextLink.isValid()
//							      && index < ja.length())
//								myNextLink = Rich_Snippet.getLink(ja.getString(index++));
//							if (index > ja.length()) break;
//							myNextLink.createTitle(title);
//						} while (!myNextLink.canContinue(title));
//						llWikiLink.add(myNextLink);
//					}
//
//					// llWikiLink
//					for (final WikiLink wl : llWikiLink)
//						break;
//
//					myNextLink = llWikiLink.getFirst();
//					// new Title
//					jo2.put("title", myNextLink.title);
//					if (myNextLink.href.contains("wiktionary")) return getFromWikitionary(title, host);
//					else return getJSON(myNextLink.title, host);
//				} else {
//					jo2.put("p", ps);
//					jo2.put("table", Rich_Snippet.pullElementsFromHtml("table", revision));
//				}
//				ret.put(jo2);
//			}
//			final JSONObject jo = new JSONObject();
//			jo.put("data", ret);
//			jo.put("title", title);
//			jo.put("host_nickname", host.toString());
//			jo.put("host_url", host.getUrl());
//			jo.put("score", 100);
//			return jo;
//		} catch (final Exception e) {
//			e.printStackTrace();
//			return handleError("nowiki", title, host);
//		}
//	}
//	private static JSONObject handleError(final String error, final String title, final WikiHost type) {
//
//		JSONObject jo;
//
//		final int position = Arrays.asList(WikiHost.preference).indexOf(type);
//		final boolean moveToNext = position < WikiHost.maxPosition && position != -1;
//		switch (type) {
//			default :
//				break;
//		}
//
//		if (moveToNext) {
//			jo = getJSON(title, WikiHost.preference[position + 1]);
//			if (!jo.has("error")) Rich_Snippet.safePut(jo, "error", error);
//		} else {
//			jo = new JSONObject();
//			Rich_Snippet.safePut(jo, "error", error);
//		}
//		return jo;
//	}
//	private static JSONObject getFromWikitionary(final String title, final WikiHost host) {
//		try {
//			// can add &pretty=true server for checking
//			final String server = "http://glosbe.com/gapi/translate?from=eng&dest=eng&format=json&phrase=";
//			final JSONObject data = Rich_Snippet.getJSONFromServerAndTitle(server, title);
//			final JSONObject ret = new JSONObject();
//			final JSONArray texts = new JSONArray();
//			final JSONArray tuc = data.getJSONArray("tuc");
//			for (int i = 0; i < tuc.length(); i++)
//				if (tuc.getJSONObject(i).has("meanings")) {
//					final JSONArray meanings = tuc.getJSONObject(i).getJSONArray(
//					      "meanings");
//					final JSONArray meanings2 = new JSONArray();
//					for (int j = 0; j < meanings.length(); j++)
//						meanings2.put(meanings.getJSONObject(j).getString(
//						      "text"));
//					texts.put(meanings2);
//				} else if (tuc.getJSONObject(i).has("phrase")) texts.put(tuc.getJSONObject(i).getJSONObject("phrase")
//				      .getString("text"));
//			ret.put("data", texts);
//			return ret;
//		} catch (final Exception e) {
//			e.printStackTrace();
//			return handleError("nodic", title, host);
//		}
//	}
//}
