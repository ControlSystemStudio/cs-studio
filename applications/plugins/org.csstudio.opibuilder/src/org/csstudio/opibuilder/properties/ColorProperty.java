
package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.visualparts.RGBColorPropertyDescriptor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author Xihui Chen
 *
 */
public class ColorProperty extends AbstractWidgetProperty {

	public ColorProperty(String prop_id, String description,
			WidgetPropertyCategory category, boolean visibleInPropSheet,
			Object defaultValue) {
		super(prop_id, description, category, visibleInPropSheet, defaultValue);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.properties.AbstractWidgetProperty#checkValue(java.lang.Object)
	 */
	@Override
	public Object checkValue(Object value) {
		assert value != null : "value is null"; //$NON-NLS-1$
		
		Object acceptedValue = value;

		if (!(value instanceof RGB)) {
			acceptedValue = null;
		}
		
		return acceptedValue;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.properties.AbstractWidgetProperty#createPropertyDescriptor()
	 */
	@Override
	protected IPropertyDescriptor createPropertyDescriptor() {
		PropertyDescriptor descriptor = new RGBColorPropertyDescriptor(prop_id, description);
		descriptor.setCategory(category.toString());	
		return descriptor;
	}

}
