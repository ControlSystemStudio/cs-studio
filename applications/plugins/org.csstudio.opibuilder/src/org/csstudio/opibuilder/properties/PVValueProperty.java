package org.csstudio.opibuilder.properties;

import org.csstudio.platform.data.IValue;
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

	/**The property is used to store pv values. The value type is {@link IValue}.
	 * @param prop_id the property ID.
	 * @param defaultValue the default value.
	 */
	public PVValueProperty(String prop_id, IValue defaultValue) {
		super(prop_id, "", null, defaultValue);
		setVisibleInPropSheet(false);
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
