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

import org.csstudio.auth.securestore.SecureStore;
import org.csstudio.auth.security.Credentials;
import org.csstudio.auth.ui.dialogs.LoginDialog;
import org.csstudio.platform.startupservice.IStartupServiceListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

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
	 * Runnable which performs the actual XMPP login.
	 */
	private static final class XmppLoginProcess implements Runnable, IGenericServiceListener<ISessionService> {
		
		// Keys for the objects in the secure store
		private static final String SECURE_STORE_USERNAME = "xmpp.username";
		private static final String SECURE_STORE_PASSWORD = "xmpp.password";
		
		private ISessionService _sessionService;
		private final String _server;

		/**
		 * Creates a new login process.
		 * 
		 * @param server
		 *            the server to connect to.
		 */
		XmppLoginProcess(String server) {
			Activator.getDefault().addSessionServiceListener(this);
			_server = server;
		}
		
		/**
		 * Tries to log in the user based on the data found in the Secure Store.
		 * If the user cannot be logged in based on the Secure Store, a login
		 * dialog is displayed.
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
			String username = (String) store.getObject(SECURE_STORE_USERNAME);
			String password = (String) store.getObject(SECURE_STORE_PASSWORD);
			if (username != null && password != null) {
				return tryLogin(username, password);
			}
			return false;
		}

		/**
		 * Shows a login dialog to the user and then tries to login the user
		 * based on the information he entered in the dialog.
		 */
		private void loginWithDialog() {
			boolean finished = false;
			do {
				Credentials credentials = showLoginDialog();
				if (credentials == null) {
					// the user cancelled the login
					finished = true;
				} else if (credentials == Credentials.ANONYMOUS) {
					finished = tryAnonymousLogin();
					if (!finished) {
						showLoginErrorMessage("Anonymous Login failed. Please try again.");
					}
				} else {
					finished = tryLogin(credentials);
					if (finished) {
						writeToSecureStore(credentials);
					} else {
						showLoginErrorMessage("Anonymous Login failed. Please try again.");
					}
				}
			} while (!finished);
		}

		/**
		 * Tries to log in anonymously using the username and password set in
		 * the preferences for anonymous login.
		 * 
		 * @return <code>true</code> if the login succeeded, <code>false</code>
		 *         otherwise.
		 */
		private boolean tryAnonymousLogin() {
			IPreferencesService prefs = Platform.getPreferencesService();
			String username = prefs.getString(Activator.PLUGIN_ID,
					PreferenceConstants.ANONYMOUS_LOGIN_USER, null, null);
			String password = prefs.getString(Activator.PLUGIN_ID,
					PreferenceConstants.ANONYMOUS_LOGIN_PASSWORD, null, null);
			if (username != null && password != null) {
				return tryLogin(username, password);
			}
			return false;
		}

		/**
		 * Writes the specified credentials into the secure store as the
		 * username and password for the XMPP login.
		 * 
		 * @param credentials
		 *            the credentials.
		 */
		private void writeToSecureStore(Credentials credentials) {
			SecureStore store = SecureStore.getInstance();
			store.setObject(SECURE_STORE_USERNAME, credentials.getUsername());
			store.setObject(SECURE_STORE_PASSWORD, credentials.getPassword());
		}

		/**
		 * Displays a login dialog in the UI thread and returns the credentials
		 * that the user entered in the dialog.
		 * 
		 * @return the credentials entered by the user. Returns
		 *         <code>null</code> if the user cancelled the dialog.
		 */
		private Credentials showLoginDialog() {
			// One-element array for communication with the UI thread.
			final Credentials[] credentials = new Credentials[1];
			
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					LoginDialog dialog = new LoginDialog(null, "ECF/XMPP Login",
							"Please enter your username and password for the XMPP server.",
							null);
					if (dialog.open() == Window.OK) {
						credentials[0] = dialog.getLoginCredentials();
					}
				}
			});
			
			// Return the credentials that the UI thread wrote into the array.
			return credentials[0];
		}
		
		/**
		 * Displays an error message to the user to inform the user that the
		 * login failed.
		 * @param message 
		 */
		private void showLoginErrorMessage(final String message) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(null, "ECF/XMPP Login",
							message);
				}
			});
		}

		/**
		 * Tries to log in the user with the specified credentials.
		 * 
		 * @param credentials
		 *            the credentials.
		 * @return <code>true</code> if the login succeeded, <code>false</code>
		 *         otherwise.
		 */
		private boolean tryLogin(Credentials credentials) {
			return tryLogin(credentials.getUsername(), credentials.getPassword());
		}

		/**
		 * Tries to log in the user with the specified username and password.
		 * 
		 * @param username
		 *            the username.
		 * @param password
		 *            the password.
		 * @return <code>true</code> if the login succeeded, <code>false</code>
		 *         otherwise.
		 */
		private boolean tryLogin(String username, String password) {
			try {
				if (_sessionService != null) {
					_sessionService.connect(username, password, _server);
				} else {
					showLoginErrorMessage("Session Service not available");
				}
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		public void bindService(ISessionService service) {
			_sessionService = service;
		}
		
		@Override
		public void unbindService(ISessionService service) {
			_sessionService = null;
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
