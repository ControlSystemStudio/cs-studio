package org.csstudio.opibuilder.properties.support;


import org.csstudio.opibuilder.visualparts.ScriptsInputCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


public class ScriptPropertyDescriptor extends TextPropertyDescriptor {
	
	
	/**
	 * Creates an property descriptor with the given id and display name.
	 * 
	 * @param id
	 *            the id of the property
	 * @param displayName
	 *            the name to display for the property
	 */
	public ScriptPropertyDescriptor(final Object id, final String displayName) {
		super(id, displayName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new ScriptsInputCellEditor(parent, "Attach Scripts");
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
}
