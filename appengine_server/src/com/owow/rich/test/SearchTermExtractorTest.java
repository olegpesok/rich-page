package com.owow.rich.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import com.google.appengine.labs.repackaged.com.google.common.base.Function;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.owow.rich.entity.EntityExtractor;
import com.owow.rich.entity.SearchTermExtractor;
import com.owow.rich.items.Highlight;
import com.owow.rich.items.SearchTerm;

/**
 * Tests for {@link Foo}.
 *
 * @author user@example.com (John Doe)
 */
@RunWith(JUnit4.class)
public class SearchTermExtractorTest {

	private SearchTermExtractor	preProcessor;
	private EntityExtractor	    entityExtractorMock;

	public static Highlight[]	 HIGHLIGHTS	= new Highlight[]{
	      new Highlight(
	            "Iceland",
	            "I went on a vacation to Iceland earlier this year"
	      ),
	      new Highlight(
	            "iendster",
	            "(e.g. Friendster, Digg, and Myspace)."
	      ),
	      new Highlight(
	            "Judge Michael J. Rus",
	            "Judge Michael J. Russo went over the deal with Castro"
	      ),
	      new Highlight(
	            " New York's",
	            "the accelerator backed by New York's Economic Development Council"
	      ),
	      new Highlight(
	            "Mohammed Morsi",
	            "In what is seen as a trial of strength, supporters of Mohammed Morsi filled the streets around a mosque in Cairo to condemn his removal by the army. "
	      ),
	                                       };

	@Before
	public void setUp() {
		entityExtractorMock = Mockito.mock(EntityExtractor.class);
		preProcessor = new SearchTermExtractor(entityExtractorMock);
	}

	@Test
	public void testHighlightConstructor() {
		Assert.assertEquals(24, HIGHLIGHTS[0].startIndex);
		Assert.assertEquals(31, HIGHLIGHTS[0].endIndex);
		Assert.assertEquals(HIGHLIGHTS[0].highlight,
		      HIGHLIGHTS[0].text.substring(HIGHLIGHTS[0].startIndex, HIGHLIGHTS[0].endIndex));
	}

	@Test
	public void testBasicExtractTerms() {
		final String expected = "Iceland";
		final List<SearchTerm> results = preProcessor.extractTerms(HIGHLIGHTS[0]);
		Assert.assertEquals(expected, results.get(0).term);
	}

	@Test
	public void testExtractTermsWithPartialStart() {
		final String expected = "Friendster";
		final List<SearchTerm> results = preProcessor.extractTerms(HIGHLIGHTS[1]);
		Assert.assertEquals(expected, results.get(0).term);
	}

	@Test
	public void testExtractTermsWithPartialEnd() {
		final String expected = "Judge Michael J. Russo";
		final List<SearchTerm> results = preProcessor.extractTerms(HIGHLIGHTS[2]);
		Assert.assertEquals(expected, results.get(0).term);
	}

	@Test
	public void testCleanTerms() {
		final String expected = "New York";
		final List<SearchTerm> results = preProcessor.extractTerms(HIGHLIGHTS[3]);
		Assert.assertEquals(expected, results.get(0).term);
	}

	@Test
	public void testExtractEntites() {
		// Prepare
		final List<String> expected = Lists.newArrayList("Mohammed Morsi", "Mohamed Morsi", "Cairo");
		Mockito.when(entityExtractorMock.extract(Mockito.anyString())).thenReturn(
		      Lists.newArrayList(
		            SearchTerm.create("Mohamed Morsi"),
		            SearchTerm.create("Cairo")
		            ));

		// Test
		final List<SearchTerm> results = preProcessor.extractTerms(HIGHLIGHTS[4]);

		// Assert
		Assert.assertArrayEquals(
		      expected.toArray(),
		      Lists.transform(results, new Function<SearchTerm, String>(){
			      @Override
			      public String apply(final SearchTerm arg0) {
				      return arg0.term;
			      }
		      }).toArray());
	}

}