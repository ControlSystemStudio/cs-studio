package org.csstudio.opibuilder.properties.support;

import org.csstudio.opibuilder.visualparts.StringListCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Descriptor for a property that has a value which should be edited with a StringList
 * cell editor.
 * 
 * @author Xihui Chen
 * 
 */
public final class StringListPropertyDescriptor extends TextPropertyDescriptor {
	/**
	 * Standard constructor.
	 * 
	 * @param id
	 *            the id of the property
	 * @param displayName
	 *            the name to display for the property
	 */
	public StringListPropertyDescriptor(final Object id, final String displayName) {
		super(id, displayName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new StringListCellEditor(parent, "Edit String List");
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
}
