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
public class SecurityContext
{
    final private static SecurityContext instance = new SecurityContext();
    final private List<SecurityListener> listeners = new CopyOnWriteArrayList<>();

    private Subject subject = null;
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
    
    /** @param subject Currently logged-in Subject or <code>null</code> */
    public void setSubject(final Subject subject)
    {
        synchronized (this)
        {
            this.subject = subject;
            if (subject != null  &&  authorization_provider != null)
                authorizations = authorization_provider.getAuthorizations(subject);
            else
                authorizations = null;
        }
        for (SecurityListener listener : listeners)
            listener.changedSecurity(subject, authorizations);
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
        return authorizations != null  &&  authorizations.havePermission(authorization);
    }
}   
