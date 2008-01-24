package org.csstudio.sds.ui.internal.properties;

import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.ui.internal.properties.view.IPropertyDescriptor;
import org.csstudio.sds.ui.internal.properties.view.PropertyDescriptor;
import org.csstudio.sds.ui.properties.IPropertyDescriptorFactory;

/**
 * A factory that creates a certain type of PropertyDescriptor.
 * 
 * @author Stefan Hofer & Sven Wende
 *
 */
public final class PointlistPropertyDescriptorFactory implements
		IPropertyDescriptorFactory {

	/**
	 * {@inheritDoc}
	 */
	public IPropertyDescriptor createPropertyDescriptor(final Object id,
			final WidgetProperty property) {
		// einen sinnvolleren Descriptor zurückgeben! (swende)
		PropertyDescriptor descriptor = new PointlistPropertyDescriptor(id,
				property.getDescription(), property.getCategory().toString());
		
		// validator
		descriptor.setValidator(new PropertyTypeCellEditorValidator(property));
		return descriptor;
	}

}
