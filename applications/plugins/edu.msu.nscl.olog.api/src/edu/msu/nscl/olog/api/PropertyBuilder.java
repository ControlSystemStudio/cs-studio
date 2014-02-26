/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.msu.nscl.olog.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author berryman
 */
public class PropertyBuilder {

	// required
	private String name;
	private Map<String, String> attributes = new HashMap<String, String>();;

	/**
	 * @param name
	 * 
	 */
	public static PropertyBuilder property(String name) {
		PropertyBuilder propertyBuilder = new PropertyBuilder();
		propertyBuilder.name = name;
		return propertyBuilder;
	}

	public static PropertyBuilder property(Property property) {
		PropertyBuilder propertyBuilder = new PropertyBuilder();
		propertyBuilder.name = property.getName();
		for (Entry<String, String> entry : property.getEntrySet()) {
			propertyBuilder.attributes.put(entry.getKey(), entry.getValue());
		}
		return propertyBuilder;
	}

	public PropertyBuilder attribute(String attribute){
		this.attributes.put(attribute, null);
		return this;
	}
	
	public PropertyBuilder attribute(String attribute, String attributeValue) {
		this.attributes.put(attribute, attributeValue);
		return this;
	}

	XmlProperty toXml() {
		return new XmlProperty(name, attributes);
	}

	Property build() {
		return new Property(this.toXml());
	}

}
