import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.labs.repackaged.com.google.common.base.Pair;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.owow.rich.utils.NameExtractor;

public class NameExtractorTest {

	NameExtractor extractor;
	
	@Before
	public void setUp() {
		extractor = new NameExtractor();
	}

	@Test
	public void testExtractNames() throws JSONException, IOException {
		List<Pair<String, String>> inputAndExpectedResultList = Lists.newArrayList();
		inputAndExpectedResultList.add(Pair.of(
			"Last week Pando published a very widely circulated (and very good) article providing a post mortem of the 'Facebook platform', explaining how something that might have been a big part of the future of the web ran into the sand. The timing was interesting, though, because it co-incided with the spectacular acceleration of growth in Facebook's mobile revenue, which was effectively zero a year ago and is now 40% of Facebook's ad revenue and all of the growth. This isn't a function of an increase in mobile users, since the trend there has been much more moderate. There may be some usage increase, though, since Facebook has significantly improved the smartphone apps, dumping a failed HTML5 wrapper approach. There may also be conversion from feature phone to smart - the feature phone app has 100m users but had 82m in November last year - not much growth on that side. ",
			"[[week, Pando], [), article], [post, mortem], [platform], [something], [part], [future], [web], [sand., The, timing], [acceleration], [growth], [Facebook], [revenue], [year], [%], [Facebook], [ad, revenue], [growth., This], [function], [increase], [mobile, users], [trend], [moderate., There], [usage, increase], [Facebook], [smartphone, apps], [HTML5, wrapper, approach., There], [conversion], [feature, phone], [feature, phone, app], [users], [November], [year], [growth], [side]]"));
		
		inputAndExpectedResultList.add(Pair.of(
				"Whatsapp claims over 250m active users and Line, Viber and to some extend WeChat (though mainly Chinese) all have global scale. There are dozens of others with over 10m users each, and a while ago I poked around Google Play long enough to find 50 social messaging apps that have had more than 1m downloads. Obviously these are downloads rather than users, but Line claims 80% of its 200m registered users are active monthly and Whatsapp, of course, says 250m. Outside of China and Japan, almost all of these users have a Facebook account too. Yet they're using these other services instead.",
				"[[Whatsapp], [users], [Line], [Viber], [extend, WeChat, (], [)], [scale., There], [dozens], [others], [users], [while], [Google, Play], [messaging, apps], [downloads., Obviously], [downloads], [users], [Line], [%], [users], [Whatsapp], [course], [Outside], [China], [Japan], [users], [Facebook, account, too., Yet], [services]]"));
		inputAndExpectedResultList.add(Pair.of(
				"It's pretty clear that these aren't just going to be subsumed into Facebook - rather they unbundle key parts of the Facebook mobile experience. The interesting question is whether that actually matters, given Facebook's revenue trajectory. Is the mobile opportunity big enough that it just doesn't matter that Facebook doesn't have the dominance that it has on the desktop?",
				"[[Facebook], [parts], [Facebook, mobile, experience., The], [question], [Facebook], [revenue, trajectory., Is], [opportunity], [Facebook], [dominance], [desktop]]"));
		
		for (Pair<String, String> pair : inputAndExpectedResultList) {
			String inputText = pair.first;
			
			// TEST:
			List<List<String>> results = extractor.getNameExtractor(inputText);
			
			// ASSERT:
			Assert.assertEquals(pair.second, results.toString());
      }
		
	}

}