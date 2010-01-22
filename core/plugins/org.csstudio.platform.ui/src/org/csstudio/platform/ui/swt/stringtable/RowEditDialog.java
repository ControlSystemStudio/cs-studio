package org.csstudio.platform.ui.swt.stringtable;

import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**The RowEditDialog is the abstract superclass of dialogs
 * that are used to edit a row of items in a table  
 * @author Xihui Chen
 */
public abstract class RowEditDialog extends Dialog {

	protected String[] rowData;
	
	/** Initialize Dialog */
	protected RowEditDialog(Shell parentShell) {
    	super(parentShell);
        // Allow resize
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    /**Set the rowData which will be initially displayed in the Edit Dialog.
	 * It must be called prior to open(). 
	 * @param rowData the rowData to set
	 */
	public void setRowData(String[] rowData) {
		this.rowData = rowData;
	}

	/**
	 * @return the rowData
	 */
	public String[] getRowData() {
		return rowData;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.RowEditDialog_ShellTitle);
	}
}
