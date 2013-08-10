package com.owow.rich.apiHandler;

import java.io.Serializable;

public class ApiView implements Serializable{
	private String	mView;
	public ApiView(String view)
	{
		mView = view;
	}
	public String getView()
	{
		return mView;
	}
	public void setView(String View)
	{
		mView = View;
	}

	@Override
	public String toString() {
		return mView;
	}
}
