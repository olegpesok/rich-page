import java.util.Map;

import org.junit.Test;

import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;
import com.owow.rich.utils.ComparisonUtils;
import com.owow.rich.utils.ComparisonUtils.ScoredObject;
import com.owow.rich.utils.ComparisonUtils.ScoredObjectList;


public class NlpUtilsTest {

	//TODO(guti): check how to mock the service:
	@Test
	public void test() {
		Map<String, ScoredObject<String>> map = Maps.newHashMap();
		map.put("1", new ScoredObject<String>("alon","1","alon"));
		map.put("2", new ScoredObject<String>("balon","2","alon"));
		map.put("3", new ScoredObject<String>("alon","3","alon"));
		map.put("4", new ScoredObject<String>("alon","4","alon"));
		
	   ScoredObjectList<String> res = new ComparisonUtils().rankDocumentsSimilarityToText("alon balon", map, "alon balon");
	}
}