package org.csstudio.sds.ui.internal.properties.view;

import org.eclipse.jface.viewers.CellEditor;

/**
 * A listener which is notified when a cell editor is activated/deactivated.
 * 
 * @author Sven Wende
 */
interface ICellEditorActivationListener {
	/**
	 * Notifies that the cell editor has been activated.
	 * 
	 * @param cellEditor
	 *            the cell editor which has been activated
	 */
	void cellEditorActivated(CellEditor cellEditor);

	/**
	 * Notifies that the cell editor has been deactivated.
	 * 
	 * @param cellEditor
	 *            the cell editor which has been deactivated
	 */
	void cellEditorDeactivated(CellEditor cellEditor);
}
