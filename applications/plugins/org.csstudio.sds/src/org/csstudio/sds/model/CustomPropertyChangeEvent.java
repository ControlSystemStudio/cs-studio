package org.csstudio.sds.model;

import java.beans.PropertyChangeEvent;

public class CustomPropertyChangeEvent extends PropertyChangeEvent {
	
	private Object _customization;

	public CustomPropertyChangeEvent(Object source, String propertyName, Object oldValue,
			Object newValue, Object customization) {
		super(source, propertyName, oldValue, newValue);
		_customization = customization;
	}
	
	public Object getCustomization() {
		return _customization;
	}

}
