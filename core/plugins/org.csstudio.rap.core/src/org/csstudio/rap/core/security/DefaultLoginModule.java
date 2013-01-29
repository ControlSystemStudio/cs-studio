/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.rap.core.security;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.sun.security.auth.UserPrincipal;

/**The default login module for css.rap. The username and password are defined 
 * in its options, for example: username="myname", password="mypassword".
 * @author Xihui Chen
 *
 */
public class DefaultLoginModule implements LoginModule {

	private static final String PASSWORD = "password";
	private static final String USERNAME = "username";
	private CallbackHandler callbackHandler;
	private boolean loggedIn;
	private Subject subject;

	private String username;

	private Map<String, ?> options;

	public DefaultLoginModule() {
	}

	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.options = options;
	}

	public boolean login() throws LoginException {

		String requiredUserName = (String) options.get(USERNAME);
		if (requiredUserName == null)
			throw new LoginException(
					"No user name is defined for default login module.");

		String requiredPassword = (String) options.get(PASSWORD);
		if (requiredPassword == null)
			throw new LoginException(
					"No password is defined for default login module.");

		Callback label = new TextOutputCallback(TextOutputCallback.INFORMATION,
				"Please login!");
		NameCallback nameCallback = new NameCallback("Username:");
		PasswordCallback passwordCallback = new PasswordCallback("Password:",
				false);
		try {
			callbackHandler.handle(new Callback[] { label, nameCallback,
					passwordCallback });
		} catch (ThreadDeath death) {
			LoginException loginException = new LoginException();
			loginException.initCause(death);
			throw loginException;
		} catch (Exception exception) {
			LoginException loginException = new LoginException();
			loginException.initCause(exception);
			throw loginException;
		}
		String username = nameCallback.getName();
		String password = null;
		if (passwordCallback.getPassword() != null) {
			password = String.valueOf(passwordCallback.getPassword());
		}
		loggedIn = requiredUserName.equals(username)
				&& requiredPassword.equals(password);
		if (!loggedIn)
			throw new LoginException("Wrong user name or password.");
		this.username = username;
		return loggedIn;
	}

	public boolean commit() throws LoginException {
		subject.getPrincipals().add(new UserPrincipal(username));
		return loggedIn;
	}

	public boolean abort() throws LoginException {
		loggedIn = false;
		return true;
	}

	public boolean logout() throws LoginException {
		loggedIn = false;
		return true;
	}
}
