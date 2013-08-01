package com.owow.rich.test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.apiHandler.GeographicHandler;
import com.owow.rich.apiHandler.WikipediaHandler;
import com.owow.rich.apiHandler.WikipediaHandler.WikiHost;

public class GeographicHandlrTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	private GeographicHandler	geographicalHandler;

	private WikipediaHandler	wh;
	@Before
	public void setUp() throws Exception {
		geographicalHandler = new GeographicHandler();
		wh = new WikipediaHandler();
		wh.setHost(WikiHost.AllEng);

	}

	@Test
	public void test() throws Exception {
		geographicalHandler.getData("israel", ApiType.geo);
	}

	@Test
	public void test2() throws Exception {
		wh.getData("TEA", ApiType.wiki);
	}

}
