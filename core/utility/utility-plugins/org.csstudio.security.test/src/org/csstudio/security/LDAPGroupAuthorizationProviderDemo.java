/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;

import org.csstudio.security.authorization.AuthorizationProvider;
import org.csstudio.security.authorization.Authorizations;
import org.csstudio.security.authorization.LDAPGroupAuthorizationProvider;
import org.junit.Test;

import com.sun.security.auth.UserPrincipal;

/** Demo of {@link LDAPGroupAuthorizationProvider}
 *
 *  <p>Requires LDAP server with specific entries.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LDAPGroupAuthorizationProviderDemo
{
    @Test
    public void demoLDAPGroupAuthorization() throws Exception
    {
        // Enable full logging
        Logger.getLogger("").setLevel(Level.ALL);
        for (Handler handler : Logger.getLogger("").getHandlers())
            handler.setLevel(Level.ALL);
        
        final String url = "ldap://localhost/dc=test,dc=ics";
        final String group_base = "ou=Group";
        
        final AuthorizationProvider ldap =
            new LDAPGroupAuthorizationProvider(url, group_base);
        
        final Subject subject = new Subject();
        subject.getPrincipals().add(new UserPrincipal("fred"));
        final Authorizations authorizations = ldap.getAuthorizations(subject);
        
        assertThat(authorizations, not(nullValue()));
        assertThat(authorizations.haveAuthorization("archive_config"), equalTo(true));
    }
}
