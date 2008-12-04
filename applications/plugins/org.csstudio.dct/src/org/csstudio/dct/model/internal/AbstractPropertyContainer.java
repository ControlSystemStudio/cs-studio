package org.csstudio.dct.model.internal;

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

	private Map<String, Object> properties = new HashMap<String, Object>();

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            the record name
	 * 
	 */
	public AbstractPropertyContainer(String name) {
		super(name);
	}

	public AbstractPropertyContainer(String name, UUID id) {
		super(name, id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addProperty(String key, Object value) {
		properties.put(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getProperty(String key) {
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
	public Map<String, Object> getProperties() {
		return properties;
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
