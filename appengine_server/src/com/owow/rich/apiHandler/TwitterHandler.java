package com.owow.rich.apiHandler;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public class TwitterHandler extends ApiHandler {

	public static final String	lang	= "en";
	@Override
	public ApiResponse getFirstResponse(String title, ApiType apiType) throws Exception {
		String consumerKey = "8bbkBc3fNrrz3cXki5y1zg";
		String consumerSecret = "JknxvCSNfW8T1F2tUsBGzI4ifjuG1XPXwHLOTwqmLk";
		String token = "1663285404-rVQoPYBc7RPPq9Czz4geKuo67DHTIgoGNcEl9wS";
		String tokenSecret = "e5AOfLOztsWqeQ0qKLqlVAVSFwjmPFnL2bhOAI33mE";
		TwitterFactory tf = new TwitterFactory();
		Twitter twitter = tf.getInstance();
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
		twitter.setOAuthAccessToken(new AccessToken(token, tokenSecret));

		String celeb = "Mark Zuckerberg";

		String s = "";
		ResponseList<User> rlu = twitter.searchUsers(celeb, 1);

		User ourCeleb = rlu.get(0);
		s = createView(ourCeleb.getProfileImageURL(), ourCeleb.getDescription(), ourCeleb.getScreenName(), ourCeleb.getName(), ourCeleb.getStatus().getText());
		Query query = new Query("@" + ourCeleb.getScreenName() + " OR #" + ourCeleb.getScreenName() + "");
		query.setLang(lang);
		QueryResult result = twitter.search(query);
		for (Status status : result.getTweets()) {
			s += "@" + status.getUser().getScreenName() + ":" + status.getText() + "<br />";
		}
		ApiResponse ar = new ApiResponse(title, null, new ApiView(s), ApiType.twitter);
		return ar;
	}

	public String createView(String imageUrl, String description, String screenName, String name, String status)
	{
		return name + "<br />";
	}

	@Override
	public ApiView getView(ApiResponse fromGetData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
