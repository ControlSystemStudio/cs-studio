package org.csstudio.opibuilder.visualparts;


import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * A table cell editor for values of type RGB.
 * <p>
 * There is already a ColorCellEditor, but when activated, it adds another step
 * where it only displays a small color patch, the RGB indices and then a button
 * to start the dialog.
 * <p>
 * That's a waste of real estate, adds another 'click' to the editing of colors,
 * plus the overall layout was really poor on Mac OS X, where the button didn't
 * fully show.
 * <p>
 * This implementation, based on the CheckboxCellEditor sources, jumps right
 * into the color dialog.
 * 
 * TODO: In den CSS Core ziehen - dann kann Kay die Klasse wiederverwenden (swende)
 *  
 * @author Kay Kasemir
 */
public final class RGBCellEditor extends CellEditor {
	/**
	 * A shell.
	 */
	private Shell _shell;

	/**
	 * The current RGB value.
	 */
	private RGB _value;

	/**
	 * Creates a new color cell editor parented under the given control. The
	 * cell editor value is an SWT RGB value.
	 * 
	 * @param parent
	 *            The parent table.
	 */
	public RGBCellEditor(final Composite parent) {
		super(parent, SWT.NONE);
		_shell = parent.getShell();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activate() {
		ColorDialog dialog = new ColorDialog(_shell);
		if (_value != null) {
			dialog.setRGB(_value);
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
		//Assert.isTrue(value instanceof RGB);
		if (value == null || !(value instanceof RGB)) {
			_value = new RGB(0,0,0);
		} else {
			_value = (RGB) value;
		}
	}
}
