package com.owow.rich.apiHandler;

import java.util.Map;

import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;

public enum ApiType {
	// qoura(QouraHandler.class, "qo"),
	// google(GoogleHandler.class, "go"),
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
	twitter(TwitterHandler.class, "tw"),
	stackoverflaw(StackOverflowHandler.class, "st"),
	custom(StackOverflowHandler.class, "cu");

	public Class<? extends ApiHandler>	myClass;
	public String	                    nickname;
	ApiType(Class<? extends ApiHandler> mClass, String nick)
	{
		myClass = mClass;
		nickname = nick;
	}

	public String getIdentifyer()
	{
		return nickname;
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
		if (map == null) {
			setMap();
		}
		ApiType ap = map.get(s);
		return ap == null ? custom : ap;
	}

	static void setMap()
	{
		map = Maps.newHashMap();
		ApiType[] ats = ApiType.values();
		for (ApiType at : ats) {
			map.put(at.getIdentifyer(), at);
		}
	}
}
