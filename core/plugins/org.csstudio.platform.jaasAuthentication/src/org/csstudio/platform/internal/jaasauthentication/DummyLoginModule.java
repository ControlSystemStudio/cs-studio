package org.csstudio.platform.internal.jaasauthentication;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * This class is provided for testing purposes. It accepts any combination of
 * username (except &quot;fail&quot;) and password as valid and authenticates
 * the user. If &quot;fail&quot; is entered as the username, the login fails.
 * 
 * @author Joerg Rathlev
 */
public class DummyLoginModule implements LoginModule {

	private Subject subject;
	private CallbackHandler callbackHandler;
	private String username = "anonymous";

	public boolean abort() throws LoginException {
		// always successful, there is nothing to rollback
		return true;
	}

	public boolean commit() throws LoginException {
		if (!username.equals("fail")) {
			subject.getPrincipals().add(new DummyPrincipal(username));
		}
		return true;
	}
	
	/**
	 * A {@code DummyPrincipal} is added to the {@code Subject} when this
	 * login module commits.
	 */
	private static class DummyPrincipal implements Principal {
		private String username;
		
		private DummyPrincipal(String username) {
			this.username = username;
		}
		
		public String getName() {
			return username;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof DummyPrincipal) {
				return ((DummyPrincipal) o).username.equals(username);
			} else {
				return false;
			}
		}
		
		@Override
		public int hashCode() {
			return username.hashCode();
		}
	}

	public void initialize(Subject subject, CallbackHandler handler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		this.subject = subject;
		this.callbackHandler = handler;
	}

	public boolean login() throws LoginException {
		username = getUsernameFromCallbackHandler();
		return !username.equals("fail");
	}
	
	/**
	 * Sends a {@code NameCallback} to the callback handler to get the
	 * username.
	 */
	private String getUsernameFromCallbackHandler() {
		NameCallback nameCallback = new NameCallback("User name");
		try {
			callbackHandler.handle(new Callback[] {nameCallback});
		} catch (IOException e) {
			return "anonymous";
		} catch (UnsupportedCallbackException e) {
			return "anonymous";
		}
		return nameCallback.getName();
	}

	public boolean logout() throws LoginException {
		// Note: a real implementation would have to remove the principal
		// added by this login module from the subject, but I have not
		// implemented that because this module is only for testing anyway.
		return true;
	}

}
