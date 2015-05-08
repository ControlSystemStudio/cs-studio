/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.rap.core.security;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Handles the callbacks to show a login dialog for the LoginModule.
 *
 * @author Xihui Chen
 *
 */
@SuppressWarnings("serial")
public class LoginDialogCallbackHandler extends AbstractLoginDialog {

    public LoginDialogCallbackHandler(Display display) {
        this(display.getActiveShell());
    }

    protected LoginDialogCallbackHandler(Shell parentShell) {
        super(parentShell);
    }

    protected Control createDialogArea(Composite parent) {
        Composite dialogarea = (Composite) super.createDialogArea(parent);
        dialogarea.setLayoutData(new GridData(GridData.FILL_BOTH));
        dialogarea.setLayout(new GridLayout(2, false));
        createCallbackHandlers(dialogarea);
        return dialogArea;
    }

    private void createCallbackHandlers(Composite composite) {
        Callback[] callbacks = getCallbacks();
        for (int i = 0; i < callbacks.length; i++) {
            Callback callback = callbacks[i];
            if (callback instanceof TextOutputCallback) {
                createTextOutputHandler(composite,
                        (TextOutputCallback) callback);
            } else if (callback instanceof NameCallback) {
                createNameHandler(composite, (NameCallback) callback);
            } else if (callback instanceof PasswordCallback) {
                createPasswordHandler(composite, (PasswordCallback) callback);
            }
        }
    }

    private void createPasswordHandler(Composite composite,
            final PasswordCallback callback) {
        Label label = new Label(composite, SWT.NONE);
        label.setText(callback.getPrompt());
        final Text passwordText = new Text(composite, SWT.SINGLE | SWT.LEAD
                | SWT.PASSWORD | SWT.BORDER);
        passwordText.setLayoutData(new GridData(GridData.FILL_BOTH));
        passwordText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                callback.setPassword(passwordText.getText().toCharArray());
            }
        });
    }

    private void createNameHandler(Composite composite,
            final NameCallback callback) {
        Label label = new Label(composite, SWT.NONE);
        label.setText(callback.getPrompt());
        final Text text = new Text(composite, SWT.SINGLE | SWT.LEAD
                | SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL_BOTH));
        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                callback.setName(text.getText());
            }
        });
    }

    private void createTextOutputHandler(Composite composite,
            TextOutputCallback callback) {
        getShell().setText(callback.getMessage());
    }


}
