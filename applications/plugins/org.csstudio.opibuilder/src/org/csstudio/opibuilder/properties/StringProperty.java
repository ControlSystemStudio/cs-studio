package org.csstudio.opibuilder.properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.jdom.Element;

public class StringProperty extends AbstractWidgetProperty {
	
	

	public StringProperty(String prop_id, String description,
			WidgetPropertyCategory category, boolean visibleInPropSheet,
			String defaultValue) {
		super(prop_id, description, category, visibleInPropSheet, defaultValue);		
	}

	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		
		String acceptedValue = null;

		if (value instanceof String) 
			acceptedValue = (String) value;
		else
			acceptedValue = value.toString();
		
		
		return acceptedValue;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		return new TextPropertyDescriptor(prop_id, description);
	}

	@Override
	public void writeToXML(Element propElement) {
		propElement.setText(getPropertyValue().toString());
	}

	@Override
	public Object readValueFromXML(Element propElement) {
		return propElement.getValue();
	}
}
