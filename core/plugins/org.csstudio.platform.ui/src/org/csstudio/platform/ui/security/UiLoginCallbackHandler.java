/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.platform.ui.security;

import org.csstudio.platform.security.Credentials;
import org.csstudio.platform.security.ILoginCallbackHandler;
import org.csstudio.platform.ui.dialogs.LoginDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * Implementation of {@link ILoginCallbackHandler} which displays an interactive
 * dialog to the user.
 * 
 * @author Joerg Rathlev
 */
public final class UiLoginCallbackHandler implements ILoginCallbackHandler {

	/**
	 * The title of the dialog.
	 */
	private String _title;
	
	/**
	 * The messge that will be displayed in the dialog.
	 */
	private String _message;
	
	/**
	 * The user name that will be shown in the dialog when it opens.
	 */
	private String _defaultUserName;
	
	/**
	 * Creates a new login callback handler.
	 * 
	 * @param title
	 *            the title of the login dialog.
	 * @param message
	 *            the message shown in the login dialog.
	 * @param defaultUserName
	 *            the user name that is preset in the dialog when it opens. Set
	 *            this to <code>null</code> for no preset.
	 */
	public UiLoginCallbackHandler(final String title, final String message, 
			final String defaultUserName) {
		_title = title;
		_message = message;
		_defaultUserName = defaultUserName;
	}
	
	/**
	 * Displays a login dialog and returns the credentials that were entered by
	 * the user. Returns <code>null</code> if the user did not enter any
	 * credentials or cancelled the dialog.
	 */
	public Credentials getCredentials() {
		// a one-element array for communication between the current thread
		// and the UI thread
		final Credentials[] credentials = new Credentials[1];
		
		// run the login dialog in the UI thread
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				LoginDialog dialog = new LoginDialog(
						Display.getCurrent().getActiveShell(),
						_title, _message, _defaultUserName);
				if (dialog.open() == Window.OK) {
					credentials[0] = dialog.getLoginCredentials();
				}
			}
		});
		return credentials[0];
	}

	/**
	 * Displays an error message to the user.
	 */
	public void signalFailedLoginAttempt() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				MessageDialog.openError(null, _title,
						"Login failed. Please try again.");
			}
		});
	}

}
