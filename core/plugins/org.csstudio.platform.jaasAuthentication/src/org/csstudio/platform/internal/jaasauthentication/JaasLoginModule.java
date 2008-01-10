package org.csstudio.platform.internal.jaasauthentication;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.securestore.SecureStore;
import org.csstudio.platform.security.Credentials;
import org.csstudio.platform.security.ILoginCallbackHandler;
import org.csstudio.platform.security.ILoginModule;
import org.csstudio.platform.security.User;

/**
 * Performs user login via JAAS.
 * 
 * @author Joerg Rathlev
 */
public class JaasLoginModule implements ILoginModule {
	
	/**
	 * The name of the configuration entry for the JAAS {@link LoginContext}.
	 */
	private static final String CONTEXT_NAME = "ControlSystemStudio";
	
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
	
	/**
	 * The name of the JAAS configuration file included in the plug-in.
	 */
	private static final String CONFIG_FILE = "conf/auth.conf";

	/**
	 * The logger.
	 */
	private static final CentralLogger log = CentralLogger.getInstance();

	/**
	 * {@inheritDoc}
	 */
	public User login(ILoginCallbackHandler handler) {
		CredentialsCallbackHandler ch = new CredentialsCallbackHandler();
		setConfigFileProperty();
		LoginContext loginCtx = null;
		User user = null;
		boolean loggedIn = false;
		
		// Re-attempt to login as long as the login is not complete.
		while (!loggedIn) {
			// The LoginContext cannot be reused if a call to its login()
			// method failed. This is why a new LoginContext instance is
			// created in every iteration through this loop.
			try {
				loginCtx = new LoginContext(CONTEXT_NAME, ch);
			} catch (LoginException e) {
				log.error(this, "Login error: cannot create a JAAS LoginContext. Using anonymously.", e);
				return null;
			}
			
			ch.credentials = handler.getCredentials();
			if (ch.credentials != null) {
				try {
					loginCtx.login();  // this will call back to get the credentials
					Subject subject = loginCtx.getSubject();
					user = subjectToUser(subject);
					loggedIn = true;
					SecureStore store = SecureStore.getInstance();
					store.unlock(user.getUsername(),
							ch.credentials.getPassword());
				} catch (LoginException e) {
					// Note: LoginContext unfortunately does not throw a
					// more specific exception than LoginException.
					
					handler.signalFailedLoginAttempt();
					log.info(this, "Login failed", e);
				}
			} else {
				// no credentials -> anonymous login
				loggedIn = true;
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
		String prop = System.getProperty(AUTH_CONFIG_PROPERTY);
		if (prop == null || !(new File(prop).exists())) {
			URL url = Activator.getDefault().getBundle()
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
	private User subjectToUser(Subject subject) throws LoginException {
		User user = new User(subjectToUsername(subject));
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
	private String subjectToUsername(Subject subject) throws LoginException {
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
		public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
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
