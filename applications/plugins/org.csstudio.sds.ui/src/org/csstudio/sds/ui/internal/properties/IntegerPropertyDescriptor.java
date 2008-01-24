package org.csstudio.sds.ui.internal.properties;

import org.csstudio.sds.ui.internal.properties.view.TextPropertyDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * Descriptor for a property that has a value which should be edited with a
 * integer cell editor.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class IntegerPropertyDescriptor extends TextPropertyDescriptor {
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
	public IntegerPropertyDescriptor(final Object id, final String displayName,
			final String category) {
		super(id, displayName, category);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new IntegerCellEditor(parent);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
}
