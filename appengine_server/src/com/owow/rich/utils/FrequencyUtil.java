package com.owow.rich.utils;

import com.google.api.client.http.GenericUrl;
import com.owow.rich.RichLogger;

public class FrequencyUtil {

	public static int getFrequency(String nGram) {
		try{
			GenericUrl adamUrl = new GenericUrl("http://54.213.139.165:9090/");
			adamUrl.appendRawPath(nGram.toLowerCase());
			
			String numberString = HtmlUtil.getUrlSource(adamUrl.toString());
			return Integer.parseInt(numberString);
		} catch(Exception ex) {
			RichLogger.logException("Error in frequency util", ex);
			return -1;
		}
	}
}
