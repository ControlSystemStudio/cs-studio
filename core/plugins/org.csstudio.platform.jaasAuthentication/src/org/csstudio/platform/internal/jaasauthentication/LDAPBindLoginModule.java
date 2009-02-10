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

import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.eclipse.osgi.util.NLS;

/** JAAS Login Module that attempts to 'bind' to LDAP.
 *  In principle the JndiLoginModule already allows LDAP authentication,
 *  but it has several requirements:
 *  <ul>
 *  <li>userPassword must be in '{crypt}' format
 *  <li>entry must have uidNumber and gidNumber
 *  </ul>
 *   
 *  This login module performs a 'bind' to LDAP without trying
 *  to read any specific attributes.
 *  <p>
 *  JAAS config file parameters:
 *  <ul>
 *  <li>debug: Set to "true" to enable debug messages which will include
 *             the user's password!
 *  <li>user.provider.url: URL of the LDAP server, something like
 *             "ldap://localhost:389/ou=People,dc=test,dc=ics"
 *  <li>user.dn.format: NLS.bind format of the DN to which we will
 *             perform the bind, for example
 *             "uid={0},ou=People,dc=test,dc=ics".
 *             The {0} will be replaced with the user name
 *  </ul>
 *  <p>
 *  For now this login module always performs a "simple" bind.
 * 
 *  @author Kay Kasemir
 *  @see com.sun.security.auth.module.JndiLoginModule
 */
@SuppressWarnings("nls")
public class LDAPBindLoginModule implements LoginModule
{
    private static final String USER_DN_FORMAT_TAG = "user.dn.format";

    /** JAAS config argument to enable debug */
    private static final String DEBUG_TAG = "debug";

    /** JAAS config argument for LDAP server URL
     *  Using same name as JndiLoginModule to ease transition
     */
    public final String URL_TAG = "user.provider.url";

    private Subject subject;
    private CallbackHandler callbackHandler;

    /** Debug flag from JAAS config */
    private boolean debug;

    /** LDAP server URL from JAAS config */
    private String server_url;

    /** Format of DN for user from JAAS config */
    private String user_dn_format;

    /** Name of authenticated user or <code>null</code> */
    private String user = null;
    
    /** Initialize from JAAS config file
     *  @see LoginModule
     */
    public void initialize(Subject subject, CallbackHandler callbackHandler,
            Map<String, ?> sharedState, Map<String, ?> options)
    {
        this.subject = subject;
        this.callbackHandler = callbackHandler;

        // initialize any configured options
        debug = "true".equalsIgnoreCase((String)options.get(DEBUG_TAG));
        server_url = (String)options.get(URL_TAG);
        user_dn_format = (String)options.get(USER_DN_FORMAT_TAG);
    }

    /** Perform login, calling back for user name and password
     *  @see LoginModule
     */
    public boolean login() throws LoginException
    {
        if (server_url == null)
            throw new LoginException("Missing " + URL_TAG);
        
        if (callbackHandler == null)
            throw new LoginException("No CallbackHandler");

        final String user_pw[] = getUserPassword();
        // Check user against password to verify that it's indeed
        // the claimed user
        if (debug)
            System.out.println("LDAPBindLoginModule: '" + user_pw[0] + "', '" +
                    user_pw[1] + "'");
        if (authenticate(user_pw[0], user_pw[1]))
        {
            user = user_pw[0];
            return true;
        }

        return false;
    }

    /** Obtain user name and password via JAAS callbacks
     *  @return Array with user name and password
     *  @throws LoginException on error
     */
    private String[] getUserPassword() throws LoginException
    {
        final NameCallback name = new NameCallback("User Name:");
        final PasswordCallback password = new PasswordCallback("Password :", false);
        try
        {
            callbackHandler.handle(new Callback[] { name, password });
        }
        catch (Throwable ex)
        {
            throw new LoginException("Cannot get user/password");
        }
        final String result[] = new String[]
        {
            name.getName(),
            new String(password.getPassword())
        };
        password.clearPassword();
        return result;
    }

    /** Authenticate to LDAP via 'bind'
     *  @param user User name
     *  @param password Password
     *  @return <code>true</code> if OK
     */
    private boolean authenticate(final String user, final String password)
    {
        final Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, server_url);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        final String dn = NLS.bind(user_dn_format, user);
        if (debug)
            System.out.println("Attempting to bind to '" + dn + "'");
        env.put(Context.SECURITY_PRINCIPAL, dn);
        env.put(Context.SECURITY_CREDENTIALS, password);
        
        try
        {
            final DirContext context = new InitialDirContext(env);
            context.close();
            return true;
        }
        catch (NamingException ex)
        {
            if (debug)
                System.out.println("Bind failed: " + ex.getMessage());
            return false;
        }
    }

    /** @see LoginModule */
    public boolean abort() throws LoginException
    {
        return false;
    }

    /** @see LoginModule */
    public boolean commit() throws LoginException
    {
        if (user == null)
            return false;
        subject.getPrincipals().add(new SimplePrincipal(user));
        return true;
    }

    /** @see LoginModule */
    public boolean logout() throws LoginException
    {
        return true;
    }
}
