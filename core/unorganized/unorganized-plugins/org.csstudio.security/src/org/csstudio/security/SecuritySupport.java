/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security;

import java.security.Principal;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;

import org.csstudio.security.authorization.AuthorizationProvider;
import org.csstudio.security.authorization.Authorizations;
import org.csstudio.security.internal.SecurityContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.sun.security.auth.NTUserPrincipal;
import com.sun.security.auth.UnixPrincipal;
import com.sun.security.auth.UserPrincipal;

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
   
    /** Singleton {@link SecurityContext} */
    private static SecurityContext security = null;

    /** {@inheritDoc} */
    @Override
    public void start(final BundleContext context) throws Exception
    {
    	final Logger logger = Logger.getLogger(getClass().getName());

    	// Fetch (i.e. create) the SecurityContext
        security = SecurityContext.getInstance();

        // Obtain AuthorizationProvider from OSGi service.
        // Could 'inject' into the SecurityContext via DeclarativeServices
        // if there was just one service implementation,
        // but want to filter on a specific AuthorizationProvider based on preference setting.
        final String authorization_name = SecurityPreferences.getAuthorizationProvider();
        final String filter = "(component.name=" + authorization_name + ")";
        // Expect exactly one with that name
        final Collection<ServiceReference<AuthorizationProvider>> authorization_services =
    		context.getServiceReferences(AuthorizationProvider.class, filter);
    	if (authorization_services.size() == 1)
    	{
    		final ServiceReference<AuthorizationProvider> service =
    				authorization_services.iterator().next();
    		security.setAuthorizationProvider(context.getService(service));
    	}
    	else
        	logger.warning(
    			"Expected 1 authorization provider, found " + authorization_services.size() +
    			"\nList available providers on OSGi console via" +
    			"services (objectClass=org.csstudio.security.authorization.AuthorizationProvider)");
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

    /** @return Is it the current, OS-authenticated user?
     *          Or is it a different user, authenticated via name/password?
     */
    public static boolean isCurrentUser()
    {
        return security.isCurrentUser();
    }
    
    /** A Subject can have multiple Principals.
     * 
     *  <p>Attempt to determine the 'primary' Principal
     *  @param user Subject that describes user
     *  @return Primary user name
     */
    public static String getSubjectName(final Subject user)
    {
        final Set<Principal> principals = user.getPrincipals();
        for (Principal principal : principals)
        {
            // If there's only one, use that
            if (principals.size() == 1)
                return principal.getName();
            
            // Try to identify the 'primary' one.
            // Not UnixNumericUserPrincipal, ..
            // but the one that has the actual name.
            if (principal instanceof UnixPrincipal  ||
                principal instanceof UserPrincipal  ||
                principal instanceof NTUserPrincipal)
                return principal.getName();
        }
        Logger.getLogger(SecuritySupport.class.getName())
            .log(Level.WARNING, "Cannot determine name for {0}", user);
        return user.toString();
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
