package com.owow.rich.test;

import java.util.Map;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PropertyContainer;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.items.WebPage;
import com.owow.rich.storage.Storage;

public class Storage2Test {

	Storage	   storage;
	WebPage	   page;
	String	   ngram;
	ApiResponse	apiResponse;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	@Before
	public void setUp() throws Exception {
		storage = new Storage();
		page = new WebPage("", "", "http://www.ben-evans.com");
		ngram = "Shalti shalti shalti shalti!";
		apiResponse = new ApiResponse(ngram, new JSONObject(), ApiType.custom);
	}

	@Test
	public void test() throws EntityNotFoundException {
		storage.saveApiResponse(page, ngram, apiResponse);
		storage.saveApiResponse(page, ngram, apiResponse, true, true);
		PropertyContainer container = storage.loadPropertyContainer(page, ngram);
		Map map = container.getProperties();

	}
}
