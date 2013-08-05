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
import com.owow.rich.utils.TemplateUtil;

/**
 * Tests for {@link WikipediaHandler}.
 *
 * @author abgutman1@gmail.com
 */
@RunWith(JUnit4.class)
public class WikipediaHandlerTest {

	WikipediaHandler	handler;

	@Before
	public void setUp() {
		handler = new WikipediaHandler();
		TemplateUtil.BASEPATH = "war/templates/";
	}

	@Test
	public void testGetData() throws Exception {
		// Prepare
		String expectedSubString = "Alon USA Energy Inc";

		// Test:
		ApiResponse response = handler.getFirstResponse("Alon+USA", ApiType.wiki);

		// Assert:
		Assert.assertTrue(response.view != null);
		Assert.assertTrue(response.view.toString().contains(expectedSubString));
	}

	@Test
	public void testGetData2() throws Exception {
		// Prepare
		String expectedSubString = "Brad Kelley";

		// Test:
		ApiResponse response = handler.getFirstResponse("Lonely+Planet", ApiType.wiki);

		// Assert:
		Assert.assertTrue(response.view != null);
		Assert.assertTrue(response.view.toString(), response.view.getView().contains(expectedSubString));
	}

	@Test
	public void testGetData3() throws Exception {
		// Prepare
		String expectedSubString = "San Francisco";

		// Test:
		ApiResponse response = handler.getFirstResponse("San+Francisco", ApiType.wiki);

		// Assert:
		Assert.assertTrue(response.view.getView() != null);
		Assert.assertTrue(response.view.getView(), response.view.getView().contains(expectedSubString));
	}

	@Test
	public void testGetData4() throws Exception {
		// Prepare
		String expectedSubString = "Google AdSense";

		// Test:
		ApiResponse response = handler.getFirstResponse("google+ads", ApiType.wiki);

		// Assert:
		Assert.assertTrue(response.view.getView() != null);
		Assert.assertTrue(response.view.getView().contains(expectedSubString));
	}

	@Test
	public void testGetData5() throws Exception {
		//Dictionary

		// Prepare
		String expectedSubString = "A meal that is eaten in the late afternoon";

		// Test:
		ApiResponse response = handler.getFirstResponse("TEA", ApiType.wiki);

		// Assert:
		Assert.assertTrue(response.view.getView() != null);
		Assert.assertTrue(response.view.getView(), response.view.getView().contains(expectedSubString));
	}

	@Test
	public void testHighlightConstructor() throws JSONException {
		// Prepare
		JSONObject json = new JSONObject("{data:[{p:['<b>hello<b>']}]}");
		TemplateUtil.BASEPATH = "war/templates/";

		// Test:
		// String html = handler.createView(json);
		//
		// // Assert:
		// Assert.assertEquals("<b>hello<b>", html);
	}

}