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

/** Dialog to edit an archive data server URL
 *  @author Xihui Chen - original org.csstudio.opibuilder.preferences.MacroEditDialog
 *  @author Kay Kasemir
 */
public class ArchiveURLEditor extends RowEditDialog
{
    private Text url;

    /** Initialize */
    public ArchiveURLEditor(final Shell shell)
    {
        super(shell);
    }

    /** {@inheritDoc} */
    @Override
    protected void configureShell(final Shell newShell)
    {
        super.configureShell(newShell);
        newShell.setText(Messages.ArchiveURLDialogTitle);
    }

    /** {@inheritDoc} */
    @Override
    protected Control createDialogArea(final Composite parent)
    {
        final Composite parent_composite = (Composite) super.createDialogArea(parent);
        final Composite composite = new Composite(parent_composite, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setLayout(new GridLayout(2, false));

        // URL: __url____
        final Label titleLable = new Label(composite, 0);
        titleLable.setText(Messages.URL_Lbl);
        titleLable.setLayoutData(new GridData(SWT.TOP, 0, false, true));

        // URLs can be very long. Limit the initial width, but allow WRAP
        url = new Text(composite, SWT.BORDER | SWT.WRAP);
        url.setText(rowData[0]);
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
        rowData[0] = url == null ? "" : url.getText().trim();
        super.okPressed();
    }
}
