package com.owow.rich.retriever;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.owow.rich.items.Feedback;
import com.owow.rich.items.Query;
import com.owow.rich.items.Result;
import com.owow.rich.items.Result.Results;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class LocalRetriver {

	public static Results retirve(Query query) {
		
//		ofy().save().entity(new Query("123123", "red")).now();
//		Car c = ofy().load().type(Query.class).id("123123").now();
//		ofy().delete().entity(c);
		
		// TODO: Maybe use Objectify:
//		Results results = (Results) PersistentCahce.get(query.getId(), "Results");
//		
//		if(results == null) {
//			results = new Results();
//		}
//		return results;
		
		return new Results();
   }
	
	public static void saveResults(Query query, Results results) {
//		PersistentCahce.set(query.getId(), results, "Results");
//		for (Result result : results.results) {
//			ofy().save().entity(result).now();
//      }
//		Car c = ofy().load().type(Query.class).id("123123").now();
//		ofy().delete().entity(c);

   }

	public static void learnFromFeedback(List<Feedback> feedbackList) {
   }
}
