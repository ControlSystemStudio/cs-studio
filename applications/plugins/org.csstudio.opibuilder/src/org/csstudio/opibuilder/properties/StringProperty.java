package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.util.MacrosUtil;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.jdom.Element;

/**The widget property for string. It also accept macro string $(macro).
 * @author Xihui Chen
 *
 */
public class StringProperty extends AbstractWidgetProperty {
	
	

	public StringProperty(String prop_id, String description,
			WidgetPropertyCategory category, String defaultValue) {
		super(prop_id, description, category, defaultValue);
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
	
	@Override
	public Object getPropertyValue() {
		if(executionMode == ExecutionMode.RUN_MODE && widgetModel !=null)
			return MacrosUtil.replaceMacros(
					widgetModel, (String) super.getPropertyValue());
		else
			return super.getPropertyValue();
	}
	
	
	
	
	
	

}
