package com.owow.rich.retriever;

import java.util.List;

import com.owow.rich.items.Feedback;
import com.owow.rich.items.Query;
import com.owow.rich.items.Result;
import com.owow.rich.items.Result.Results;
import com.owow.rich.storage.PersistentCahce;

public class LocalRetriver {

	public static Results retirve(Query query) {
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
   }

	public static void learnFromFeedback(List<Feedback> feedbackList) {
   }
}
