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
 *
 *  <p>To define a new authorization mechanism:
 *  <ol>
 *  <li>Implement this interface, for example as <code>MyAuthorizationProvider</code>.
 *  <li>Register <code>MyAuthorizationProvider</code> as an OSGi service for
 *      class <code>AuthorizationProvider.class</code>, name it for example <code>MyAuth</code>.
 *      Refer to examples in this plugin which register via declarative services.
 *  <li>Set the preference
 *      <code>org.csstudio.security/authorization_provider=MyAuth</code>
 *  </ol>
 *  @author Kay Kasemir
 */
public interface AuthorizationProvider
{
    /** Obtain authorizations for a user
     *  @param user JAAS {@link Subject} that describes the user
     *  @return {@link Authorizations} held by this user
     *  @throws Exception on error
     */
    public Authorizations getAuthorizations(final Subject user) throws Exception;
}
