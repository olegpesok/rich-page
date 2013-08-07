package com.owow.rich.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.api.client.http.GenericUrl;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;



public class NameExtractor {
	
	public List<List<String>> getNameExtractor(String pageUrl) throws JSONException, IOException {
		try{
		//Sends request to Python:
		GenericUrl herokuUrl = new GenericUrl("http://stormy-forest-1816.herokuapp.com/");
		herokuUrl.put("url", pageUrl);
		String jsonString = HtmlUtil.getUrlSource(herokuUrl.toString());
		JSONArray jsonArray = new JSONArray(jsonString);
		List<List<String>> results = Lists.newArrayList();
		for (int i = 0; i < jsonArray.length(); i++) {
	      JSONArray namesArray = jsonArray.getJSONArray(i);
			List<String> names = new ArrayList<String>();
	      for (int j = 0; j < namesArray.length(); j++) {
				JSONArray nameAndType = namesArray.getJSONArray(j);
				String name = nameAndType.getString(0);
				names.add(name);
         }
	      results.add(names);
      }
		//Process the JSON:
		return results;
		} catch(Exception ex) {
			return null;
		}
	}
}
