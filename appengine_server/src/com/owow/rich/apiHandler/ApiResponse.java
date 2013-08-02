package com.owow.rich.apiHandler;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PropertyContainer;
import com.google.appengine.api.datastore.Text;

public class ApiResponse implements Serializable {

	public final static String	JSONKEY	        = "json";
	public final static String	VIEWKEY	        = "view";
	public final static String	APITYPEKEY	     = "apitype";

	private static final long	serialVersionUID	= -4369034077791508101L;
	public JSONObject	         json;
	public ApiView	            view;
	public boolean	            resultOk	        = true;
	// private Exception mError;
	public ApiType	            myType;

	public ApiResponse(JSONObject json, String html, ApiType apiType) {
		this(json, new ApiView(html), apiType);
	}

	public ApiResponse(JSONObject json, ApiView view, ApiType apiType)
	{
		this.json = json;
		this.view = view;
		myType = apiType;
	}
	public ApiResponse(JSONObject json, ApiType type)
	{
		this(json, new ApiView(""), type);
	}

	@Override
	public String toString() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("json", json);
			jsonObject.put("view", view);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

	public ApiView getNullHandlerView() throws InstantiationException, IllegalAccessException, Exception {
		return myType.myClass.newInstance().getView(this);
	}

	public PropertyContainer getPropertyContainerFromApiResponse()
	{
		PropertyContainer propertyContainer = new EmbeddedEntity();
		propertyContainer.setProperty(JSONKEY, new Text(json.toString()));

		propertyContainer.setProperty(VIEWKEY, new Text(view.getView().toString()));
		propertyContainer.setProperty(APITYPEKEY, myType.getIdentifyer());

		return propertyContainer;
	}

	public static ApiResponse getApiResponseFromEntity(Entity ent)
	{
		if (!ent.hasProperty(APITYPEKEY)) return null;
		JSONObject json;
		try {
			json = new JSONObject(((Text) ent.getProperty(VIEWKEY)).getValue());
		} catch (JSONException e) {
			json = null;
		}
		ApiView apiView = new ApiView(((Text) ent.getProperty(VIEWKEY)).getValue());
		ApiType apiType = ApiType.create((String) ent.getProperty(APITYPEKEY));
		return new ApiResponse(json, apiView, apiType);
	}
	// public boolean isOK()
	// {
	// return resultOk;
	// }
	// public Exception getError()
	// {
	// return mError;
	// }
	// public void setError(Exception Error)
	// {
	// resultOk = false;
	// mError = Error;
	// }
}
