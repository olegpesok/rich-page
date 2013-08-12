package com.owow.rich.apiHandler;

/*
 Example code based on code from Nicholas Smith at http://imnes.blogspot.com/2011/01/how-to-use-yelp-v2-from-java-including.html
 For a more complete example (how to integrate with GSON, etc) see the blog post above.
 */

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * Example for accessing the Yelp API.
 */
public class YelpHandler extends ApiHandler {

	@Override
	public ApiResponse getFirstResponse(String title, ApiType apiType) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	public static class YelpApi2 extends DefaultApi10a {

		@Override
		public String getAccessTokenEndpoint() {
			return null;
		}

		@Override
		public String getAuthorizationUrl(Token arg0) {
			return null;
		}

		@Override
		public String getRequestTokenEndpoint() {
			return null;
		}

	}
	public static class Yelp {
		OAuthService	service;
		Token		    accessToken;

		/**
		 * Setup the Yelp API OAuth credentials.
		 * 
		 * OAuth credentials are available from the developer site, under Manage
		 * API access (version 2 API).
		 * 
		 * @param consumerKey
		 *           Consumer key
		 * @param consumerSecret
		 *           Consumer secret
		 * @param token
		 *           Token
		 * @param tokenSecret
		 *           Token secret
		 */
		public Yelp(String consumerKey, String consumerSecret, String token, String tokenSecret) {
			service = new ServiceBuilder().provider(YelpApi2.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
			accessToken = new Token(token, tokenSecret);
		}

		/**
		 * Search with term and location.
		 * 
		 * @param term
		 *           Search term
		 * @param latitude
		 *           Latitude
		 * @param longitude
		 *           Longitude
		 * @return JSON string response
		 */
		public String search(String term, double latitude, double longitude) {
			OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
			request.addQuerystringParameter("term", term);
			request.addQuerystringParameter("ll", latitude + "," + longitude);
			service.signRequest(accessToken, request);
			Response response = request.send();
			return response.getBody();
		}

		// CLI
		public void main(String[] args) {
			// Update tokens here from Yelp developers site, Manage API access.
			String consumerKey = "jWbRqLnH7D24KhWamKBXYw";
			String consumerSecret = "WRmRXxmTFfGoV5UYTg6s6VCYJA0";
			String token = "M6eQ7MMPocsNAeUQ0xHqINVzROk-mJ2M";
			String tokenSecret = "-nTGZEl5pAVezLG5TRch4RbsnqQ";

			Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
			String response = yelp.search("burritos", 30.361471, -87.164326);

			System.out.println(response);
		}
	}
}