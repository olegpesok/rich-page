package com.owow.rich.test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public class TwitterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	Twitter	twitter;
	@Before
	public void setUp() throws Exception {

		String consumerKey = "8bbkBc3fNrrz3cXki5y1zg";
		String consumerSecret = "JknxvCSNfW8T1F2tUsBGzI4ifjuG1XPXwHLOTwqmLk";
		String token = "1663285404-rVQoPYBc7RPPq9Czz4geKuo67DHTIgoGNcEl9wS";
		String tokenSecret = "e5AOfLOztsWqeQ0qKLqlVAVSFwjmPFnL2bhOAI33mE";
		TwitterFactory tf = new TwitterFactory();
		twitter = tf.getInstance();
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
		twitter.setOAuthAccessToken(new AccessToken(token, tokenSecret));

	}
	@Test
	public void test() throws TwitterException {
		// All tweets that contains Mark Zuckerbarg with positive attitude
		Query query = new Query("Mark Zuckerberg :)");
		query.lang("en");
		QueryResult result = twitter.search(query);
		String s = "";
		for (Status status : result.getTweets()) {
			s += "@" + status.getUser().getScreenName() + ":" + status.getText() + "\n";
		}
		System.out.print(s); // Printing all the results
	}

	@Test
	public void test2() throws TwitterException {
		String s = "";
		// Searches for user Mark Zuckerberg, Page 1
		ResponseList<User> rlu = twitter.searchUsers("Mark Zuckerberg", 1);
		for (User user : rlu) {
			s += user.getScreenName() + "\n";
		}
	}

	@Test
	public void test3() throws TwitterException {
		String celeb = "Mark Zuckerberg";

		String s = "";
		ResponseList<User> rlu = twitter.searchUsers(celeb, 1);

		User ourCeleb = rlu.get(0);
		// Getting celeb screenName, get all posts that contain @celebScreenName
		// or have #celebScreenName
		Query query = new Query("@" + ourCeleb.getScreenName() + " OR #" + ourCeleb.getScreenName() + "");
		query.setLang("en");
		QueryResult result = twitter.search(query);
		for (Status status : result.getTweets()) {
			s += "@" + status.getUser().getScreenName() + ":" + status.getText() + "\n";
		}

		System.out.print(s);
	}

}
