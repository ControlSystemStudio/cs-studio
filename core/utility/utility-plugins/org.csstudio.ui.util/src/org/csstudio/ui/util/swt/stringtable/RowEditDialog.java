/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.util.swt.stringtable;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

/**The RowEditDialog is the abstract superclass of dialogs
 * that are used to edit a row of items in a table
 * @author Xihui Chen
 */
public abstract class RowEditDialog extends Dialog {

	protected String[] rowData;

	/** Initialize Dialog */
	protected RowEditDialog(final Shell parentShell) {
    	super(parentShell);
    }

    @Override
    protected void configureShell(final Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.RowEditDialog_ShellTitle);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    /**Set the rowData which will be initially displayed in the Edit Dialog.
	 * It must be called prior to open().
	 * @param rowData the rowData to set
	 */
	public void setRowData(final String[] rowData) {
		this.rowData = rowData;
	}

	/**
	 * @return the rowData
	 */
	public String[] getRowData() {
		return rowData;
	}
}
