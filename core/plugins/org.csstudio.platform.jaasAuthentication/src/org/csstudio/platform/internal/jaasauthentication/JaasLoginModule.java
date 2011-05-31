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
 package org.csstudio.platform.internal.jaasauthentication;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.csstudio.auth.securestore.SecureStore;
import org.csstudio.auth.security.Credentials;
import org.csstudio.auth.security.ILoginCallbackHandler;
import org.csstudio.auth.security.ILoginModule;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.security.User;
import org.csstudio.platform.internal.jassauthentication.preference.ConfigurationFromPreferences;
import org.csstudio.platform.internal.jassauthentication.preference.JAASPreferenceModel;
import org.csstudio.platform.internal.jassauthentication.preference.PreferencesHelper;

/**
 * Performs user login via JAAS.
 *
 * @author Joerg Rathlev
 * @author Xihui Chen
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JaasLoginModule implements ILoginModule {

	/**
	 * The key for the property of the User object which stores the JAAS
	 * {@link Subject}.
	 */
	private static final String SUBJECT_PROPERTY = "JAAS.Subject";

	/**
	 * The key of the system property which points to the JAAS configuration
	 * file.
	 */
	private static final String AUTH_CONFIG_PROPERTY = "java.security.auth.login.config";

	/** The name of the JAAS configuration file included in the plug-in.
	 *  @see #JAAS_CONFIG
	 */
	private static final String CONFIG_FILE = "conf/auth.conf";

	/**
	 * {@inheritDoc}
	 */
	@Override
    public User login(final ILoginCallbackHandler handler) {
		// Determine which JAAS configuration entry to use
		//final IPreferencesService service = Platform.getPreferencesService();
		//final String contextName = service.getString(Activator.PLUGIN_ID,
		//		JAAS_CONFIG, null, null);
		String contextName;
		if(PreferencesHelper.getConfigSource().equals(JAASPreferenceModel.SOURCE_FILE)) {
			contextName = PreferencesHelper.getConfigFileEntry();
			setConfigFileProperty();
		} else {
			contextName = "From_Preference_Page"; //$NON-NLS-1$
			Configuration.setConfiguration(new ConfigurationFromPreferences());
		}

        final Logger logger = Logger.getLogger(getClass().getName());
        logger.fine("Using JAAS config '" + contextName + "'");

		final CredentialsCallbackHandler ch = new CredentialsCallbackHandler();

		LoginContext loginCtx = null;
		User user = null;
		boolean loggedIn = false;

		// Re-attempt to login as long as the login is not complete.
		while (!loggedIn) {
			// The LoginContext cannot be reused if a call to its login()
			// method failed. This is why a new LoginContext instance is
			// created in every iteration through this loop.
			ch.credentials = handler.getCredentials();
			if (ch.credentials != null) {
				//Anonymous login
				if(ch.credentials == Credentials.ANONYMOUS) {
					loggedIn = true;
				} else {	//real login
					try {
							loginCtx = new LoginContext(contextName, ch);
						} catch (LoginException e) {
							logger.log(Level.WARNING, "Login error: cannot create a JAAS LoginContext. Using anonymously.", e);
							return null;
						}
					try {
						loginCtx.login();  // this will call back to get the credentials
						final Subject subject = loginCtx.getSubject();
						user = subjectToUser(subject);
						loggedIn = true;
						final SecureStore store = SecureStore.getInstance();
						store.unlock(user.getUsername(),
								ch.credentials.getPassword());
					} catch (LoginException e) {
						// Note: LoginContext unfortunately does not throw a
						// more specific exception than LoginException.
						handler.signalFailedLoginAttempt();
						logger.log(Level.WARNING, "Login failed", e);
					}
				}
			} else { //user canceled, keep the current user as it was
				loggedIn = true;
				user = SecurityFacade.getInstance().getCurrentUser();
			}
		}
		return user;
	}

	/**
	 * Sets the java.security.auth.login.config system property to
	 * a valid configuration file. If the property is already set and it points
	 * to an existing file, the property is not modified. If it is not set or
	 * does not point to an existing file, it is set to the URL of the
	 * configuration file included in this plug-in.
	 */
	private void setConfigFileProperty() {
		final String prop = System.getProperty(AUTH_CONFIG_PROPERTY);
		if (prop == null || !(new File(prop).exists())) {
			final URL url = Activator.getDefault().getBundle()
				.getResource(CONFIG_FILE);
			System.setProperty(AUTH_CONFIG_PROPERTY, url.toExternalForm());
		}
	}

	/**
	 * Creates a {@link User} object for a specified {@link Subject}.
	 * @param subject the {@code Subject}.
	 * @return a CSS {@code User}.
	 * @throws LoginException if no user object can be created for the subject.
	 */
	private User subjectToUser(final Subject subject) throws LoginException {
		final User user = new User(subjectToUsername(subject));
		user.setProperty(SUBJECT_PROPERTY, subject);
		return user;
	}

	/**
	 * Returns the given subject's username. The username is taken from the
	 * subject's principals. If the subject has more than one principal, one
	 * of them will be chosen arbitrarily to get the username.
	 *
	 * @param subject the subject.
	 * @return a username.
	 * @throws LoginException if the subject does not contain any principals.
	 */
	private String subjectToUsername(final Subject subject) throws LoginException {
		for (Principal p :  subject.getPrincipals()) {
			// return the name of the first principal
			return p.getName();
		}
		throw new LoginException("Subject does not have principals.");
	}

	/**
	 * Throws an {@link UnsupportedOperationException}. This module does not
	 * support logout.
	 * @throws UnsupportedOperationException always thrown.
	 */
	@Override
    public void logout() {
		/*
		 * Note: to do logout, we would have to store the jaas login context and
		 * call its logout() method. This would work, but I am not sure what
		 * to do with the user object after logout.
		 */

		throw new UnsupportedOperationException();
	}

	/**
	 * Implementation of {@link CallbackHandler} that will return the username
	 * and password stored in a credentials object when called back.
	 */
	private static class CredentialsCallbackHandler implements CallbackHandler {
		private Credentials credentials;

		/**
		 * Handles username and password callbacks by returning the username
		 * and password of the credentials. Other callbacks are not supported.
		 */
		@Override
        public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
			for (Callback c : callbacks) {
				if (c instanceof NameCallback) {
					// return the username
					((NameCallback) c).setName(credentials.getUsername());
				} else if (c instanceof PasswordCallback) {
					// return the password
					((PasswordCallback) c).setPassword(credentials.getPassword().toCharArray());
				} else {
					throw new UnsupportedCallbackException(c);
				}
			}
		}
	}
}
