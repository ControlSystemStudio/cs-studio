/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.opibuilder.visualparts;


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
