package org.csstudio.sds.ui.internal.properties;

import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.properties.OptionProperty;
import org.csstudio.sds.ui.internal.properties.view.ComboBoxPropertyDescriptor;
import org.csstudio.sds.ui.internal.properties.view.IPropertyDescriptor;
import org.csstudio.sds.ui.internal.properties.view.PropertyDescriptor;
import org.csstudio.sds.ui.properties.IPropertyDescriptorFactory;


/**
 * Descriptor factory for option properties (see {@link OptionProperty}).
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class OptionPropertyDescriptorFactory implements
		IPropertyDescriptorFactory {

	/**
	 * {@inheritDoc}
	 */
	public IPropertyDescriptor createPropertyDescriptor(final Object id,
			final WidgetProperty property) {
		final OptionProperty optionProperty = (OptionProperty) property;
		PropertyDescriptor descriptor = new ComboBoxPropertyDescriptor(id,
				property.getDescription(), property.getCategory().toString(),
				optionProperty.getOptions());

		// validator
		descriptor.setValidator(new PropertyTypeCellEditorValidator(property));
		return descriptor;
	}

}
