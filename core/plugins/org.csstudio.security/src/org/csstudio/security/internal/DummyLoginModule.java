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
package org.csstudio.security.internal;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.sun.security.auth.UserPrincipal;

/** JAAS Login Module for testing purposes.
 * 
 *  <p>Accepts any combination of username (except &quot;fail&quot;)
 *  and password as valid and authenticates the user.
 *  If &quot;fail&quot; is entered as the username, the login fails.
 *  
 *  <p>Registered via Extension point
 *  to be JAAS-accessible from outside this plugin's classpath.
 * 
 *  @author Joerg Rathlev - Original from org.csstudio.platform.internal.jaasauthentication;
 *  @author Kay Kasemir
 */
public class DummyLoginModule implements LoginModule
{
	private Subject subject;
	private CallbackHandler callbackHandler;
	private UserPrincipal principal = null;
	private boolean debug;

	@Override
    public void initialize(final Subject subject, final CallbackHandler handler,
			final Map<String, ?> sharedState, final Map<String, ?> options)
	{
		this.subject = subject;
		this.callbackHandler = handler;
        debug = "true".equalsIgnoreCase((String)options.get("debug"));
	}

    @Override
	public boolean login() throws LoginException
	{
		final String username = getUsernameFromCallbackHandler();
		if (debug)
			System.out.println("Dummy login for '" + username + "'");
		if ("fail".equals(username))
		{
			principal = null;
			return false;
		}
		principal = new UserPrincipal(username);
		return true;		
	}

    @Override
	public boolean abort() throws LoginException
	{
		// always successful, there is nothing to rollback
		return true;
	}

    @Override
	public boolean commit() throws LoginException
	{
		if (principal != null)
			subject.getPrincipals().add(principal);
		return true;
	}

	/** Sends a {@code NameCallback} to the callback handler to get the
	 *  username.
	 *  @throws LoginException 
	 */
	private String getUsernameFromCallbackHandler() throws LoginException
	{
		NameCallback nameCallback = new NameCallback("User name");
		try
		{
			callbackHandler.handle(new Callback[] {nameCallback});
			return nameCallback.getName();
		}
		catch (Exception ex)
		{
			throw new LoginException("Cannot obtain user name");
		}
	}
	
    @Override
	public boolean logout() throws LoginException
	{
		subject.getPrincipals().remove(principal);
		principal = null;
		return true;
	}
}
