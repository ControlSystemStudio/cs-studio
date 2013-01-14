/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.menu.pvscript;

import org.csstudio.ui.util.swt.stringtable.RowEditDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Preference edit dialog for a script and its description
 *  @author Kay Kasemir
 */
public class ScriptInfoEditor extends RowEditDialog
{
    private Text description, script;

    /** Initialize
     *  @param shell Parent shell
     */
	public ScriptInfoEditor(final Shell shell)
    {
		super(shell);
    }
	
    /** {@inheritDoc} */
    @Override
    protected void configureShell(final Shell newShell)
    {
        super.configureShell(newShell);
        newShell.setText(Messages.ScriptInfoDlgTitle);
    }
    
    /** {@inheritDoc} */
    @Override
    protected Control createDialogArea(Composite parent)
    {
        final Composite parent_composite = (Composite) super.createDialogArea(parent);
        final Composite composite = new Composite(parent_composite, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setLayout(new GridLayout(2, false));

        // Description: __description____
        Label l = new Label(composite, 0);
        l.setText(Messages.PrefEdit_Description);
        l.setLayoutData(new GridData(0, 0, false, false));
        description = new Text(composite, SWT.BORDER);
        description.setText(rowData[0]);
        description.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        // Script: __script____
        l = new Label(composite, 0);
        l.setText(Messages.PrefEdit_Command);
        l.setLayoutData(new GridData(SWT.TOP, 0, false, true));
        // Script could can be long. Limit the initial width, but allow WRAP
        script = new Text(composite, SWT.BORDER | SWT.WRAP);
        script.setText(rowData[1]);
        final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 500;
        script.setLayoutData(gd);

        return parent_composite;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    protected void okPressed()
    {
        rowData[0] = description == null ? "" : description.getText().trim();
        rowData[1] = script == null ? "" : script.getText().trim();
        super.okPressed();
    }

}
