package com.owow.rich.storage;

import com.google.appengine.api.datastore.PropertyContainer;

public interface PropertyContainerSupport {
	public PropertyContainer createPropertyContainer();
	public void loadPropertiesFromPropertyContainer(PropertyContainer container);
}
