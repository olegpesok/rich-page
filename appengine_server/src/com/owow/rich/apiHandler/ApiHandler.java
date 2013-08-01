package com.owow.rich.apiHandler;

public interface ApiHandler {
	public ApiResponse getData(String title, ApiType at) throws Exception;
	public ApiView getView(ApiResponse fromGetData) throws Exception;
}
