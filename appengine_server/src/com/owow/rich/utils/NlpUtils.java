package com.owow.rich.utils;

import java.io.StringWriter;
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
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.labs.repackaged.org.json.XML;
import com.owow.rich.RichLogger;

public class NlpUtils {
	
	private final AlchemyAPI ALCHEMY_API = AlchemyAPI
			.GetInstanceFromString("dc86318ce4f5cf5ae2872376afe43940938d7edf");
	
	public class Tag {
		public String text;
		public double score;
		public String type;
		public Tag(String text, double score, String type) {
			this.text = text;
			this.score = score;
			this.type = type;
		}
		
		@Override
		public String toString(){
			return text + " s:" + score + " t:" + type;
		}
		@Override
		public int hashCode() {
		   return text.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Tag) {
				return text.equals(((Tag)obj).text);
			}
		   return false;
		} 
	}
	
	public class scoredResult implements Comparable<scoredResult>{
		public String text;
		public double score;
		public scoredResult(String text, double score) {
			this.text = text;
			this.score = score;
		}

		@Override
      public int compareTo(scoredResult o) {
	      return (int)((score - o.score)*10000);
      }
	}

	public List<scoredResult> rankResults(String text, List<String> textList) {
		List<scoredResult> scoredResults = Lists.newArrayList();
		List<Tag> tagList = extractAllTags(text);
		for (String otherText : textList) {
			List<Tag> otherTagList = extractAllTags(otherText);
			double score = compare(tagList, otherTagList);
			scoredResults.add(new scoredResult(otherText, score));
      }
		Collections.sort(scoredResults);
		return scoredResults;
	}
	
	public double compare(String text1, String text2) {
		List<Tag> tagsList1 = extractAllTags(text1);
		List<Tag> tagsList2 = extractAllTags(text2);
		return compare(tagsList1, tagsList2);
	}

	private double compare(List<Tag> tagsList1, List<Tag> tagsList2) {
		HashSet<Tag> tagSet1 = new HashSet<Tag>(tagsList1);
		HashSet<Tag> tagSet2 = new HashSet<Tag>(tagsList2);
		
		double score = 0;
		for (Tag tag : tagSet2) {
	      if(tagSet1.contains(tag)) {
	      	score += tag.score;
	      }
      }
		score /= Math.log(1 + tagsList1.size() + tagsList2.size());
		return score;
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

	private List<Tag> getAlchemyItems(Document document, String groupString, String itemString){
		JSONObject response = convertAlchmeyResponseToJson(document);
		List<Tag> results = Lists.newArrayList();
		try{	
			// Fucking API, if less then one results return object and not array. so handle both cases.
			Object groupObject = response.getJSONObject("results").get(groupString);
			if (groupObject instanceof JSONObject) {
				Object itemOrItemList = ((JSONObject)groupObject).get(itemString);
				if(itemOrItemList instanceof JSONArray)
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
