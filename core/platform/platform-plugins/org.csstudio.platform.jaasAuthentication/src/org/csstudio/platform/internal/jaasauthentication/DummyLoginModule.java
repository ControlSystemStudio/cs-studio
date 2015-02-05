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
