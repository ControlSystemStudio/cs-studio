/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import java.util.List;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**The widget property for list. This property is only used for property change communication
 * between model and editpart, so it is not savable and viewable in property sheet. 
 * @author Xihui Chen
 *
 */
public class UnsavableListProperty extends AbstractWidgetProperty {
	
	/**String Property Constructor. The property value type is {@link List}.
	 * @param prop_id the property id which should be unique in a widget model.
	 * @param description the description of the property,
	 * which will be shown as the property name in property sheet.
	 * @param category the category of the widget.
	 * @param defaultValue the default value when the widget is first created. Can be NULL.
	 */
	public UnsavableListProperty(String prop_id, String description,
			WidgetPropertyCategory category, List<?> defaultValue) {
		super(prop_id, description, category, defaultValue);
	}
	
	@Override
	public Object checkValue(Object value) {
		if(value instanceof List)
			return value;
		return null;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		return null;
	}

	@Override
	public void writeToXML(Element propElement) { /* NOP */ }

	@Override
	public Object readValueFromXML(Element propElement) {
		return null;
	}
	
	@Override
	public boolean isSavable() {
		return false;
	}
	

}
