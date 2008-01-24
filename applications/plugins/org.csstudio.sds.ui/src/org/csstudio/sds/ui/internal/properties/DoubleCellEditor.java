package org.csstudio.sds.ui.internal.properties;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * A cell editor that manages a <code>java.lang.Double</code> entry field.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class DoubleCellEditor extends TextCellEditor {
	/**
	 * Standard constructor.
	 * 
	 * @param parent
	 *            The parent control.
	 */
	public DoubleCellEditor(final Composite parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doGetValue() {
		Object value = super.doGetValue();
		return Double.parseDouble(value.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetValue(final Object value) {
		if (value==null) {
			super.doSetValue(String.valueOf(new Double(0)));
		} else {
			super.doSetValue(String.valueOf(value.toString()));
		}
	}
}
