package com.owow.rich.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.owow.rich.apiHandler.ApiHandler;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.apiHandler.WikipediaHandler;
import com.owow.rich.utils.TemplateUtil;

public class FreebaseHandlerTest {
	ApiHandler	handler;

	@Before
	public void setUp() {
		handler = new WikipediaHandler();
		TemplateUtil.BASEPATH = "war/templates/";
	}

	@Test
	public void testGetData() throws Exception {
		// Prepare
		String expectedSubString = "second single from the album Shine by British R&B singer Estelle and features American rapper Kanye West";

		// Test:
		ApiResponse response = handler.getFirstResponse("American Boy", ApiType.wiki);

		// Assert:
		Assert.assertTrue(response.view != null);
		Assert.assertTrue(response.view.toString().contains(expectedSubString));
	}

	@Test
	public void testGetData2() throws Exception {
		// Prepare
		String expectedSubString = "Lonely Planet is the largest travel guide book publisher in the world. The company was owned by BBC Worldwide, which bought it in 2007 and sold it in 2013 to American billionaire Brad Kelley";

		// Test:
		ApiResponse response = handler.getFirstResponse("Lonely Planet", ApiType.wiki);

		// Assert:
		Assert.assertTrue(response.view != null);
		Assert.assertTrue(response.view.toString(), response.text.contains(expectedSubString));
	}

	@Test
	public void testGetData3() throws Exception {
		// Prepare
		String expectedSubString = "San Francisco, officially the City and County of San Francisco, is the leading financial";

		// Test:
		ApiResponse response = handler.getFirstResponse("san francisco", ApiType.wiki);

		// Assert:
		Assert.assertTrue(response.view.getView() != null);
		Assert.assertTrue(response.view.getView().contains(expectedSubString));
	}

	@Test
	public void testGetData4() throws Exception {
		// Prepare
		String expectedSubString = "Google AdSense is a program run by Google Inc.";

		// Test:
		ApiResponse response = handler.getFirstResponse("google ads", ApiType.wiki);

		// Assert:
		Assert.assertTrue(response.view.getView() != null);
		Assert.assertTrue(response.text.contains(expectedSubString));
	}

	@Test
	public void testGetDataWithLowFirebaseScore() throws Exception {
		// Prepare
		String expectedSubString = "Freddie Wong is an American filmmaker";

		// Test:
		ApiResponse response = handler.getFirstResponse("freddiew", ApiType.wiki);

		// Assert:
		Assert.assertTrue(response == null);
	}

}