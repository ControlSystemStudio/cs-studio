package org.csstudio.dct.model.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.csstudio.dct.model.IPropertyContainer;
import org.csstudio.dct.model.IRecord;

/**
 * Standard implementation of {@link IRecord}.
 * 
 * @author Sven Wende
 */
public abstract class AbstractPropertyContainer extends AbstractElement implements IPropertyContainer {

	private Map<String, String> properties;

	public AbstractPropertyContainer(String name, UUID id) {
		super(name, id);
		properties = new HashMap<String, String>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addProperty(String key, String value) {
		properties.put(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProperty(String key) {
		return properties.get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeProperty(String key) {
		properties.remove(key);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasProperty(String key) {
		return properties.containsKey(key);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

//	/**
//	 *{@inheritDoc}
//	 */
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = super.hashCode();
//		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
//		return result;
//	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj instanceof AbstractPropertyContainer) {
			AbstractPropertyContainer container = (AbstractPropertyContainer) obj;

			// .. super
			if (super.equals(obj)) {
				// .. properties
				if (getProperties().equals(container.getProperties())) {
					result = true;
				}
			}
		}

		return result;
	}
}
