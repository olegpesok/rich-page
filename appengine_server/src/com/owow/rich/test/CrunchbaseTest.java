package com.owow.rich.test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.apiHandler.CrunchBaseHandler;

public class CrunchbaseTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	CrunchBaseHandler	cbh;
	@Before
	public void setUp() throws Exception {
		cbh = new CrunchBaseHandler();
	}

	@Test
	public void test() throws Exception {
		ApiResponse apiResponse = cbh.getFirstResponse("Microsoft", ApiType.crunch);
		System.out.print(apiResponse.json);
	}

}
