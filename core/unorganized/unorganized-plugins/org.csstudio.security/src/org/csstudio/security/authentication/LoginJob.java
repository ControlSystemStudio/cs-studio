/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.authentication;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.login.Configuration;

import org.csstudio.security.SecurityPreferences;
import org.csstudio.security.internal.PreferenceBasedJAASConfiguration;
import org.csstudio.security.internal.SecurityContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.security.auth.ILoginContext;
import org.eclipse.equinox.security.auth.LoginContextFactory;

/** Eclipse Job for performing Log-in
 * 
 *  <p>Uses Eclipse {@link ILoginContext} to perform
 *  a JAAS-based login.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LoginJob extends Job
{
    final private String jaas_name;
    final private CallbackHandler callback;
    final private boolean get_current_user;

    /** Create an (unattended) login job that fetches
     *  the current user.
     *  
     *  Depending on the OS, this requires an entry "windows"
     *  or "unix" in the JAAS configuration.
     *  
     *  See default jaas.conf for example
     *  
     *  @return LoginJob
     */
    public static LoginJob forCurrentUser()
    {
        final String method =
                System.getProperty("os.name").contains("indow")
                ? "windows"
                : "unix";
         return new LoginJob(method, new UnattendedCallbackHandler(), true);
    }
    
    /** Initialize login for JAAS config set in preferences
     *  @param callback {@link CallbackHandler} for name, password, errors and "OK" when done
     */
    public LoginJob(final CallbackHandler callback)
    {
        this(SecurityPreferences.getJaasConfigName(), callback);
    }

    /** Initialize
     *  @param jaas_name JAAS config name to use for log in
     *  @param callback {@link CallbackHandler} for name, password, errors and "OK" when done
     */
    public LoginJob(final String jaas_name, final CallbackHandler callback)
    {
        this(jaas_name, callback, false);
    }
    
    /** Initialize
     *  @param jaas_name JAAS config name to use for log in
     *  @param callback {@link CallbackHandler} for name, password, errors and "OK" when done
     *  @param get_current_user Get the current, OS-authenticated user?
     */
    private LoginJob(final String jaas_name, final CallbackHandler callback,
            final boolean get_current_user)
    {
        super("Log in");
        this.jaas_name = jaas_name;
        this.callback = callback;
        this.get_current_user = get_current_user;
    }
    
    /** Log user out */
    public static void logout()
    {
        SecurityContext.getInstance().setSubject(null, true);
    }

    /** {@inheritDoc} */
    @Override
    protected IStatus run(final IProgressMonitor monitor)
    {
        final Logger logger = Logger.getLogger(getClass().getName());
        try
        {
        	final String jaas_config = SecurityPreferences.getJaasConfig();
        	if (jaas_config != null  &&  !jaas_config.isEmpty())
        	{	// Use complete configuration from preferences
        		Configuration.setConfiguration(new PreferenceBasedJAASConfiguration(jaas_config));
        	}
        	// Get JAAS file and config name in any case.
        	// Will actually be ignored if we set a complete preference-based config
        	final URL jaas_file = new URL(SecurityPreferences.getJaasConfigFile());
            final ILoginContext login = LoginContextFactory.createContext(jaas_name, jaas_file, callback);
            login.login();
                        
            final Subject subject = login.getSubject();
            logger.log(Level.FINE, "Logged in as {0}", subject);
            SecurityContext.getInstance().setSubject(subject, get_current_user);
            
            // Signal 'OK'
            callback.handle(new Callback[]
            { 
                new TextOutputCallback(TextOutputCallback.INFORMATION, "OK")
            });
        }
        catch (Exception ex)
        {
            logger.log(Level.WARNING, "Log in error", ex);
            final String message;
            if (ex.getCause() != null)
                message = ex.getMessage() + ": " + ex.getCause().getMessage();
            else
                message = ex.getMessage();
            try
            {
                callback.handle(new Callback[]
                { 
                    new TextOutputCallback(TextOutputCallback.ERROR, message)
                });
            }
            catch (Exception e)
            {
                logger.log(Level.WARNING, "Log in callback error", e);
            }
        }
        return Status.OK_STATUS;
    }

    /** {@inheritDoc} */
    @Override
    protected void canceling()
    {
        // See no other way to abort
        getThread().interrupt();
    }
}
