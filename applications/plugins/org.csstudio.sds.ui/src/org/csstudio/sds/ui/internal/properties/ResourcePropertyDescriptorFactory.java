package org.csstudio.sds.ui.internal.properties;

import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.properties.ResourceProperty;
import org.csstudio.sds.ui.internal.properties.view.IPropertyDescriptor;
import org.csstudio.sds.ui.internal.properties.view.PropertyDescriptor;
import org.csstudio.sds.ui.properties.IPropertyDescriptorFactory;

/**
 * A factory that creates a certain type of PropertyDescriptor.
 * 
 * @author Stefan Hofer & Sven Wende & Kai Meyer
 *
 */
public final class ResourcePropertyDescriptorFactory implements
		IPropertyDescriptorFactory {

	/**
	 * {@inheritDoc}
	 */
	public IPropertyDescriptor createPropertyDescriptor(final Object id,
			final WidgetProperty property) {
		PropertyDescriptor descriptor = new ResourcePropertyDescriptor(id,
				property.getDescription(), property.getCategory().toString(), ((ResourceProperty)property).getFileExtensions());
		
		// validator
		descriptor.setValidator(new PropertyTypeCellEditorValidator(property));
		return descriptor;
	}

}
