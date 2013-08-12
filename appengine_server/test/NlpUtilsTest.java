import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.labs.repackaged.com.google.common.base.Function;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.owow.rich.items.ScoredResult;
import com.owow.rich.utils.NlpUtils;
import com.owow.rich.utils.NlpUtils.Tag;

public class NlpUtilsTest {
	NlpUtils	nlpUtils;

	@Before
	public void setUp() {
		nlpUtils = new NlpUtils();
	}

//	@Test
//	public void testRankResults() throws UnsupportedEncodingException {
//		List<ScoredResult> res = nlpUtils.rankResults("Stripe is one of the most successful startups",
//		      Lists.newArrayList("Stripe is a company that provides a way for individuals and businesses", "alon balon Google"));
//		res = res;
//	}

	@Test
	public void testCompare() {
		ScoredResult res = nlpUtils
		      .compare(
		            "Stripe is one of the most successful startups we've funded, and the problem they solved was an urgent one. If anyone could have sat back and waited for users, it was Stripe. But in fact they're famous within YC for aggressive early user acquisition.",
		            "Stripe is a company that provides a way for individuals and businesses to accept payments over the internet. Co-founded by brothers");
		res = res;
	}

	@Test
	public void testExtractConcepts() {
		List<Tag> results = nlpUtils
		      .extractConcepts("It's often said that you should live every day as though it's your last. That always seemed a little impractical to me. But when my brother died, I felt like I had missed my chance. Life could never be the same again. Part of me was already dead. I had always understood death intellectually, but now I felt it at a visceral level, and that's completely different. Death was inside of me now.");
		List<String> rewsultsText = Lists.transform(results, new Function<Tag, String>(){
			@Override
			public String apply(Tag tag) {
				return tag.text;
			}
		});

		Assert.assertNotNull(rewsultsText);
		Assert.assertTrue(rewsultsText.contains("Death"));
	}

	@Test
	public void testExtractEntities() {
		List<Tag> results = nlpUtils
		      .extractEntities("After the funeral, I returned to work. Sometimes, it's wonderful to be able to focus my mind on something simple, something I can control, like computers. A few weeks later we launched Gmail.");
		List<String> rewsultsText = Lists.transform(results, new Function<Tag, String>(){
			@Override
			public String apply(Tag tag) {
				return tag.text;
			}
		});

		Assert.assertNotNull(rewsultsText);
		Assert.assertTrue(rewsultsText.contains("Gmail"));
	}

	@Test
	public void testExtractKeyWords() {

		List<Tag> results = nlpUtils
		      .extractKeyWords("It's often said that you should live every day as though it's your last. That always seemed a little impractical to me. But when my brother died, I felt like I had missed my chance. Life could never be the same again. Part of me was already dead. I had always understood death intellectually, but now I felt it at a visceral level, and that's completely different. Death was inside of me now.");
		List<String> rewsultsText = Lists.transform(results, new Function<Tag, String>(){
			@Override
			public String apply(Tag tag) {
				return tag.text;
			}
		});

		Assert.assertNotNull(rewsultsText);
		Assert.assertTrue(rewsultsText.contains("death"));
		Assert.assertTrue(rewsultsText.contains("brother"));
	}
}