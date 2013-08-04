package com.owow.rich.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.alchemyapi.api.AlchemyAPI;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.labs.repackaged.org.json.XML;
import com.owow.rich.RichLogger;
import com.owow.rich.items.SearchTerm;

public class NlpUtils {
	
	private final AlchemyAPI ALCHEMY_API = AlchemyAPI
			.GetInstanceFromString("dc86318ce4f5cf5ae2872376afe43940938d7edf");
	
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
	
	public List<String> extractConcepts(String text) {
      try {
         Document document = ALCHEMY_API.TextGetRankedConcepts(text);
			return getAlchemyItems(document, "concepts", "concept");
      } catch (Exception e) {
      	return Lists.newArrayList();
      }
   }

	public List<String> extractEntities(String text) {
      try {
         Document document = ALCHEMY_API.TextGetRankedNamedEntities(text);
			return getAlchemyItems(document, "entities", "entity");
      } catch (Exception e) {
      	return Lists.newArrayList();
      }
   }
	
	public List<String> extractKeyWords(String text) {
		 try {
	         Document document = ALCHEMY_API.TextGetRankedKeywords(text);
				return getAlchemyItems(document, "keywords", "keyword");
	      } catch (Exception e) {
	      	return Lists.newArrayList();
	      }
   }

	private List<String> getAlchemyItems(Document document, String groupString, String itemString){
		JSONObject response = convertAlchmeyResponseToJson(document);
		List<String> results = Lists.newArrayList();
		try{	
			// Fucking API, if less then one results return object and not array. so handle both cases.
			Object itemOrItemList = response.getJSONObject("results").getJSONObject(groupString).get(itemString);
			if(itemOrItemList instanceof JSONArray)
			{
				JSONArray itemList = (JSONArray) itemOrItemList;
				for (int i = 0; i < itemList.length(); i++) {
					String item = itemList.getJSONObject(i).getString("text");
					results.add(item);
				}
			} else if (itemOrItemList instanceof JSONObject) {
				JSONObject itemObject = (JSONObject) itemOrItemList;
				String item = itemObject.getString("text");
				results.add(item);
			}
		} catch (Exception e) {
			RichLogger.log.severe("alchemy api extract rentities");
		}
	   return results;
   }



	public String categorizeText(String text) {
	   return null;
   }

}
