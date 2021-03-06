package com.owow.rich.apiHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.PropertyContainer;
import com.google.appengine.api.datastore.Text;
import com.google.common.collect.Lists;
import com.owow.rich.items.Query;
import com.owow.rich.items.Result;

public class ApiResponse implements Serializable {

	public final static String	JSONKEY	         = "json";
	public final static String	VIEWKEY	         = "view";
	public final static String	BACKUPRESPONSEKEY	= "backUp";
	public final static String	APITYPEKEY	      = "apitype";
	public final static String	TITLEKEY	         = "title";
	public final static String	IDKEY	            = "id";
	public final static String	SCOREKEY	         = "score";
	public final static String	FILTER_REASON_KEY	         = "filter_reason";
	
	private static final long	serialVersionUID	= -4369034077791508101L;

	public boolean	            goodEnough	      = false;
	public JSONObject	         json;
	public ApiView	            view;

	public long	               apiScore;
	public String	            text;
	public String	            id;
	public String	            title;
	public List<String>	      alias	            = Lists.newArrayList();

	public boolean	            resultOk	         = true;
	// private Exception mError;
	public ApiType	            myType;
	public String filterReason = "NOT_FILTERD";

	public ApiResponse(JSONObject json, String html, ApiType apiType, int score, String text, String id, String title, List<String> alias) {
		apiScore = score;
		this.text = text;
		this.json = json;
		view = new ApiView(html);
		this.id = id;
		this.title = title;
		this.alias = alias;
		myType = apiType;
	}

	public ApiResponse(String title, JSONObject json, String html, ApiType apiType) {
		this(title, json, new ApiView(html), apiType);
	}

	public ApiResponse(String title, JSONObject json, ApiView view, ApiType apiType)
	{
		this(json, view.getView(), apiType, 0, view.getView(), null, title, new ArrayList<String>());
	}

	public ApiResponse(String title, JSONObject json, ApiType type)
	{
		this(title, json, new ApiView(""), type);
	}

	@Override
	public String toString() {
		final JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("json", json);
			jsonObject.put("view", view);
		} catch (final JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

	public ApiView getNullHandlerView() throws InstantiationException, IllegalAccessException, Exception {
		return myType.myClass.newInstance().getView(this);
	}

	public PropertyContainer getPropertyContainerFromApiResponse(ApiResponse... apiResponses)
	{
		final PropertyContainer propertyContainer = new EmbeddedEntity();
		propertyContainer.setProperty(VIEWKEY, new Text(view.getView().toString()));
		propertyContainer.setProperty(APITYPEKEY, myType == null ? null : myType.getIdentifyer());

		// Backup
		if (apiResponses.length > 0)
		{
			final EmbeddedEntity embeddedViewsEntity = new EmbeddedEntity();

			int index = 0;
			for (ApiResponse apiResponse : apiResponses) {
				index++;
				embeddedViewsEntity.setProperty(BACKUPRESPONSEKEY + index, apiResponse.getPropertyContainerFromApiResponse());
			}
			embeddedViewsEntity.setProperty("length", index);
			propertyContainer.setProperty(BACKUPRESPONSEKEY, embeddedViewsEntity);
		}
		// end backup

		propertyContainer.setProperty(TITLEKEY, title);
		propertyContainer.setProperty(SCOREKEY, apiScore);
		propertyContainer.setProperty(IDKEY, id);
		propertyContainer.setProperty(FILTER_REASON_KEY, filterReason);

		return propertyContainer;
	}

	public EmbeddedEntity getPropertyContainerFromApiResponse()
	{
		return (EmbeddedEntity) getPropertyContainerFromApiResponse(new ApiResponse[0]);
	}

	public static ApiResponse getApiResponseFromEntity(String title, PropertyContainer ent)
	{
		if (!ent.hasProperty(APITYPEKEY)) return null;
		final ApiView apiView = !ent.hasProperty(VIEWKEY) ? new ApiView("") : new ApiView(((Text) ent.getProperty(VIEWKEY)).getValue());
		final ApiType apiType = ApiType.create((String) ent.getProperty(APITYPEKEY));

		final ApiResponse ar = new ApiResponse(title, new JSONObject(), apiView, apiType);

		ar.apiScore = ent.hasProperty(SCOREKEY) && ent.getProperty(SCOREKEY) != null
		      ? (Long) ent.getProperty(SCOREKEY) : -1;
		ar.id = (String) (ent.hasProperty(IDKEY) ? ent.getProperty(IDKEY) : null);
		ar.title = (String) (ent.hasProperty(TITLEKEY) ? ent.getProperty(TITLEKEY) : null);
		return ar;
	}

	public static ApiResponse getApiResponseFromEntity(PropertyContainer ent) {
		return getApiResponseFromEntity(null, ent);
	}

	public List<String> getTitleAndAliases() {
		List<String> aliasAndTitle = Lists.newArrayList(alias);
		aliasAndTitle.add(title);
//		aliasAndTitle.add(text.substring(0, Math.min(200, text.length())));
		return aliasAndTitle;
   }

	public List<Result> getResults(Query query) {
		Result result = new Result(title, view.getView(), text, apiScore, query.highlight, filterReason, alias);
	   return Lists.newArrayList(result);
   }
}
