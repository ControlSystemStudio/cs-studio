/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.dialog;

import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Dialog for error messages that uses a tray to optionally
 *  display more detail
 *  @deprecated Use ExceptionDetailsErrorDialog from org.csstudio.ui.util
 *  @author Kay Kasemir
 */
@Deprecated
public class ErrorDetailDialog  extends TrayDialog
{
    final String title;
    final String message;
    final String detail;

    /** Initialize
     *  @param shell Parent shell
     *  @param title Dialog title
     *  @param message Main message, multi-line but brief
     *  @param detail Detailed message, multi-line and possibly longer
     */
    public ErrorDetailDialog(final Shell shell,
            final String title, final String message, final String detail)
    {
        super(shell);
        this.title = title;
        this.message = message;
        this.detail = detail;
    }

    /** Allow resize */
    @Override
    protected boolean isResizable()
    {
        return true;
    }

    /** Set the dialog title. */
    @Override
    protected void configureShell(final Shell shell)
    {
        super.configureShell(shell);
        shell.setText(title);
    }

    /** Create message and button to show detail */
    @Override
    protected Control createDialogArea(final Composite parent)
    {
        final Composite composite = (Composite) super.createDialogArea(parent);
        composite.setLayout(new GridLayout());

        Label l = new Label(composite, 0);
        l.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        l.setText(message);

        final Button detail_button = new Button(composite, SWT.PUSH);
        detail_button.setLayoutData(new GridData(SWT.RIGHT, 0, true, false));
        detail_button.setText(Messages.ErrorDetailDialog_More);
        detail_button.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                detail_button.setVisible(false);
                showDetail();
            }
        });
        return composite;
    }

    /** Open tray to display detail */
    protected void showDetail()
    {
        final DialogTray tray = new DialogTray()
        {
            @Override
            protected Control createContents(final Composite parent)
            {
                final Composite container = new Composite(parent, SWT.NONE);
                container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                container.setLayout(new GridLayout());

                final Text info = new Text(container, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
                info.setText(detail);
                // Use width hint to limit growth of dialog for long texts.
                // User can then manually resize as desired.
                final GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
                data.widthHint = 500;
                info.setLayoutData(data);

                return container;
            }
        };
        openTray(tray);
    }

    /** Show only the 'OK' button, no 'Cancel' */
    @Override
    protected void createButtonsForButtonBar(final Composite parent)
    {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true);
    }
}
