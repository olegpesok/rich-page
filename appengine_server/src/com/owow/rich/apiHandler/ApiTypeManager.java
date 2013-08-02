package com.owow.rich.apiHandler;

import java.util.LinkedList;
import java.util.List;

public class ApiTypeManager {

	final static ApiType[]	prefrence	= new ApiType[]
	                                  {
	                                  ApiType.freebase, ApiType.dictionary, ApiType.wiki
	                                  };
	final static ApiType	  end	       = null;

	/**
	 * Gets ordered preference list of ApiTypes. In case an ApiType does not return a value or encounter an error, you should move and try the next on the list.
	 * @param start ApiType
	 * @return Ordered preference list of ApiTypes
	 */
	public static List<ApiType> getApiSequence(ApiType at)
	{
		LinkedList<ApiType> ret = new LinkedList<ApiType>();
		int i;
		for (i = 0; i < prefrence.length && prefrence[i] != at; i++)
		{}
		ret.add(at);
		i++;
		for (; i < prefrence.length && prefrence[i] != end; i++)
			ret.add(prefrence[i]);

		return ret;
	}
}
