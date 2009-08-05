package org.csstudio.opibuilder.properties;

import org.csstudio.platform.data.IValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**
 * The property which contains a {@link IValue}. This property won't be shown in 
 * property view.
 * 
 * @author Xihui Chen
 *
 */
public class PVValueProperty extends AbstractWidgetProperty {

	public PVValueProperty(String prop_id, IValue defaultValue) {
		super(prop_id, "", null, false, defaultValue);
	}

	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		IValue acceptableValue = null;
		if(value instanceof IValue)
			acceptableValue = (IValue) value;		
		return acceptableValue;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		return null;
	}

	@Override
	public void writeToXML(Element propElement) {		
	}
	
	@Override
	public Object readValueFromXML(Element propElement) {
		return null;
	}
	

}
