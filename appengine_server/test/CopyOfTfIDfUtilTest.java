import org.junit.Assert;
import org.junit.Test;

import com.owow.rich.utils.FrequencyUtil;


public class CopyOfTfIDfUtilTest {

	@Test
	public void test() {
		Assert.assertEquals(4445, FrequencyUtil.getFrequency("a test"));
		Assert.assertEquals(-1, FrequencyUtil.getFrequency("an test"));
		Assert.assertEquals(2902, FrequencyUtil.getFrequency("apple"));
		Assert.assertEquals(2902, FrequencyUtil.getFrequency("Apple"));
	}
}
