package com.owow.rich.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.owow.rich.utils.StringTokenizer;

public class StringTokenizerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	@Before
	public void setUp() throws Exception {}

	@Test
	public void test() {
		String[] tokens = new StringTokenizer("Shalti theking, shga", ", ").tokenize();
		Assert.assertTrue(tokens.length + "", tokens.length == 3);
	}
}
