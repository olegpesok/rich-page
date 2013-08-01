package com.owow.rich.entity;

import java.io.StringWriter;
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
import com.owow.rich.items.SearchTerm;


public class EntityExtractor {
	public List<SearchTerm> extract(String text) {
		AlchemyAPI alchemyApi = AlchemyAPI
				.GetInstanceFromString("dc86318ce4f5cf5ae2872376afe43940938d7edf");
		try {
			Document document = alchemyApi.TextGetRankedNamedEntities(text);
			JSONObject response = XML.toJSONObject(toString(document));
			JSONArray entites = response.getJSONObject("results").getJSONObject("entities").getJSONArray("entity");
			List<SearchTerm> results = Lists.newArrayList();
			for (int i = 0; i < entites.length(); i++) {
				String name = entites.getJSONObject(i).getJSONObject("disambiguated").getString("name");
				results.add(SearchTerm.create(name));
			}
			return results;
		} catch (Exception e) {
			return Lists.newArrayList();
		}
	}
	
	public static String toString(Document doc) {
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
			return sw.toString();
		} catch (Exception ex) {
			throw new RuntimeException("Error converting to String", ex);
		}
	}
}
