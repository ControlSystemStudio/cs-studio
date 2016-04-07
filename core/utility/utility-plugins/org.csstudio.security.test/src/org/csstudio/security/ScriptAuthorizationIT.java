/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;

import javax.security.auth.Subject;

import org.csstudio.security.authorization.AuthorizationProvider;
import org.csstudio.security.authorization.Authorizations;
import org.csstudio.security.authorization.ScriptAuthorizationProvider;
import org.junit.Test;

import com.sun.security.auth.UserPrincipal;

/** JUnit demo of the {@link ScriptAuthorizationProvider}
 *
 *  <p>Depends on specific user and groups.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScriptAuthorizationIT
{
    @Test
    public void testFileBasedAuthorization() throws Exception
    {
        final AuthorizationProvider auth =
            new ScriptAuthorizationProvider(new File("id_auth").getAbsolutePath());

        Subject user = new Subject();
        user.getPrincipals().add(new UserPrincipal("ky9"));
        Authorizations authorizations = auth.getAuthorizations(user);
        System.out.println(user);
        System.out.println(authorizations);
        assertThat(authorizations.haveAuthorization("netaccounts"), equalTo(true));
        assertThat(authorizations.haveAuthorization("whatever"), equalTo(false));
    }
}
