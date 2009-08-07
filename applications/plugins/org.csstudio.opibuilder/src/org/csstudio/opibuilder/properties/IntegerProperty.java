package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.properties.support.IntegerPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

public class IntegerProperty extends AbstractWidgetProperty {

	/**
	 * Lower border for the property value.
	 */
	private int min;

	/**
	 * Upper border for the property value.
	 */
	private int max;
	
	public IntegerProperty(final String prop_id, final String description,
			final WidgetPropertyCategory category, final boolean visibleInPropSheet,
			final int defaultValue) {
		super(prop_id, description, category, visibleInPropSheet, Integer.valueOf(defaultValue));
		min = Integer.MIN_VALUE;
		max = Integer.MAX_VALUE;
	}
	
	public IntegerProperty(final String prop_id, final String description,
			final WidgetPropertyCategory category, final boolean visibleInPropSheet,
			final int defaultValue, final int minValue, final int maxValue) {
		super(prop_id, description, category, visibleInPropSheet, Integer.valueOf(defaultValue));
		assert minValue < maxValue;
		min = minValue;
		max = maxValue;
	}
	

	@Override
	public Object checkValue(final Object value) {
		if(value == null)
			return null;

		Integer acceptedValue = null;

		// check type
		if (!(value instanceof Integer)) {
			if (value instanceof Number) {
				acceptedValue = ((Number) value).intValue();
			} else {
				try {
					acceptedValue = Integer.parseInt(value.toString());
				} catch (NumberFormatException e) {
					acceptedValue = null;
				}
			}
		} else {
			acceptedValue = (Integer) value;
		}

		// check borders
		if (acceptedValue != null) {
			if (acceptedValue > max) {
				acceptedValue = max;
			} else if (acceptedValue < min) {
				acceptedValue = min;
			}
		}

		return acceptedValue;
	}	

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		return new IntegerPropertyDescriptor(prop_id, description);
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
