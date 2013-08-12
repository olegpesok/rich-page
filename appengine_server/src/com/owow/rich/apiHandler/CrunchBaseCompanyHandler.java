package com.owow.rich.apiHandler;

import java.text.DateFormatSymbols;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.owow.rich.items.Image;
import com.owow.rich.utils.HtmlUtil;

public class CrunchBaseCompanyHandler extends ApiHandler {

	@Override
	public ApiResponse getFirstResponse(String title, ApiType type) throws Exception {

		// Twitter Username!!
		final String server = "http://api.crunchbase.com/v/1/company/" + title + ".js?api_key=5bfua4j796x3dthwr9sqcv4r";
		final String serverDataResponse = HtmlUtil.getUrlSource(server);

		JSONObject jsonData = new JSONObject(serverDataResponse);

		if (jsonData.length() == 0) {
			Logger.getLogger(CrunchBaseHandler.class.toString()).info("no results. title = " + title);
			return null;
		}

		final JSONObject ret = new JSONObject();
		ret.put("data", jsonData);

		ApiResponse ar = new ApiResponse(title, ret, type);
		String founded = new DateFormatSymbols().getMonths()[jsonData.getInt("founded_month") - 1] + " " + jsonData.getInt("founded_day") + ", "
		      + jsonData.getInt("founded_year");
		String imageUrl = fixImage(jsonData.getJSONObject("image").getJSONArray("available_sizes").getJSONArray(0).getString(1));
		JSONArray relationships = jsonData.getJSONArray("relationships");
		Image[] leaders = new Image[Math.min(3, relationships.length())];
		for (int i = 0; i < leaders.length; i++)
		{
			leaders[i] = getImagePerson(relationships.getJSONObject(i));
		}
		JSONArray competitions = jsonData.getJSONArray("relationships");
		Image[] competitors = new Image[Math.min(3, competitions.length())];
		for (int i = 0; i < leaders.length; i++)
		{
			leaders[i] = getImagePerson(competitions.getJSONObject(i));
		}
		ar.view = new ApiView(getView(jsonData.getString("name"), jsonData.getString("overview"), founded, imageUrl, leaders, competitors));
		return ar;
	}
	Image getImagePerson(JSONObject jo) throws JSONException
	{
		String subtitle = jo.getString("title");
		jo = jo.getJSONObject("person");
		String name = jo.getString("first_name") + jo.getString("last_name");
		String imageUrl = fixImage(jo.getJSONObject("image").getJSONArray("available_sizes").getJSONArray(0).getString(1));
		return new Image(name, subtitle, imageUrl, 150, 150);
	}

	Image getImageCompetitor(JSONObject jo) throws JSONException
	{
		try {
			jo = jo.getJSONObject("competitor");
			String name = jo.getString("name");
			String imageUrl = fixImage(jo.getJSONObject("image").getJSONArray("available_sizes").getJSONArray(0).getString(1));
			return new Image(name, null, imageUrl, 150, 150);
		} catch (Exception exception)
		{
			return null;
		}

	}
	String fixImage(String imageUrl)
	{
		return "http://www.crunchbase.com" + imageUrl;
	}
	String getView(String name, String overview, String founded, String imageUrl, Image[] leaders, Image[] Competitors)
	{

		return imageUrl;
	}

	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		String view = "";

		return null;
	}
}