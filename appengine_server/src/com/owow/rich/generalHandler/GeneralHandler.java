package com.owow.rich.generalHandler;

public abstract class GeneralHandler {

	/**
	 * Retruns the best response from the general search, (best just according to
	 * api specific internal scoring, and not the context scoring).
	 */
	public abstract Results getResults(String highlight, GeneralType type) throws Exception;

	/**
	 * Get one or more response from the api matching the highlight, e.g Freebase
	 * can return more then one entity for a given query.
	 * 
	 * Default implementation just returns one result. can be overriden by
	 * classes that return more then one response.
	 * 
	 * public List<Results> getAllApiResponses(String highlight, GeneralType
	 * type) throws Exception { return
	 * Lists.newArrayList(getFirstResponse(highlight, type)); }
	 */
}
