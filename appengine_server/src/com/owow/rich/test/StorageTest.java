package com.owow.rich.test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.items.NGram;
import com.owow.rich.items.Token;
import com.owow.rich.items.WebPage;
import com.owow.rich.storage.Storage;

@RunWith(JUnit4.class)
public class StorageTest {
	private final LocalServiceTestHelper	helper	=
	                                                    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	@Before
	public void setUp() {
		helper.setUp();
		s = new Storage();
		smap = Maps.newHashMap();

		List<Token> t = new LinkedList<Token>();
		t.add(new Token("shalti", 1));
		t.add(new Token("USA", 0));
		apiResponse = new ApiResponse("shalti", new JSONObject(), ApiType.freebase);
		Assert.assertNotNull(apiResponse.myType);
		testNGram = new NGram(t);
		smap.put(testNGram, apiResponse);
		wb = new WebPage("shalti", "ghssg", "http://google.com");
	}

	@After
	public void tearDown() {
		// helper.tearDown();
	}
	Storage	               s;
	WebPage	               wb;
	ApiResponse	            apiResponse;
	Map<NGram, ApiResponse>	smap;
	NGram	                  testNGram;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	@Test
	public void test() {
		Assert.assertNotNull(apiResponse.myType);
		s.saveEntitesMap(wb, smap);
		// ApiResponse ar = s.loadEntity(wb, testNGram);
		// Assert.assertEquals(apiResponse.myType, ar.myType);
		// Assert.assertEquals(apiResponse.json, ar.json);
	}

}
