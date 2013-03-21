/*******************************************************************************
 * Copyright (c) 2008,2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.authorization;

import java.security.Principal;
import java.util.Set;

import javax.security.auth.Subject;

import com.sun.security.auth.NTUserPrincipal;
import com.sun.security.auth.UnixPrincipal;
import com.sun.security.auth.UserPrincipal;

/** Helper for implementing an AuthorizationProvider
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AuthorizationProviderTool
{
    /** A Subject can have multiple Principals.
     * 
     *  <p>Attempt to determine the 'primary' Principal
     *  @param user Subject that describes user
     *  @return Primary user name
     *  @throws Exception if name cannot be determined
     */
    public static String getSubjectName(final Subject user) throws Exception
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
        throw new Exception("Cannot determine name for " + user);
    }
}
