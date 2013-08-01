package com.owow.rich.test;

//import java.util.List;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.appengine.labs.repackaged.com.google.common.base.Function;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.owow.rich.entity.NameExtractor;
import com.owow.rich.items.SearchTerm;

/**
 * Tests for {@link Foo}.
 *
 * @author user@example.com (John Doe)
 */
@RunWith(JUnit4.class)
public class NameExtractorTest {

	@Test
	public void testHighlightConstructor() {
		final List<SearchTerm> results = new NameExtractor().extract("How do I pretty-print existing JSON data with Java?");

		// Assert
		Assert.assertArrayEquals(
		      new String[]{"JSON", "Java?"},
		      Lists.transform(results, new Function<SearchTerm, String>(){
			      @Override
			      public String apply(final SearchTerm arg0) {
				      return arg0.term;
			      }
		      }).toArray());
	}
}
