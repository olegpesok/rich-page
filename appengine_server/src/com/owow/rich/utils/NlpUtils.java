package com.owow.rich.utils;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.alchemyapi.api.AlchemyAPI;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.labs.repackaged.org.json.XML;
import com.owow.rich.RichLogger;

public class NlpUtils {

	private final String	    API_KEY	      = "d5f35667e3dbc7bba2936fb03991144dba85c18d";
	// private final String API_KEY = "dc86318ce4f5cf5ae2872376afe43940938d7edf";

	private final AlchemyAPI	ALCHEMY_API	= AlchemyAPI.GetInstanceFromString(API_KEY);

	public class Tag {
		public String	text;
		public double	score;
		public String	type;
		public Tag(String text, double score, String type) {
			this.text = text;
			this.score = score;
			this.type = type;
		}

		@Override
		public String toString() {
			return text + " s:" + score + " t:" + type;
		}
		@Override
		public int hashCode() {
			return text.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Tag) return text.equals(((Tag) obj).text);
			return false;
		}
	}

	public class ScoredResult implements Comparable<ScoredResult> {
		public List<Tag>	firstTagList;
		public List<Tag>	seconTagdList;
		public Set<Tag>	matchingTagSet;
		public String		firstText;
		public String		secondText;
		public double		score;

		public ScoredResult(List<Tag> firstTagList, List<Tag> secondTagList, Set<Tag> mathcingTagSet, double score) {
			this.firstTagList = firstTagList;
			seconTagdList = secondTagList;
			matchingTagSet = mathcingTagSet;
			this.score = score;
		}

		@Override
		public String toString() {
			return "score:" + score + " matching tags:" + matchingTagSet;
		}

		@Override
		public int compareTo(ScoredResult o) {
			return (int) ((score - o.score) * 10000);
		}
	}

	public List<ScoredResult> rankResults(String text, List<String> textList) throws UnsupportedEncodingException {
		text = URLDecoder.decode(text, "UTF-8");

		List<ScoredResult> scoredResults = Lists.newArrayList();
		List<Tag> tagList = extractAllTags(text);
		for (String otherText : textList) {
			otherText = URLDecoder.decode(otherText, "UTF-8");
			List<Tag> otherTagList = extractAllTags(otherText);
			ScoredResult scoreResult = compare(tagList, otherTagList);
			scoredResults.add(scoreResult);
		}
		Collections.sort(scoredResults);
		return scoredResults;
	}

	public ScoredResult compare(String text1, String text2) {
		try {
			text1 = URLDecoder.decode(text1, "UTF-8");
			text2 = URLDecoder.decode(text2, "UTF-8");
<<<<<<< HEAD
		} catch(Exception e) {
//			RichLogger.log.log(Level.SEVERE, "fucking encoding " + text1 + " AND " + text2, e);
=======
		} catch (Exception e) {
			RichLogger.log.log(Level.SEVERE, "fucking encoding " + text1 + " AND " + text2, e);
>>>>>>> 7fddcfd618f6f60d07d8af069df26a0e566db897
		}

		List<Tag> tagsList1 = extractAllTags(text1);
		List<Tag> tagsList2 = extractAllTags(text2);
		ScoredResult result = compare(tagsList1, tagsList2);
		result.firstText = text1;
		result.secondText = text2;
		return result;
	}

	private ScoredResult compare(List<Tag> tagsList1, List<Tag> tagsList2) {
		HashSet<Tag> tagSet1 = new HashSet<Tag>(tagsList1);
		HashSet<Tag> tagSet2 = new HashSet<Tag>(tagsList2);

		HashSet<Tag> mathcingTagSet = new HashSet<Tag>();
		double score = 0;
		for (Tag tag : tagSet2)
			if (tagSet1.contains(tag)) {
				mathcingTagSet.add(tag);
				score += tag.score;
			}
		score /= Math.log(1 + tagsList1.size() + tagsList2.size());
		return new ScoredResult(tagsList1, tagsList2, mathcingTagSet, score);
	}

	public List<Tag> extractAllTags(String text) {
		List<Tag> results = Lists.newArrayList();
		results.addAll(extractConcepts(text));
		results.addAll(extractEntities(text));
		results.addAll(extractKeyWords(text));
		return results;
	}

	public List<Tag> extractConcepts(String text) {
		try {
			Document document = ALCHEMY_API.TextGetRankedConcepts(text);
			return getAlchemyItems(document, "concepts", "concept");
		} catch (Exception e) {
			return Lists.newArrayList();
		}
	}

	public List<Tag> extractEntities(String text) {
		try {
			Document document = ALCHEMY_API.TextGetRankedNamedEntities(text);
			return getAlchemyItems(document, "entities", "entity");
		} catch (Exception e) {
			return Lists.newArrayList();
		}
	}

	public List<Tag> extractKeyWords(String text) {
		try {
			Document document = ALCHEMY_API.TextGetRankedKeywords(text);
			return getAlchemyItems(document, "keywords", "keyword");
		} catch (Exception e) {
			return Lists.newArrayList();
		}
	}

	private List<Tag> getAlchemyItems(Document document, String groupString, String itemString) {
		JSONObject response = convertAlchmeyResponseToJson(document);
		List<Tag> results = Lists.newArrayList();
		try {
			// Fucking API, if less then one results return object and not array.
			// so handle both cases.
			Object groupObject = response.getJSONObject("results").get(groupString);
			if (groupObject instanceof JSONObject) {
				Object itemOrItemList = ((JSONObject) groupObject).get(itemString);
				if (itemOrItemList instanceof JSONArray)
				{
					JSONArray itemList = (JSONArray) itemOrItemList;
					for (int i = 0; i < itemList.length(); i++) {
						String item = itemList.getJSONObject(i).getString("text");
						double relevance = Double.parseDouble(itemList.getJSONObject(i).getString("relevance"));
						results.add(new Tag(item, relevance, itemString));
					}
				} else if (itemOrItemList instanceof JSONObject) {
					JSONObject itemObject = (JSONObject) itemOrItemList;
					String item = itemObject.getString("text");
					double relevance = Double.parseDouble(itemObject.getString("relevance"));
					results.add(new Tag(item, relevance, itemString));
				}
			}
		} catch (Exception e) {
			RichLogger.log.severe("alchemy api extract rentities");
		}
		return results;
	}

	private static JSONObject convertAlchmeyResponseToJson(Document doc) {
		try {

			StringWriter sw = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer
			      .setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(new DOMSource(doc), new StreamResult(sw));

			return XML.toJSONObject(sw.toString());
		} catch (Exception ex) {
			RichLogger.log.severe("Can parse alchemy doc");
			return null;
		}
	}

	public String categorizeText(String text) {
		return null;
	}

}
