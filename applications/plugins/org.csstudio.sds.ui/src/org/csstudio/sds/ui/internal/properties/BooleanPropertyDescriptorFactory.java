/**
 * 
 */
package org.csstudio.sds.ui.internal.properties;

import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.ui.internal.properties.view.BooleanPropertyDescriptor;
import org.csstudio.sds.ui.internal.properties.view.IPropertyDescriptor;
import org.csstudio.sds.ui.internal.properties.view.PropertyDescriptor;
import org.csstudio.sds.ui.properties.IPropertyDescriptorFactory;

/**
 * A factory that creates a certain type of PropertyDescriptor.
 * 
 * @author Stefan Hofer & Sven Wende
 *
 */
public final class BooleanPropertyDescriptorFactory implements
		IPropertyDescriptorFactory {

	/**
	 * {@inheritDoc}
	 */
	public IPropertyDescriptor createPropertyDescriptor(final Object id, final WidgetProperty property) {
		//TODO: CellEditor für Booleans?
//		PropertyDescriptor descriptor = new TextPropertyDescriptor(id, property.getDescription(), property.getCategory().toString()){
//			@Override
//			public CellEditor createPropertyEditor(final Composite parent) {
//				CellEditor editor = new CheckboxCellEditor(parent);
//				if (getValidator() != null) {
//					editor.setValidator(getValidator());
//				}
//				return editor;
//			}
//			
//		};
		PropertyDescriptor descriptor = new BooleanPropertyDescriptor(id, property.getDescription());
		
		// validator
		descriptor.setValidator(new PropertyTypeCellEditorValidator(property));
		
		return descriptor;
	}


}
