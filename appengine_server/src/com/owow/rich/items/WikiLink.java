package com.owow.rich.items;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class WikiLink {
	public String	href;
	public String	text;
	public String	searchTerm;
	public String	summary;
	public WikiLink(final String href, final String text) {
		this(href, text, null);
	}

	public WikiLink(final String href, final String text, final String header) {
		this.href = href;
		this.text = text;
		summary = header;
	}

	public void createSearchTerm(final String currentSearchTerm) {
		if (href.contains("/")) searchTerm = href.substring(
		      href.lastIndexOf("/") + 1,
		      href.length());
		else searchTerm = currentSearchTerm;
	}

	public boolean isValid()
	{
		if (href.contains("Wikipedia")) return false;
		if (href.contains("index.php?")) return false;
		if (href.contains("wiktionary")) return false;
		if (href.contains(":")) return false;
		if (href.contains("Acronym")) return false;
		return true;
	}

	public boolean canContinue(final String currentTitle)
	{
		return
		// href.contains("wiktionary") ||
		currentTitle != searchTerm && currentTitle.toLowerCase() != "the" + searchTerm.toLowerCase() && searchTerm.toLowerCase() != "the " + currentTitle;
	}

	@Deprecated
	public static WikiLink createFromAElement(final String a)
	{
		final Element e = Jsoup.parse(a).getElementsByTag("a").first();
		return new WikiLink(e.attr("href"), e.text());
	}

	public static WikiLink createFromliElement(Element li)
	{
		Element a = li.getElementsByTag("a").first();
		String title = a.text();
		String href = a.attr("href");

		return new WikiLink(href, title, li.text());
	}

	public static WikiLink createFromATagElement(Element a) {
		String summary = a.text();
		String href = a.attr("href");

		return new WikiLink(href, summary);
	}

}
