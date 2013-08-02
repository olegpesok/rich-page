package com.owow.rich.apiHandler;

public interface ApiHandler {
	public ApiResponse getData(String title, ApiType apiType) throws Exception;
	public ApiView getView(ApiResponse fromGetData) throws Exception;
}
