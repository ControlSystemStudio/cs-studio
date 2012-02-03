/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Dialog for displaying an error.
 *
 *  In contrast to MessageDialog.openError(...), this dialog
 *  uses a text box for the error message, which allows users
 *  to copy/paste the error out into an email etc.
 *
 *  @deprecated Use ExceptionDetailsErrorDialog from org.csstudio.ui.util

 *  @author Kay Kasemir
 */
@Deprecated
public class ErrorDialog extends Dialog
{
    final private String title;
    final private String message;

    /** Initialize
     *  @param shell
     *  @param title
     *  @param message
     */
    private ErrorDialog(final Shell shell, final String title, final String message)
    {
        super(shell);
        this.title = title;
        this.message = message;
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

    /** Create the GUI. */
    @Override
    protected Control createDialogArea(final Composite parent)
    {
        final Composite parent_comp = (Composite) super.createDialogArea(parent);

        final Composite box = new Composite(parent_comp, 0);
        box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        box.setLayout(new GridLayout(2, false));

        final Label icon = new Label(box, 0);
        icon.setImage(parent.getDisplay().getSystemImage(SWT.ICON_ERROR));
        icon.setLayoutData(new GridData(0, SWT.TOP, false, false));

        final Text text = new Text(box, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
        text.setText(message);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        return parent_comp;
    }

    /** Create only an "OK" button */
    @Override
    protected void createButtonsForButtonBar(final Composite parent)
    {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true);
    }

    /** Open and display the dialog
     *  @param shell Parent shell
     *  @param title Dialog Title
     *  @param message Error message
     */
    static public void open(final Shell shell,
            final String title,
            final String message)
    {
        final ErrorDialog dialog = new ErrorDialog(shell, title, message);
        dialog.open();
    }
}
