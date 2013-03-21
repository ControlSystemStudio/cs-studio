/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.authorization;

import javax.security.auth.Subject;

/** Interface of service that determines {@link Authorizations} of a user ({@link Subject})
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public interface AuthorizationProvider
{
    /** ID of extension point for adding implementations */
    public static String EXT_ID = "org.csstudio.security.authorization";
    
    /** Obtain authorizations for a user
     *  @param user JAAS {@link Subject} that describes the user
     *  @return {@link Authorizations} held by this user
     *  @throws Exception on error
     */
    public Authorizations getAuthorizations(final Subject user) throws Exception;
}
