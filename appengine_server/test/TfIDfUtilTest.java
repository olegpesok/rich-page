import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;
import com.owow.rich.utils.TFIDFUtil;
import com.owow.rich.utils.TFIDFUtil.DocumentScore;


public class TfIDfUtilTest {

	//TODO(guti): check how to mock the service:
	@Test
	public void test() {
		Map<String, String> map = Maps.newHashMap();
		map.put("1", "alon");
		map.put("2", "balon");
		map.put("3", "alon");
		map.put("4", "alon");
	   List<DocumentScore> res = new TFIDFUtil().rankDocumentsSimilarityToText("alon balon", map, "alon balon");
	}
}
