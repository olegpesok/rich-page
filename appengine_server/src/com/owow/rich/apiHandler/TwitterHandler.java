package com.owow.rich.apiHandler;

import com.google.appengine.api.oauth.OAuthService;
import com.google.appengine.api.oauth.OAuthServiceFactory;
import com.google.appengine.api.users.User;

public class TwitterHandler extends ApiHandler {

	@Override
	public ApiResponse getFirstResponse(String title, ApiType apiType) throws Exception {
		OAuthService oauth = OAuthServiceFactory.getOAuthService();
		User user;
		user = oauth.getCurrentUser();
		return null;
	}

	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
