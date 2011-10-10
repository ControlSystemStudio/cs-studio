/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.jdom.Element;

/**Just used to display something in the property view, which cannot be edited.
 * @author Xihui Chen
 *
 */
public class UnchangableStringProperty extends StringProperty {
	
	
	/**String Property Constructor. The property value type is {@link String}. This
	 * String property is not editable in property sheet. It is used for information 
	 * display purpose only.
	 * @param prop_id the property id which should be unique in a widget model.
	 * @param description the description of the property,
	 * which will be shown as the property name in property sheet.
	 * @param category the category of the widget.
	 * @param defaultValue the default value when the widget is first created.
	 */
	public UnchangableStringProperty(String prop_id, String description,
			WidgetPropertyCategory category, String defaultValue) {
		super(prop_id, description, category, defaultValue);		
	}


	@Override
	protected PropertyDescriptor createPropertyDescriptor() {		
		return new TextPropertyDescriptor(prop_id, description){
			@Override
			public CellEditor createPropertyEditor(Composite parent) {
				return null;
			}
		};
	}
	
	@Override
	public boolean configurableByRule() {
		return false;
	}
	

}
