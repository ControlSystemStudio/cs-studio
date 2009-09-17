package org.csstudio.opibuilder.properties.support;


import org.csstudio.opibuilder.visualparts.ActionsCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


/**The property descriptor for actions property.
 * @author Xihui Chen
 *
 */
public class ActionsPropertyDescriptor extends TextPropertyDescriptor {
	
	
	/**
	 * Creates an property descriptor with the given id and display name.
	 * 
	 * @param id
	 *            the id of the property
	 * @param displayName
	 *            the name to display for the property
	 */
	public ActionsPropertyDescriptor(final Object id, final String displayName) {
		super(id, displayName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new ActionsCellEditor(parent, "Set Actions");
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
}
