package org.csstudio.apputil.ui.swt.stringtable;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**The RowEditDialog is the abstract superclass of dialogs
 * that are used to edit a row of items in a table  
 * @author Xihui Chen
 */
public abstract class RowEditDialog extends Dialog {

	protected String[] rowData;
	
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
	
	protected RowEditDialog(Shell parentShell) {
		super(parentShell);		
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Edit Row Data");		
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "OK", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", true);
	}
	
}
