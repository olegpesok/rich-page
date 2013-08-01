package com.owow.rich.test;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.apiHandler.WikipediaHandler;
import com.owow.rich.view.TemplateUtil;


/**
 * Tests for {@link WikipediaHandler}.
 *
 * @author abgutman1@gmail.com
 */
@RunWith(JUnit4.class)
public class WikipediaHandlerTest {


	WikipediaHandler handler;

	@Before
	public void setUp() {
		handler = new WikipediaHandler();
		TemplateUtil.BASEPATH = "war/templates/";
	}

	@Test
	public void testGetData() throws Exception {
		// Prepare
		String expectedSubString = "<p><b>Alon USA Energy Inc.</b>";

		// Test:
		ApiResponse response = handler.getData("Alon_USA", ApiType.wiki);

		// Assert:
		Assert.assertTrue(response.view != null);
		Assert.assertTrue(response.view.toString().contains(expectedSubString));
	}

	@Test
	public void testHighlightConstructor() throws JSONException {
		// Prepare
		JSONObject json = new JSONObject("{data:[{p:['<b>hello<b>']}]}");
		TemplateUtil.BASEPATH = "war/templates/";

		// Test:
//		String html = handler.createView(json);
//
//		// Assert:
//		Assert.assertEquals("<b>hello<b>", html);
	}

}