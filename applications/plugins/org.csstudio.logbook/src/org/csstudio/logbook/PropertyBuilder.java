/**
 * 
 */
package org.csstudio.logbook;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 
 * A builder for a default implementation of the Property interface.
 * 
 * @author shroffk
 * 
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
		for (Entry<String, String> entry : property.getAttributes()) {
			propertyBuilder.attributes.put(entry.getKey(), entry.getValue());
		}
		return propertyBuilder;
	}

	public PropertyBuilder attribute(String attribute) {
		this.attributes.put(attribute, "");
		return this;
	}

	public PropertyBuilder attribute(String attribute, String attributeValue) {
		this.attributes.put(attribute, attributeValue);
		return this;
	}

	public Property build() {
		return new PropertyImpl(name, attributes);
	}

	/**
	 * A Default implementation of the Property Interface
	 * @author shroffk
	 *
	 */
	private class PropertyImpl implements Property {

		private final String name;
		private final Map<String, String> attributes;

		public PropertyImpl(String name, Map<String, String> attributes) {
			this.name = name;
			if (attributes != null)
				this.attributes = Collections.unmodifiableMap(attributes);
			else
				this.attributes = new HashMap<String, String>();
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Collection<String> getAttributeNames() {
			return attributes.keySet();
		}

		@Override
		public Collection<String> getAttributeValues() {
			return attributes.values();
		}

		@Override
		public String getAttributeValue(String attributeName) {
			return attributes.get(attributeName);
		}

		@Override
		public Set<Entry<String, String>> getAttributes() {
			return attributes.entrySet();
		}

	}
}
