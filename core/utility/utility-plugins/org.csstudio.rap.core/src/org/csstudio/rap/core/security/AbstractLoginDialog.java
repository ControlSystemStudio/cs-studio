/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.rap.core.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**An abstract login dialog the implemented {@link CallbackHandler}.
 * @author Xihui Chen
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractLoginDialog extends Dialog implements
		CallbackHandler {

	Callback[] callbackArray;

	protected final Callback[] getCallbacks() {
		return this.callbackArray;
	}

	protected AbstractLoginDialog(Shell parentShell) {
		super(parentShell);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.security.auth.callback.CallbackHandler#handle(javax.security.auth
	 * .callback.Callback[])
	 */
	public void handle(final Callback[] callbacks) throws IOException {
		this.callbackArray = callbacks;
		final Display display = Display.getDefault();
		display.syncExec(new Runnable() {

			public void run() {
				setBlockOnOpen(true);
				open();
			}
		});

	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Login");
	}
}
