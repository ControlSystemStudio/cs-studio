/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.properties.support.PropertySSHelper;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**
 * A boolean widget property.
 * @author Alexander Will(class of same name in SDS)
 * @author Xihui Chen
 * 
 */
public final class BooleanProperty extends AbstractWidgetProperty {


	/**Boolean Property Constructor
	 * @param prop_id the property id which should be unique in a widget model.
	 * @param description the description of the property,
	 * which will be shown as the property name in property sheet.
	 * @param category the category of the widget.
	 * @param defaultValue the default value when the widget is first created.
	 */
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
		if(PropertySSHelper.getIMPL() == null)
			return null;
		return PropertySSHelper.getIMPL().getBooleanPropertyDescriptor(prop_id, description);
	}


	@Override
	public void writeToXML(Element propElement) {
		propElement.setText(getPropertyValue().toString());
	}
	
	@Override
	public Object readValueFromXML(Element propElement) {
		return Boolean.parseBoolean(propElement.getValue());
	}
	
	@Override
	public boolean configurableByRule() {
		return true;
	}
	
	@Override
	public String toStringInRuleScript(Object propValue) {
		return (Boolean)propValue? "true" : "false";
	}
	
	

}
