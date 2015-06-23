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

import javax.security.auth.Subject;

import org.csstudio.security.authorization.AuthorizationProvider;
import org.csstudio.security.authorization.Authorizations;
import org.csstudio.security.authorization.FileBasedAuthorizationProvider;
import org.junit.Test;

import com.sun.security.auth.UserPrincipal;

/** JUnit test of the {@link FileBasedAuthorizationProvider}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FileBasedAuthorizationUnitTest
{
    @Test
    public void testFileBasedAuthorization() throws Exception
    {
        final AuthorizationProvider auth = new FileBasedAuthorizationProvider("../org.csstudio.security/authorization.conf");

        // "fred" is listed for full alarm access
        Subject user = new Subject();
        user.getPrincipals().add(new UserPrincipal("fred"));
        Authorizations authorizations = auth.getAuthorizations(user);
        System.out.println(user);
        System.out.println(authorizations);
        assertThat(authorizations.haveAuthorization("alarm_ack"), equalTo(true));
        assertThat(authorizations.haveAuthorization("alarm_config"), equalTo(true));

        // "linux-admin" is one of the ".*-admin". May do anything
        user = new Subject();
        user.getPrincipals().add(new UserPrincipal("linux-admin"));
        authorizations = auth.getAuthorizations(user);
        System.out.println(user);
        System.out.println(authorizations);
        assertThat(authorizations.haveAuthorization("alarm_config"), equalTo(true));
        assertThat(authorizations.haveAuthorization("unspeakable-stuff"), equalTo(true));

        // "Egon" can ack' (because anybody may do that), but not config
        user = new Subject();
        user.getPrincipals().add(new UserPrincipal("Egon"));
        authorizations = auth.getAuthorizations(user);
        System.out.println(user);
        System.out.println(authorizations);
        assertThat(authorizations.haveAuthorization("alarm_ack"), equalTo(true));
        assertThat(authorizations.haveAuthorization("alarm_config"), equalTo(false));
    }
}
