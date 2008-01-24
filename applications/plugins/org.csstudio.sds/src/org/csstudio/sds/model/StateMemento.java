package org.csstudio.sds.model;

import java.util.Map;

public class StateMemento {

	private Map<String, Object> _propertyValues;

	public StateMemento(Map<String, Object> propertyValues) {
		assert propertyValues != null;
		_propertyValues = propertyValues;
	}
	
	public Map<String, Object> getPropertyValues() {
		return _propertyValues;
	}
	
}
