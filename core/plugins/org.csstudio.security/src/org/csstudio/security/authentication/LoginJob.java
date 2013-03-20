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

import org.csstudio.security.SecurityPreferences;
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
    final private CallbackHandler callback;

    /** Initialize
     *  @param callback {@link CallbackHandler} for name, password, errors and "OK" when done
     */
    public LoginJob(final CallbackHandler callback)
    {
        super("Log in");
        this.callback = callback;
    }

    /** Log user out */
    public static void logout()
    {
        SecurityContext.getInstance().setSubject(null);
    }

    /** {@inheritDoc} */
    @Override
    protected IStatus run(final IProgressMonitor monitor)
    {
        final Logger logger = Logger.getLogger(getClass().getName());
        try
        {
            final String jaas_name = SecurityPreferences.getConfigName();
            final URL jaas_file = new URL(SecurityPreferences.getConfigFile());
            final ILoginContext login = LoginContextFactory.createContext(jaas_name, jaas_file, callback);
            login.login();
            
            final Subject subject = login.getSubject();
            logger.log(Level.FINE, "Logged in as {0}", subject);
            SecurityContext.getInstance().setSubject(subject);
            
            // Signal 'OK'
            callback.handle(new Callback[]
            { 
                new TextOutputCallback(TextOutputCallback.INFORMATION, "OK")
            });
        }
        catch (Exception ex)
        {
            logout();
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
