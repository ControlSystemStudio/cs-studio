package org.csstudio.opibuilder.properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.jdom.Element;

/**Just used to display something in the property view, which cannot be edited.
 * @author Xihui Chen
 *
 */
public class UnchangableStringProperty extends AbstractWidgetProperty {
	
	

	public UnchangableStringProperty(String prop_id, String description,
			WidgetPropertyCategory category, boolean visibleInPropSheet,
			String defaultValue) {
		super(prop_id, description, category, visibleInPropSheet, defaultValue);		
	}

	@Override
	public Object checkValue(Object value) {
		return null;
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
	public void writeToXML(Element propElement) {
		propElement.setText(getPropertyValue().toString());
		
	}
	
	@Override
	public Object readValueFromXML(Element propElement) {
		return propElement.getValue();
	}

}
