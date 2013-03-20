/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.ui.internal;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.csstudio.security.authentication.LoginJob;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Log in dialog
 * 
 *  <p>Uses Eclipse {@link ILoginContext} to perform
 *  a JAAS-based login.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls") // TODO Externalize strings
public class LoginDialog extends Dialog
{
    // GUI elements
    private Text user;
    private Text password;
    private Label message;
    private Job job;
    
    /** JAAS {@link CallbackHandler} that
     *  fetches name, password from dialog
     *  and displays errors in dialog as well.
     */
    class DialogCallbackHandler implements CallbackHandler
    {
        @Override
        public void handle(final Callback[] callbacks) throws IOException,
                UnsupportedCallbackException
        {
            final Display display = user.getDisplay();
            for (Callback callback : callbacks)
            {
                if (callback instanceof NameCallback)
                {
                    final NameCallback nc = (NameCallback) callback;
                    display.syncExec(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            nc.setName(user.getText());
                        }
                    });
                }
                else if (callback instanceof PasswordCallback)
                {
                    final PasswordCallback pc = (PasswordCallback) callback;
                    display.syncExec(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            pc.setPassword(password.getTextChars());
                        }
                    });
                }
                else if (callback instanceof TextOutputCallback)
                {
                    final TextOutputCallback tc = (TextOutputCallback) callback;
                    display.syncExec(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (user.isDisposed())
                                return;
                            if (tc.getMessageType() == TextOutputCallback.INFORMATION  &&
                                "OK".equals(tc.getMessage()))
                            {
                                // Close dialog
                                setReturnCode(OK);
                                close();
                            }
                            else
                                displayError(tc.getMessage());
                        }
                    });
                }
            }
        }
    };
    
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
        shell.setText("Log in...");
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

        user = new Text(composite, SWT.BORDER);
        user.setToolTipText("Enter user name");
        user.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        
        l = new Label(composite, 0);
        l.setText("Password:");
        l.setLayoutData(new GridData());

        password = new Text(composite, SWT.BORDER | SWT.PASSWORD);
        password.setToolTipText("Enter password");
        password.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        
        message = new Label(composite, 0);
        message.setForeground(composite.getDisplay().getSystemColor(SWT.COLOR_RED));
        message.setLayoutData(new GridData(SWT.FILL, 0, true, false, 2, 1));
        
        return composite;
    }

    /** @param error Error message to display */
    private void displayError(final String error)
    {
        if (error == null)
            message.setText("");
        else
            message.setText(error);
        message.getParent().layout();
    }

    /** Perform login with entered name and password */
    @Override
    protected void okPressed()
    {
        job = new LoginJob(new DialogCallbackHandler());
        job.schedule();
    }

    @Override
    protected void cancelPressed()
    {
        if (job != null)
            job.cancel();
        super.cancelPressed();
    }
}
