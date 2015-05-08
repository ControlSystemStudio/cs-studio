/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.rdbtable.ui;

import org.csstudio.display.rdbtable.Messages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Log in dialog
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls") // TODO Externalize strings
public class LoginDialog extends Dialog
{
    // GUI elements
    private Text txt_user, txt_password;
    private String user, password;

    /** Initialize
     *  @param shell Parent shell
     */
    public LoginDialog(final Shell shell)
    {
        super(shell);
    }

    /** Set title
     *  @param shell Dialog's shell
     */
    @Override
    protected void configureShell(final Shell shell)
    {
        super.configureShell(shell);
        shell.setText(Messages.LoginTitle);
    }

    /** Allow resize */
    @Override
    protected boolean isResizable()
    {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    protected Control createDialogArea(final Composite parent)
    {
        final Composite composite = (Composite) super.createDialogArea(parent);
        // In 3.7.2, parent does create Composite with GridLayout...
        final GridLayout layout = (GridLayout) composite.getLayout();
        layout.numColumns = 2;

        Label l = new Label(composite, 0);
        l.setText("User Name:");
        l.setLayoutData(new GridData());

        txt_user = new Text(composite, SWT.BORDER);
        txt_user.setToolTipText("Enter user name");
        txt_user.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        l = new Label(composite, 0);
        l.setText("Password:");
        l.setLayoutData(new GridData());

        txt_password = new Text(composite, SWT.BORDER | SWT.PASSWORD);
        txt_password.setToolTipText("Enter password");
        txt_password.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        return composite;
    }

    /** Perform login with entered name and password */
    @Override
    protected void okPressed()
    {
        user = txt_user.getText();
        password = txt_password.getText();
        super.okPressed();
    }

    /** @return User name */
    public String getUser()
    {
        return user;
    }

    /** @return Password */
    public String getPassword()
    {
        return password;
    }
}
