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

//	@Test
//	public void test4() throws JSONException, IOException {
//		GenericUrl gu = new GenericUrl(servlet);
//		gu.set("m", ApiType.dictionary.getIdentifyer()).set("q", "shalom");
//		JSONObject response = HtmlUtil.getJSON(gu.build());
//		Assert.assertTrue(response.getBoolean("resultOK"));
//		gu.set("v", "");
//
//		String shouldContain = "Jewish greeting";
//		String res = HtmlUtil.getUrlSource(gu.build());
//
//		Assert.assertTrue(res, res.contains(shouldContain));
//	}
}
