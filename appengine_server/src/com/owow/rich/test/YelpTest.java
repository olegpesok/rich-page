package com.owow.rich.test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.owow.rich.apiHandler.YelpHandler;

public class YelpTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}
	YelpHandler.Yelp	yelp;
	@Before
	public void setUp() throws Exception {
		String consumerKey = "jWbRqLnH7D24KhWamKBXYw";
		String consumerSecret = "WRmRXxmTFfGoV5UYTg6s6VCYJA0";
		String token = "M6eQ7MMPocsNAeUQ0xHqINVzROk-mJ2M";
		String tokenSecret = "-nTGZEl5pAVezLG5TRch4RbsnqQ";

		yelp = new YelpHandler.Yelp(consumerKey, consumerSecret, token, tokenSecret);

	}

	@Test
	public void test() {
		String response = yelp.search("burritos", 30.361471, -87.164326);

		System.out.println(response);
	}

}
