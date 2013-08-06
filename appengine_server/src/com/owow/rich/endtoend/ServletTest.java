package com.owow.rich.endtoend;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.api.client.http.GenericUrl;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.utils.HtmlUtil;

public class ServletTest {

	static final String	server	= "http://localhost:8888/";
	static final String	servlet	= server + "Snippet";
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}
	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void test() throws JSONException, IOException {
		GenericUrl gu = new GenericUrl(servlet);
		gu.set("m", ApiType.freebase.getIdentifyer()).set("q", "Willy Wonka");
		JSONObject response = HtmlUtil.getJSON(gu.build());
		Assert.assertTrue(response.getBoolean("resultOK"));
		gu.set("v", "");

		String shouldContain = "Willy Wonka is a major character of Roald Dahl";
		String res = HtmlUtil.getUrlSource(gu.build());

		Assert.assertTrue(res, res.contains(shouldContain));
	}

	@Test
	public void test2() throws JSONException, IOException {
		GenericUrl gu = new GenericUrl(servlet);
		gu.set("m", ApiType.freebase.getIdentifyer()).set("q", "Mark Zuckerberg");
		JSONObject response = HtmlUtil.getJSON(gu.build());
		Assert.assertTrue(response.getBoolean("resultOK"));
		gu.set("v", "");

		String shouldContain = "Mark Elliot Zuckerberg";
		String res = HtmlUtil.getUrlSource(gu.build());

		Assert.assertTrue(res, res.contains(shouldContain));
	}

	@Test
	public void test3() throws JSONException, IOException {
		GenericUrl gu = new GenericUrl(servlet);
		gu.set("m", ApiType.wiki.getIdentifyer()).set("q", "MC Hammer");
		JSONObject response = HtmlUtil.getJSON(gu.build());
		Assert.assertTrue(response.getBoolean("resultOK"));
		gu.set("v", "");

		String shouldContain = "Stanley Kirk Burrell";
		String res = HtmlUtil.getUrlSource(gu.build());

		Assert.assertTrue(res, res.contains(shouldContain));
	}

	@Test
	public void test4() throws JSONException, IOException {
		singleTest("shalom", ApiType.dictionary.getIdentifyer(), "http://en.wikipedia.org/wiki/Shalom", true, "Jewish greeting");
	}
	
	@Test
	public void manyTests() throws JSONException, IOException {
//		singleTest("Music", ApiType.freebase.getIdentifyer(), "url=http://www.mtv.com/music/", true, "Music is an art");
//		singleTest("Scott Sassa", ApiType.freebase.getIdentifyer(), "http://www.hollywoodreporter.com/news/scott-sassa-lands-gig-at-521671", true, "Scott");
//		singleTest("Central Italy", ApiType.freebase.getIdentifyer(), "http://wikitravel.org/en/Central_Italy", true, "Central Italy is one of the five official");
//		singleTest("Angry Birds", ApiType.freebase.getIdentifyer(), "http://ben-evans.com/?page=11", true, "Angry Birds is a video game franchise createdl");
//		singleTest("microsoft", ApiType.freebase.getIdentifyer(), "http://ben-evans.com/?page=11", true, "Microsoft Corporation is an American multinational software");
//		singleTest("iPods", ApiType.freebase.getIdentifyer(), "http://ben-evans.com/?page=11", true, "The iPod is a line of portable media players");
//		singleTest("Apple", ApiType.freebase.getIdentifyer(), "http://ben-evans.com/?page=11", true, "Apple Inc., formerly Apple Computer, Inc., is an American multinational corporation headquartered");
//		singleTest("Blackberries", ApiType.freebase.getIdentifyer(), "http://ben-evans.com/?page=11", true, "The BlackBerry is a line of wireless handheld devices");
//		singleTest("Kik", ApiType.freebase.getIdentifyer(), "http://ben-evans.com/?page=11", true, "Kik Messenger is an instant messaging");
//		singleTest("AirDrop", ApiType.freebase.getIdentifyer(), "http://ben-evans.com/?page=11", true, "AirDrop is a Wi-Fi ad-hoc service");
//		singleTest("Google Play", ApiType.freebase.getIdentifyer(), "http://ben-evans.com/?page=11", true, "Google Play, formerly known as the Android Market");
//		singleTest("Tencent", ApiType.freebase.getIdentifyer(), "http://ben-evans.com/?page=11", true, "Tencent Holdings Limited is a Chinese investment");
		singleTest("Google plus", ApiType.freebase.getIdentifyer(), "http://ben-evans.com/?page=11", true, "is a social networking service operated by Google Inc.");
		
	}
	
	private void singleTest(String query, String method, String url, boolean showView, String expectedSubString) throws JSONException, IOException {
		GenericUrl gu = new GenericUrl(servlet);
		gu.set("m", method).set("q", query);
		gu.set("url", url);
		
		JSONObject response = HtmlUtil.getJSON(gu.build());
		Assert.assertTrue(response.getBoolean("resultOK"));
		if(showView) {
			gu.set("v", "");
		}

		String res = HtmlUtil.getUrlSource(gu.build());

		Assert.assertTrue(res, res.contains(expectedSubString));
	}
	
}
