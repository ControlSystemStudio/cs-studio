package org.csstudio.opibuilder.properties;

import java.util.LinkedHashMap;
import java.util.List;

import org.csstudio.opibuilder.properties.support.ScriptsInput;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

public class ScriptProperty extends AbstractWidgetProperty {

	public ScriptProperty(String prop_id, String description,
			WidgetPropertyCategory category, boolean visibleInPropSheet) {
		super(prop_id, description, category, visibleInPropSheet, 
				new ScriptsInput());
		
	}

	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		ScriptsInput acceptableValue = null;
		if(value instanceof ScriptsInput){
			acceptableValue = (ScriptsInput)value;			
		}
		
		return acceptableValue;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		return null;
	}

	@Override
	public Object readValueFromXML(Element propElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeToXML(Element propElement) {
		// TODO Auto-generated method stub

	}

}
