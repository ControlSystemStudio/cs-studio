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

/** Listener to security events
 *  @author Kay Kasemir
 */
public interface SecurityListener
{
    /** Security settings have changed.
     *
     *  <p>User has logged in or out,
     *  permissions have changed.
     *
     *  <p>May be invoked from in non-GUI thread.
     *
     *  @param subject {@link Subject} for current user or <code>null</code> when not logged in
     *  @param is_current_user Is it the current, OS-authenticated user?
     *                         Or is it a different user, authenticated via name/password?
     *  @param authorizations {@link Authorizations} held by the user, or <code>null</code>
     */
    public void changedSecurity(Subject subject,
            boolean is_current_user, Authorizations authorizations);
}
