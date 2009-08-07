package org.csstudio.opibuilder.properties.support;

import org.csstudio.opibuilder.visualparts.DoubleCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Descriptor for a property that has a value which should be edited with a
 * double cell editor.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class DoublePropertyDescriptor extends TextPropertyDescriptor {

	/**
	 * Standard constructor.
	 * 
	 * @param id
	 *            the id of the property
	 * @param displayName
	 *            the name to display for the property
	 * @param category
	 *            the category
	 */
	public DoublePropertyDescriptor(final Object id, final String displayName) {
		super(id, displayName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new DoubleCellEditor(parent);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
}
