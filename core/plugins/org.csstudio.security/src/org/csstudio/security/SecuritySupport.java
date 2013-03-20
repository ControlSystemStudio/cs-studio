/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security;

import javax.security.auth.Subject;

import org.csstudio.security.authorization.Authorizations;
import org.csstudio.security.authorization.FileBasedAuthorizationProvider;
import org.csstudio.security.internal.SecurityContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/** Plugin activator,
 *  API entry point for obtaining authentication and authorization info
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SecuritySupport implements BundleActivator
{
    /** Plugin ID defined in MANIFEST.MF */
    public static String ID = "org.csstudio.security";
    
    private static SecurityContext security = null;

    /** {@inheritDoc} */
    @Override
    public void start(final BundleContext context) throws Exception
    {
        security = SecurityContext.getInstance();
        
        // TODO Obtain Authorization implementation from extension point
        security.setAuthorizationProvider(new FileBasedAuthorizationProvider());
    }

    /** {@inheritDoc} */
    @Override
    public void stop(final BundleContext context) throws Exception
    {
        security = null;
    }

    /** @param listener Listener to add */
    public static void addListener(final SecurityListener listener)
    {
        security.addListener(listener);
    }
    
    /** @param listener Listener to remove */
    public static void removeListener(final SecurityListener listener)
    {
        security.removeListener(listener);
    }

    /** @return Currently logged-in Subject or <code>null</code> */
    public static Subject getSubject()
    {
        return security.getSubject();
    }

    /** @return {@link Authorizations} of the currently logged-in Subject, or <code>null</code> */
    public static Authorizations getAuthorizations()
    {
        return security.getAuthorizations();
    }
    
    /** Check if user may do something
     * 
     *  @param authorization Authorization to check
     *  @return <code>true</code> if user has authorization
     */
    public static boolean havePermission(final String authorization)
    {
        return security.havePermission(authorization);
    }
}
