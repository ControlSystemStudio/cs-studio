package org.csstudio.opibuilder.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

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
		super(prop_id, description, category, visibleInPropSheet, defaultValue);
		min = Integer.MIN_VALUE;
		max = Integer.MAX_VALUE;
	}
	
	public IntegerProperty(final String prop_id, final String description,
			final WidgetPropertyCategory category, final boolean visibleInPropSheet,
			final int defaultValue, final int minValue, final int maxValue) {
		super(prop_id, description, category, visibleInPropSheet, defaultValue);
		assert minValue < maxValue;
		min = minValue;
		max = maxValue;
	}
	

	@Override
	public Object checkValue(final Object value) {
		assert value != null : "value is null"; //$NON-NLS-1$

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
	public String getPropertyValueInString() {
		return propertyValue.toString();
	}

	@Override
	protected IPropertyDescriptor createPropertyDescriptor() {
		PropertyDescriptor descriptor = new TextPropertyDescriptor(prop_id, description);
		descriptor.setCategory(category.toString());	
		return descriptor;
	}

	



}
