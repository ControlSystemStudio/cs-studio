
package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.visualparts.FontPropertyDescriptor;
import org.csstudio.opibuilder.visualparts.RGBColorPropertyDescriptor;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author Xihui Chen
 *
 */
public class FontProperty extends AbstractWidgetProperty {

	public FontProperty(String prop_id, String description,
			WidgetPropertyCategory category, boolean visibleInPropSheet,
			FontData defaultValue) {
		super(prop_id, description, category, visibleInPropSheet, defaultValue);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.properties.AbstractWidgetProperty#checkValue(java.lang.Object)
	 */
	@Override
	public Object checkValue(Object value) {
		Assert.isNotNull(value);
		
		Object acceptedValue = value;

		if (!(value instanceof FontData)) {
			acceptedValue = null;
		}
		
		return acceptedValue;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.properties.AbstractWidgetProperty#createPropertyDescriptor()
	 */
	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		return new FontPropertyDescriptor(prop_id, description);		
	}

}
