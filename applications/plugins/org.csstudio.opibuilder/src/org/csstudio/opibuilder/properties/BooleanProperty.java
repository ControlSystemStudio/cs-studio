package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.properties.support.BooleanPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**
 * A boolean widget property.
 * @author Alexander Will(class of same name in SDS)
 * @author Xihui Chen
 * 
 */
public final class BooleanProperty extends AbstractWidgetProperty {



	public BooleanProperty(String propId, String description,
			WidgetPropertyCategory category, boolean defaultValue) {
		super(propId, description, category, Boolean.valueOf(defaultValue));
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object checkValue(final Object value) {
		if(value == null)
			return null;

		Boolean acceptedValue;
		if (value instanceof Boolean) 
			acceptedValue = (Boolean) value;
		else
			acceptedValue = Boolean.parseBoolean(value.toString());

		return acceptedValue;
	}


	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		return new BooleanPropertyDescriptor(prop_id, description);
	}


	@Override
	public void writeToXML(Element propElement) {
		propElement.setText(getPropertyValue().toString());
	}
	
	@Override
	public Object readValueFromXML(Element propElement) {
		return Boolean.parseBoolean(propElement.getValue());
	}
	

}
