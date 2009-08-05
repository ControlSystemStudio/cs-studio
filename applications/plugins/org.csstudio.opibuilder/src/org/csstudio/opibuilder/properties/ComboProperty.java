package org.csstudio.opibuilder.properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**
 * @author Xihui Chen
 *
 */
public class ComboProperty extends AbstractWidgetProperty {
	
	private String[] labelsArray;

	public ComboProperty(String prop_id, String description,
			WidgetPropertyCategory category, boolean visibleInPropSheet,
			String[] labelsArray, int defaultValue) {		
		super(prop_id, description, category, visibleInPropSheet, Integer.valueOf(defaultValue));
		this.labelsArray = labelsArray;
		
	}

	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		Integer acceptedValue = null;

		// check type
		if (!(value instanceof Integer)) {
			try {
				acceptedValue = Integer.parseInt(value.toString());
			} catch (NumberFormatException e) {
				acceptedValue = null;
			}
		} else {
			acceptedValue = (Integer) value;
		}

		// check range
		if (acceptedValue != null) {
			if (acceptedValue < 0) {
				acceptedValue = 0;
			} else if (acceptedValue >= labelsArray.length) {
				acceptedValue = labelsArray.length-1;
			}
		}

		return acceptedValue;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		return new ComboBoxPropertyDescriptor(
				prop_id, description, labelsArray);
	}

	@Override
	public void writeToXML(Element propElement) {
		propElement.setText(getPropertyValue().toString());
	}
	
	@Override
	public Object readValueFromXML(Element propElement) {
		return Integer.parseInt(propElement.getValue());
	}

}
