/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.preferences;

import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.ui.util.swt.stringtable.RowEditDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Dialog to edit an ArchiveDataSource
 *  @author Xihui Chen - original org.csstudio.opibuilder.preferences.MacroEditDialog
 *  @author Kay Kasemir
 */
public class ArchiveDataSourceEditor extends RowEditDialog
{
    private Text name, key, url;

    /** Initialize */
    public ArchiveDataSourceEditor(final Shell shell)
    {
        super(shell);
    }

    /** {@inheritDoc} */
    @Override
    protected void configureShell(Shell newShell)
    {
        super.configureShell(newShell);
        newShell.setText(Messages.PrefPage_Archives);
    }

    /** {@inheritDoc} */
    @Override
    protected Control createDialogArea(Composite parent)
    {
        final Composite parent_composite = (Composite) super.createDialogArea(parent);
        final Composite composite = new Composite(parent_composite, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setLayout(new GridLayout(2, false));

        // Name: __name____
        Label l = new Label(composite, 0);
        l.setText(Messages.NameLbl);
        l.setLayoutData(new GridData(0, 0, false, false));
        name = new Text(composite, SWT.BORDER);
        name.setText(rowData[0]);
        name.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        // Key: __key____
        l = new Label(composite, 0);
        l.setText(Messages.KeyLbl);
        l.setLayoutData(new GridData(0, 0, false, false));
        key = new Text(composite, SWT.BORDER);
        key.setText(rowData[1]);
        key.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        // URL: __url____
        l = new Label(composite, 0);
        l.setText(Messages.URL_Lbl);
        l.setLayoutData(new GridData(SWT.TOP, 0, false, true));
        // URLs can be very long. Limit the initial width, but allow WRAP
        url = new Text(composite, SWT.BORDER | SWT.WRAP);
        url.setText(rowData[2]);
        final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 500;
        url.setLayoutData(gd);

        return parent_composite;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    protected void okPressed()
    {
        rowData[0] = name == null ? "" : name.getText().trim();
        rowData[1] = key == null ? "" : key.getText().trim();
        rowData[2] = url == null ? "" : url.getText().trim();
        super.okPressed();
    }
}
