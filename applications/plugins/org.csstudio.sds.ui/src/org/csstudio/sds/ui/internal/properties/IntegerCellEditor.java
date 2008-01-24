package org.csstudio.sds.ui.internal.properties;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * A cell editor that manages a <code>java.lang.Integer</code> entry field.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class IntegerCellEditor extends TextCellEditor {
	/**
	 * Standard constructor.
	 * 
	 * @param parent
	 *            The parent control.
	 */
	public IntegerCellEditor(final Composite parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doGetValue() {
		Object value = super.doGetValue();

		Integer result = 0;

		try {
			result = Integer.parseInt(value.toString());
		} catch (NumberFormatException nfe) {
			result = 0;
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetValue(final Object value) {
		if (value==null) {
			super.doSetValue(String.valueOf(new Integer(0)));
		} else {
			super.doSetValue(String.valueOf(value.toString()));
		}
	}
}
