package com.owow.rich.retriever;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import com.owow.rich.items.Feedback;
import com.owow.rich.items.Query;
import com.owow.rich.items.ResultSet;

public class LocalRetriver {

	public static ResultSet retirve(Query query) {
		ResultSet results = ofy().load().type(ResultSet.class).id(query.getId()).now();
		if (results == null || results.results == null) {
			results =  new ResultSet(query);
		}
		return results;
   }
	
	public static void saveResults(ResultSet results) {
		ofy().save().entity(results).now();
   }

	//TODO: not implemented yet:
	public static void learnFromFeedback(List<Feedback> feedbackList) {
   }
}
