package org.csstudio.sds.model.initializers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.eclipse.swt.graphics.RGB;

/**
 * A control system schema defines default properties that are used to
 * initialize widget models. Subclasses must provide a map with the default
 * values. These can be retrieved by typed getters or by a generic getter that
 * can be casted as necessary. The getters provide default values and never
 * return null (Null-Object-Pattern).
 * 
 * @author Stefan Hofer & Sven Wende
 * @version $Revision$
 * 
 */
public abstract class AbstractControlSystemSchema {

	/**
	 * The control system specific properties.
	 */
	private Map<String, Object> _properties;

	/**
	 * Constructor.
	 */
	public AbstractControlSystemSchema() {
		_properties = new HashMap<String, Object>();
		initializeProperties();
	}

	/**
	 * Called during the creation of the schema. Subclasses should init aliases
	 * here.
	 * 
	 * @param widgetModel
	 *            the w
	 */
	protected abstract void initializeAliases(AbstractWidgetModel widgetModel);

	/**
	 * Called for every widget that is initialized. Subclasses may apply
	 * settings which are valid in general for all widgets.
	 * 
	 * @param widgetModel
	 *            the widget that is beeing initialized
	 */
	protected abstract void initializeWidget(AbstractWidgetModel widgetModel);

	/**
	 * Called during the creation of the schema. Subclasses should init global
	 * properties here.
	 */
	protected abstract void initializeProperties();

	/**
	 * Adds a global property.
	 * 
	 * @param key
	 *            the key for the property
	 * @param value
	 *            the property value
	 */
	protected final void addGlobalProperty(final String key, final Object value) {
		_properties.put(key, value);
	}

	/**
	 * Returns the requested property.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The property value or <code>false</code> if there is no such
	 *         property.
	 */
	public final boolean getBooleanProperty(final String name) {
		return (Boolean) getProperty(name, Boolean.class, false);
	}

	/**
	 * @param name
	 *            Name of the requested property.
	 * @param clazz
	 *            The expexted type of the requested property.
	 * @param defaultValue
	 *            A default value if there is no such property.
	 * @return The requested property as object or the default value.
	 */
	@SuppressWarnings("unchecked")
	private Object getProperty(final String name, final Class clazz,
			final Object defaultValue) {
		Object value = _properties.get(name);
		if (value != null) {
			if (clazz.isAssignableFrom(value.getClass())) {
				return value;
			}
			CentralLogger
					.getInstance()
					.error(
							this,
							"Value of property " //$NON-NLS-1$
									+ name
									+ "is of type " + value.getClass() + " and cannot be casted to type " + clazz); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			CentralLogger.getInstance().error(this,
					"No value found for property " + name + //$NON-NLS-1$
							". Using default value instead."); //$NON-NLS-1$
		}
		return defaultValue;
	}

	/**
	 * Returns the requested property.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The property value or a default color if there is no such
	 *         property.
	 */
	public final RGB getColorProperty(final String name) {
		return (RGB) getProperty(name, RGB.class, new RGB(100, 100, 100));
	}

	/**
	 * Returns the requested property.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The property value or <code>0.0</code> if there is no such
	 *         property.
	 */
	public final double getDoubleProperty(final String name) {
		return (Double) getProperty(name, Double.class, 0.0);
	}

	/**
	 * Returns the requested property.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The property value or <code>0</code> if there is no such
	 *         property.
	 */
	public final int getIntegerProperty(final String name) {
		return (Integer) getProperty(name, Integer.class, 0);
	}

	/**
	 * Returns the requested property.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The property value or an empty object if there is no such
	 *         property.
	 */
	public final Object getObjectProperty(final String name) {
		return getProperty(name, Object.class, new Object());
	}

	/**
	 * @return The names of all properties.
	 */
	public final Set<String> getPropertyNames() {
		return _properties.keySet();
	}

	/**
	 * Returns the requested property.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The property value or an empty String if there is no such
	 *         property.
	 */
	public final String getStringProperty(final String name) {
		return (String) getProperty(name, String.class, ""); //$NON-NLS-1$
	}
}
