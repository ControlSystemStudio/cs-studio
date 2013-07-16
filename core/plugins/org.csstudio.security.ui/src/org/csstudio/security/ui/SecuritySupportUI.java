/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.ui;

import javax.security.auth.Subject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.csstudio.security.SecurityListener;
import org.csstudio.security.SecuritySupport;
import org.csstudio.security.authorization.Authorizations;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/** UI support for security.
 * 
 *  <p>Actions can register to be enabled/disabled based on authorization.
 *  
 *  @see #registerAction(IAction, String)
 *  
 *  @author Kay Kasemir
 */
public class SecuritySupportUI implements BundleActivator, SecurityListener
{
    private static final String RUNTIME_PLATFORM = "org.csstudio.runtime.platform"; //$NON-NLS-1$
	/** Map of authorizations and actions that require them */
    final private static Map<String, Collection<WeakReference<IAction>>> authorized_actions = new HashMap<>();

    /** {@inheritDoc} */
    @Override
    public void start(final BundleContext context) throws Exception
    {
        SecuritySupport.addListener(this);
        System.setProperty(RUNTIME_PLATFORM,
        		SWT.getPlatform().startsWith("rap")?"rap":"rcp"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    }

    /** {@inheritDoc} */
    @Override
    public void stop(final BundleContext context) throws Exception
    {
        SecuritySupport.removeListener(this);
    }
    
    /** Register action to be enabled/disabled based on current authorization
     *  
     *  <p>Ideally, actions should be unregistered when this behavior is no
     *  longer required.
     *  Omitting the de-registration will "work" since a weak reference is used,
     *  but action may still be invoked a few times until it is garbage collected.
     *
     *  @param action Action that should be enabled/disabled
     *  @param authorization Authorization that is required to enable the action
     */
    public static void registerAction(final IAction action, final String authorization)
    {
        synchronized (authorized_actions)
        {
            Collection<WeakReference<IAction>> action_refs = authorized_actions.get(authorization);
            if (action_refs == null)
            {   // First action for this authorization: Create the list
                action_refs = new ArrayList<WeakReference<IAction>>();
                authorized_actions.put(authorization, action_refs);
            }
            action_refs.add(new WeakReference<IAction>(action));
        }

        // Initial update
        action.setEnabled(SecuritySupport.havePermission(authorization));
    }

    /** Remove action registration
     *  @param action Action that should no longer be enabled/disabled
     *  @param authorization Authorization that is no longer required
     */
    public static void unregisterAction(final IAction action, final String authorization)
    {
        synchronized (authorized_actions)
        {
            final Collection<WeakReference<IAction>> actions = authorized_actions.get(authorization);
            if (actions == null)
                return;
            final Iterator<WeakReference<IAction>> entries = actions.iterator();
            while (entries.hasNext())
            {
                final IAction reference = entries.next().get();
                // Remove action when found.
                // While looking at the list, also remove Garbage-collected actions
                if (reference == null  ||  reference == action)
                    entries.remove();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void changedSecurity(final Subject subject, final boolean is_current_user, final Authorizations authorizations)
    {
        synchronized (authorized_actions)
        {
            // Every action must be updated:
            // Loop over authorizations
            for (String authorization : authorized_actions.keySet())
            {
                // Is it enabled?
                final boolean enabled = authorizations != null  &&  authorizations.haveAuthorization(authorization);
                
                // Loop over actions that need to be updated for this authorization
                final Iterator<WeakReference<IAction>> action_refs = authorized_actions.get(authorization).iterator();
                while (action_refs.hasNext())
                {
                    final WeakReference<IAction> actionref = action_refs.next();
                    final IAction action = actionref.get();
                    if (action == null)
                        action_refs.remove(); // Forget garbage-collected action
                    else
                        action.setEnabled(enabled);
                }
            }
        }
    }
}
