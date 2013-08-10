package com.owow.rich.generalHandler;

import java.util.Map;

import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;

public enum GeneralType {

	// qoura(QouraHandler.class, "qo"),
	// google(GoogleHandler.class, "go"),
	// bing(BingHandler.class, "bi")
	;

	public Class<? extends GeneralHandler>	myClass;
	public String	                        nickname;
	GeneralType(Class<? extends GeneralHandler> mClass, String nick)
	{
		myClass = mClass;
		nickname = nick;
	}

	public String getIdentifyer()
	{
		return nickname;
	}
	public GeneralHandler createHandler()
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
	static Map<String, GeneralType>	map;
	public static GeneralType create(String s)
	{
		if (map == null) {
			setMap();
		}
		GeneralType ap = map.get(s);
		return ap == null ? null : ap;
	}

	static void setMap()
	{
		map = Maps.newHashMap();
		GeneralType[] ats = GeneralType.values();
		for (GeneralType at : ats) {
			map.put(at.getIdentifyer(), at);
		}
	}
}
