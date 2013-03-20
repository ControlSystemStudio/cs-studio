/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.authorization;

import java.util.Collections;
import java.util.Set;

import javax.security.auth.Subject;

/** Authorizations that a user has
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Authorizations
{
    /** Special authorization that allows subject to do anything */
    final public static String FULL = "FULL";
    
    /** What the user is allowed to do */
    final private Set<String> authorizations;
    
    /** Initialize
     *  @param permissions Permissions that make 
     */
    public Authorizations(final Set<String> authorizations)
    {
        this.authorizations = Collections.unmodifiableSet(authorizations);
    }
    
    /** Obtain permissions for a user
     *  @param user JAAS {@link Subject} that describes the user
     *  @return Permissions held by this user
     */
    public Set<String> getAuthorizations()
    {
        return authorizations;
    }
    
    /** Check if user may do something
     * 
     *  @param authorization Authorization to check
     *  @return <code>true</code> if user has authorization
     */
    public boolean havePermission(final String authorization)
    {
        return authorizations.contains(authorization)  ||  authorizations.contains(FULL);
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        return "Authorizations: " + authorizations;
    }
}
