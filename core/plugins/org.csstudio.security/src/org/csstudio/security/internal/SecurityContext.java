/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;

import org.csstudio.security.SecurityListener;
import org.csstudio.security.authorization.AuthorizationProvider;
import org.csstudio.security.authorization.Authorizations;

/** Security context,
 *  maintains current authentication and authorization info
 *  and notifies listeners on change.
 * 
 *  <p>The plugin activator fetches the initial instance,
 *  i.e. creates this, and the activator also sets the
 *  AuthorizationProvider based on preferences and
 *  extension points.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SecurityContext
{
    final private static SecurityContext instance = new SecurityContext();
    final private List<SecurityListener> listeners = new CopyOnWriteArrayList<>();

    /** Subject that describes the authenticated user */
    private Subject subject = null;
    
    /** Is it the current, OS-authenticated user? */
    private boolean is_current_user = false;
    
    private AuthorizationProvider authorization_provider = null;
    private Authorizations authorizations;
    
    /** @return Singleton instance */
    public static SecurityContext getInstance()
    {
        return instance;
    }
    
    /** Define authorization provider
     *  @param authorization
     */
    public synchronized void setAuthorizationProvider(final AuthorizationProvider authorization)
    {
        this.authorization_provider = authorization;
    }

    /** @param listener Listener to add */
    public void addListener(final SecurityListener listener)
    {
        listeners.add(listener);
    }
    
    /** @param listener Listener to remove */
    public void removeListener(final SecurityListener listener)
    {
        listeners.remove(listener);
    }

    /** @return Currently logged-in Subject or <code>null</code> */
    public synchronized Subject getSubject()
    {
        return subject;
    }

    /** @return Is it the current, OS-authenticated user? */
    public synchronized boolean isCurrentUser()
    {
        return is_current_user;
    }
    
    /** @param subject Currently logged-in Subject or <code>null</code>
     *  @param is_current_user Is it the current, OS-authenticated user?
     */
    public void setSubject(final Subject subject, final boolean is_current_user)
    {
        Authorizations authorizations = null;
        if (subject != null  &&  authorization_provider != null)
        {
            try
            {
                authorizations = authorization_provider.getAuthorizations(subject);
            }
            catch (Exception ex)
            {
                Logger.getLogger(getClass().getName())
                    .log(Level.WARNING, "Cannot obtain authorizations", ex);
            }
        }
        synchronized (this)
        {   // Lock only briefly for update
            this.subject = subject;
            this.is_current_user = is_current_user;
            this.authorizations = authorizations;
        }
        for (SecurityListener listener : listeners)
            listener.changedSecurity(subject, is_current_user, authorizations);
    }

    /** @return {@link Authorizations} of the currently logged-in Subject, or <code>null</code> */
    public synchronized Authorizations getAuthorizations()
    {
        return authorizations;
    }
    
    /** Check if user may do something
     * 
     *  @param authorization Authorization to check
     *  @return <code>true</code> if user has authorization
     */
    public synchronized boolean havePermission(final String authorization)
    {
        return authorizations != null  &&  authorizations.haveAuthorization(authorization);
    }
}   
