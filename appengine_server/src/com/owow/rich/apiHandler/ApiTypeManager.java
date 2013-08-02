package com.owow.rich.apiHandler;

import java.util.List;

import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;

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
	public static List<ApiType> getApiSequence(ApiType apiType)
	{
		List<ApiType> apiTypeList = Lists.newLinkedList();
		int i;
		for (i = 0; i < prefrence.length && prefrence[i] != apiType; i++)
		{}
		apiTypeList.add(apiType);
		i++;
		for (; i < prefrence.length && prefrence[i] != end; i++)
			apiTypeList.add(prefrence[i]);

		return apiTypeList;
	}
}
