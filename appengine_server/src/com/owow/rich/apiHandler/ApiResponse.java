package com.owow.rich.apiHandler;

import org.json.JSONException;
import org.json.JSONObject;


public class ApiResponse {
	public JSONObject	json;
	public ApiView	   view;
	// public boolean resultOk = true;
	// private Exception mError;
	public ApiType	   myType;

	public ApiResponse(JSONObject json, String html, ApiType at){
		this(json, new ApiView(html) ,at);
	}
	
	public ApiResponse(JSONObject json, ApiView view, ApiType at)
	{
		this.json = json;
		this.view = view;
		myType = at;
		try {
			json.put("type", at.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public ApiResponse(JSONObject json, ApiType at)
	{
		this(json, new ApiView(""), at);
	}

	@Override
	public String toString() {
		JSONObject jo = new JSONObject();
		try {
			jo.put("json", json);
			jo.put("view", view);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jo.toString();
	}

	public ApiView getNullHandlerView() throws InstantiationException, IllegalAccessException, Exception {
		return myType.myClass.newInstance().getView(this);
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
