package com.owow.rich.utils;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.alchemyapi.api.AlchemyAPI;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.labs.repackaged.org.json.XML;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.owow.rich.RichLogger;
import com.owow.rich.items.ScoredResult;
import com.owow.rich.storage.PersistentCahce;


public class NlpUtils {

	private String getAlchemyApiKey(){
		return "f73a4e3d28fbb12206ba958524e55e9d1c3f265f";
//		return "dc86318ce4f5cf5ae2872376afe43940938d7edf";
//		return "d5f35667e3dbc7bba2936fb03991144dba85c18d";
//		return "7765f99062f08e86ad5caab7db048a57f3179384";
//		return "d511edb07905973cec006dee63a2110cca933e41";
   }
	
	private final AlchemyAPI ALCHEMY_API	= AlchemyAPI.GetInstanceFromString(getAlchemyApiKey());

	@Entity
	public static class TagSet {
		@Id long id;
		List<Tag> tags;
		private Text text;
		
		public TagSet(){}
		public TagSet(List<Tag> tags, int id, String text){
			this.id = id;
			this.tags = tags;
			this.text = new Text(text);
		}
	}
	
	@Embed
	public static class Tag{
		public String	text;
		public double	score;
		public String	type;
		
		public Tag(){};
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

	public ScoredResult compare(String text1, String text2) {
		try {
			text1 = URLDecoder.decode(text1, "UTF-8");
			text2 = URLDecoder.decode(text2, "UTF-8");
		} catch(Exception ex) {
			RichLogger.logException("error encoding", ex);
		}
		
		List<Tag> tagsList1 = getAllTagsLiveOrCahced(text1);
		List<Tag> tagsList2 = getAllTagsLiveOrCahced(text2);
		ScoredResult result = compare(tagsList1, tagsList2);
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
				score += 1;//tag.score;
			}
		score /= Math.log(1 + tagsList1.size() + tagsList2.size());
		return new ScoredResult(mathcingTagSet, score);
	}

	private List<Tag> getAllTagsLiveOrCahced(String text) {
		TagSet tagSet = ofy().load().type(TagSet.class).id(text.hashCode()).now();
		if (tagSet != null) {
			return tagSet.tags;
		}
		
		List<Tag> tagList = Lists.newArrayList();
		tagList.addAll(extractConcepts(text));
		tagList.addAll(extractEntities(text));
		tagList.addAll(extractKeyWords(text));
		
		
		tagSet = new TagSet(tagList, text.hashCode(), text);
		ofy().save().entity(tagSet).now();
		return tagSet.tags;
	}


	public List<Tag> extractConcepts(String text) {
		try {
			Document document = ALCHEMY_API.TextGetRankedConcepts(text);
			return getAlchemyItems(document, "concepts", "concept");
		} catch (Exception e) {
			PersistentCahce.set(""+text.hashCode() ,Lists.newArrayList(), "extractConcepts");
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
