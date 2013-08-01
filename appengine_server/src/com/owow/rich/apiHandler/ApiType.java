package com.owow.rich.apiHandler;

import java.util.Map;

import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;

public enum ApiType {
	qoura(QouraHandler.class, "qo"),
	google(GoogleHandler.class, "go"),
	crunch(CrunchBaseHandler.class, "cr"),
	wiki(WikipediaHandler.class, "wi"),
	dictionary(DictionaryHandler.class, "di"),
	duckduck(DuckDuckGoHandler.class, "dd"),
	wolframalpha(WolframAlphaHandler.class, "wa"),
	ckan(CkanHandler.class, "ck"),
	quandl(QuandlHandler.class, "qu"),
	geo(GeographicHandler.class, "ge"),
	freebase(FreebaseHandler.class, "fr"),
	yelp(YelpHandler.class, "ye"),
	stackoverflaw(StackOverflowHandler.class, "st");

	public Class<? extends ApiHandler>	myClass;
	public String	                    sId;
	ApiType(Class<? extends ApiHandler> c, String id)
	{
		myClass = c;
		sId = id;
	}

	public String getIdentifyer()
	{
		return sId;
	}
	public ApiHandler createHandler()
	{
		try {
			return myClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	static Map<String, ApiType>	map;
	public static ApiType create(String s)
	{
		if (map == null) setMap();
		return map.get(s);
	}

	static void setMap()
	{
		map = Maps.newHashMap();
		ApiType[] ats = ApiType.values();
		for (int i = 0; i < ats.length; i++)
			map.put(ats[i].getIdentifyer(), ats[i]);
	}
}
