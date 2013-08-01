package com.owow.rich.apiHandler;

import java.util.LinkedList;
import java.util.List;

public class ApiTypeFactory {

	final static ApiType[]	prefrence	= new ApiType[]
	                                  {
	                                  ApiType.freebase, ApiType.dictionary, ApiType.wiki
	                                  };
	final static ApiType	  end	       = null;
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
