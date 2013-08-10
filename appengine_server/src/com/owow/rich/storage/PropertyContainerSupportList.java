package com.owow.rich.storage;

import java.util.LinkedList;

import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.PropertyContainer;

@SuppressWarnings("serial")
public class PropertyContainerSupportList extends LinkedList<PropertyContainerSupport> {
	private String	prefix;

	public PropertyContainerSupportList(String childPrefix) {
		prefix = childPrefix;
	}
	public EmbeddedEntity getEmbeddedEntity()
	{
		EmbeddedEntity embeddedEntity = new EmbeddedEntity();
		embeddedEntity.setProperty("length", size());
		int i = 0;
		for (PropertyContainerSupport element : this) {
			i++;
			EmbeddedEntity embeddedEntity2 = new EmbeddedEntity();
			embeddedEntity2.setPropertiesFrom(element.createPropertyContainer());
			embeddedEntity.setProperty(prefix + i, embeddedEntity2);
		}
		return embeddedEntity;
	}
	public PropertyContainer createPropertyContainer() {
		return getEmbeddedEntity();
	}
}
