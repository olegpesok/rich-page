import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.owow.rich.utils.NlpUtils;


public class NlpUtilsTest {
	NlpUtils nlpUtils;
	
	@Before
	public void setUp() {
		nlpUtils = new NlpUtils();
	}
	
	@Test
	public void testExtractConcepts() {
		List<String> results = nlpUtils.extractConcepts("It's often said that you should live every day as though it's your last. That always seemed a little impractical to me. But when my brother died, I felt like I had missed my chance. Life could never be the same again. Part of me was already dead. I had always understood death intellectually, but now I felt it at a visceral level, and that's completely different. Death was inside of me now.");
		
		Assert.assertNotNull(results);
		Assert.assertTrue(results.contains("God"));
		Assert.assertTrue(results.contains("Death"));
	}
	
	@Test
	public void testExtractEntities() {
		List<String> results = nlpUtils.extractEntities("After the funeral, I returned to work. Sometimes, it's wonderful to be able to focus my mind on something simple, something I can control, like computers. A few weeks later we launched Gmail.");
		
		Assert.assertNotNull(results);
		Assert.assertTrue(results.contains("Gmail"));
	}
	
	@Test
	public void testExtractKeyWords() {
		
		List<String> results = nlpUtils.extractKeyWords("It's often said that you should live every day as though it's your last. That always seemed a little impractical to me. But when my brother died, I felt like I had missed my chance. Life could never be the same again. Part of me was already dead. I had always understood death intellectually, but now I felt it at a visceral level, and that's completely different. Death was inside of me now.");
		
		Assert.assertNotNull(results);
		Assert.assertTrue(results.contains("death"));
		Assert.assertTrue(results.contains("brother"));
	}
}