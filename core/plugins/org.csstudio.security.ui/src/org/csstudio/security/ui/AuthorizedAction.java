/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.ui;

import javax.security.auth.Subject;

import org.csstudio.security.SecurityListener;
import org.csstudio.security.SecuritySupport;
import org.csstudio.security.authorization.Authorizations;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

/** Eclipse Action that requires authorization.
 * 
 *  <p>Connects to {@link SecuritySupport} and enables/disables
 *  this action depending on current {@link Authorizations}.
 *  
 *  <p>Can be used like standard {@link Action} except that it
 *  needs to be <code>dispose</code>d
 *  
 *  @see #dispose()
 *  @author Kay Kasemir
 */
public class AuthorizedAction extends Action implements SecurityListener
{
    final private String authorization;

    /** Initialize
     *  @param name Name (title) of the action
     *  @param authorization Required authorization 
     */
    public AuthorizedAction(final String name, final String authorization)
    {
        this(name, null, authorization);
    }

    /** Initialize
     *  @param name Name (title) of the action
     *  @param icon Icon
     *  @param authorization Required authorization 
     */
    public AuthorizedAction(final String name, final ImageDescriptor icon, final String authorization)
    {
        super(name);
        if (icon != null)
            setImageDescriptor(icon);
        this.authorization = authorization;
        
        // Subscribe to updates, set initial enable/disable
        SecuritySupport.addListener(this);
        setEnabled(SecuritySupport.havePermission(authorization));
    }

    /** Dispose the action when it is no longer needed so that
     *  it can unregister from SecuritySupport.
     */
    public void dispose()
    {
        SecuritySupport.removeListener(this);
    }
    
    /** {@inheritDoc} */
    @Override
    public void changedSecurity(final Subject subject, final Authorizations authorizations)
    {
        setEnabled(SecuritySupport.havePermission(authorization));
    }
}
