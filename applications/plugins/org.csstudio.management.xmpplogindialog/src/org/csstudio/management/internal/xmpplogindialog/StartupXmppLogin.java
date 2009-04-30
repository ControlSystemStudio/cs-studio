/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.management.internal.xmpplogindialog;

import org.csstudio.platform.securestore.SecureStore;
import org.csstudio.platform.startupservice.IStartupServiceListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.remotercp.common.servicelauncher.ServiceLauncher;
import org.remotercp.ecf.ECFConstants;
import org.remotercp.login.connection.HeadlessConnection;
import org.remotercp.login.ui.ChatLoginWizardDialog;

/**
 * Performs a user login on the XMPP server during CSS startup. If no username
 * and password for the login are stored in the Secure Store, a login dialog is
 * displayed to the user.
 * 
 * @author Joerg Rathlev
 */
public class StartupXmppLogin implements IStartupServiceListener {
	
	private String _xmppServer;

	/**
	 * Thrown if a login attempt fails.
	 */
	private static final class LoginFailedException extends Exception {
		private static final long serialVersionUID = 1L;
		
		LoginFailedException(Throwable cause) {
			super(cause);
		}
	}

	/**
	 * Runnable which performs the actual XMPP login.
	 */
	private static final class XmppLoginProcess implements Runnable {
		
		private final String _server;

		/**
		 * Creates a new login process.
		 * 
		 * @param server
		 *            the server to connect to.
		 */
		XmppLoginProcess(String server) {
			_server = server;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public void run() {
			if (!tryLoginWithSecureStore()) {
				loginWithDialog();
			}
		}

		/**
		 * Tries to login the user on the XMPP server based on the username
		 * and password found in the Secure Store.
		 * 
		 * @return <code>true</code> if the login was successful,
		 *         <code>false</code> otherwise.
		 */
		private boolean tryLoginWithSecureStore() {
			SecureStore store = SecureStore.getInstance();
			try {
				String username = (String) store.getObject("xmpp.username");
				String password = (String) store.getObject("xmpp.password");
				if (username != null && password != null) {
					login(username, password);
					return true;
				}
			} catch (LoginFailedException e) {
				// Don't do anything (simply return false, below)
			}
			return false;
		}

		/**
		 * Shows a login dialog to the user and then tries to login the user
		 * based on the information he entered in the dialog.
		 */
		private void loginWithDialog() {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					ChatLoginWizardDialog wizardDialog = new ChatLoginWizardDialog();
					if (wizardDialog.open() == Window.OK) {
						// start remote services
						ServiceLauncher.startRemoteServices();
					}
				}
			});
		}

		/**
		 * Logs in the given user with the given password.
		 * 
		 * @param username
		 *            the username.
		 * @param password
		 *            the password.
		 * @throws LoginFailedException
		 *             if the login fails.
		 */
		private void login(String username, String password)
				throws LoginFailedException {
			try {
				HeadlessConnection.connect(username, password, _server,
						ECFConstants.XMPP);
			} catch (Exception e) {
				throw new LoginFailedException(e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {
		readPreferences();
		Runnable xmppLogin = new XmppLoginProcess(_xmppServer);
		new Thread(xmppLogin, "XMPP Login").start();
	}

	/**
	 * Reads the login settings from the prefereces.
	 */
	private void readPreferences() {
		IPreferencesService prefs = Platform.getPreferencesService();
		_xmppServer = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.XMPP_SERVER, "krykxmpp.desy.de", null);
	}

}
