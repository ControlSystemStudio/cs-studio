package org.csstudio.sds.ui.internal.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * An abstract cell editor, which values should be edited by a custom Dialog.
 *  
 * @author Kai Meyer
 */
public abstract class AbstractDialogCellEditor extends CellEditor {
	
	/**
	 * A shell.
	 */
	private Shell _shell;
	
	/**
	 * The title for this CellEditor.
	 */
	private final String _title;
	/**
	 * A boolean representing the open state of the dialog.
	 */
	private boolean _dialogIsOpen = false;
	
	/**
	 * Creates a new string cell editor parented under the given control. The
	 * cell editor value is a Map of Strings.
	 * 
	 * @param parent
	 *            The parent table.
	 * @param title
	 * 			  The title for this CellEditor
	 */
	public AbstractDialogCellEditor(final Composite parent, final String title) {
		super(parent, SWT.NONE);
		_shell = parent.getShell();
		_title = title;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void activate() {
		if (!_dialogIsOpen) {
			_dialogIsOpen = true;
			this.openDialog(_shell, _title);
			if (this.shouldFireChanges()) {
				fireApplyEditorValue();
			}
			_dialogIsOpen = false;
		}
	}
	
	/**
	 * Creates and opens the Dialog.
	 * @param parentShell The parent shell for the dialog
	 * @param dialogTitle The title for the dialog
	 */
	protected abstract void openDialog(final Shell parentShell, final String dialogTitle);
	
	/**
	 * Returns, if CellEditor.fireApplyEditorValue() should be called. 
	 * @return true if CellEditor.fireApplyEditorValue() should be called, false otherwise
	 */
	protected abstract boolean shouldFireChanges(); 

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final Control createControl(final Composite parent) {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetFocus() {
		// Ignore
	}

}
