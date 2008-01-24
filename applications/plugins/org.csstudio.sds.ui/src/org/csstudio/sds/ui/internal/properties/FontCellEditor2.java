package org.csstudio.sds.ui.internal.properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * A table cell editor for values of type Font.
 * <p>
 * There is already a FontCellEditor, but when activated, it adds another step
 * where it only displays a small color patch, the Font indices and then a button
 * to start the dialog.
 * <p>
 * That's a waste of real estate, adds another 'click' to the editing of fonts,
 * plus the overall layout was really poor on Mac OS X, where the button didn't
 * fully show.
 * <p>
 * This implementation, based on the CheckboxCellEditor sources, jumps right
 * into the font dialog.
 *  
 * @author Kay Kasemir, Kai Meyer
 */
public final class FontCellEditor2 extends CellEditor {
	/**
	 * A shell.
	 */
	private Shell _shell;

	/**
	 * The current RGB value.
	 */
	private FontData _value;

	/**
	 * Creates a new font cell editor parented under the given control. The
	 * cell editor value is an SWT Font value.
	 * 
	 * @param parent
	 *            The parent table.
	 */
	public FontCellEditor2(final Composite parent) {
		super(parent, SWT.NONE);
		_shell = parent.getShell();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activate() {
		FontDialog dialog = new FontDialog(_shell);
		if (_value != null) {
			dialog.setFontList(new FontData[] {_value});
		}
		_value = dialog.open();
		if (_value != null) {
			fireApplyEditorValue();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createControl(final Composite parent) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doGetValue() {
		return _value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetFocus() {
		// Ignore
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetValue(final Object value) {
		Assert.isTrue(value instanceof FontData);
		this._value = (FontData) value;
	}
}
