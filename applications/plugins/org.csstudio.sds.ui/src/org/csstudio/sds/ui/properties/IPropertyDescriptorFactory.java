package org.csstudio.sds.ui.properties;

import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.ui.internal.properties.view.IPropertyDescriptor;

/**
 * Defines an interface for contributing property desriptor factories.
 * 
 * @author Stefan Hofer & Sven Wende
 */
public interface IPropertyDescriptorFactory {
	
	/**
	 * @param id the id of the property.
	 * @param property the property
	 * @return A property descriptor.
	 */
	IPropertyDescriptor createPropertyDescriptor(Object id, WidgetProperty property);
}
