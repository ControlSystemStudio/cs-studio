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
import static org.junit.Assert.assertTrue;

import java.security.Principal;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

import org.csstudio.security.authentication.UnattendedCallbackHandler;
import org.junit.Before;
import org.junit.Test;

import com.sun.security.auth.callback.TextCallbackHandler;

/** Console (text-based) JUnit demo of various login methods
 *
 *  <p>Does not require Plug-In test
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LoginDemo
{
    final private static String USER_NAME = "fred";

    @Before
    public void setup()
    {
        System.setProperty("java.security.auth.login.config", "jaas.conf");
    }

    /** Should work on all Linux (Mac OS) systems */
    @Test
    public void systemDemo() throws Exception
    {
        // Use UnattendedCallbackHandler without name, password,
        // because that should not be required
        final String jaas_config =
            System.getProperty("os.name").contains("Windows")
            ? "windows"
            : "unix";
        final LoginContext login =
            new LoginContext(jaas_config, new UnattendedCallbackHandler());
        login.login();
        final Subject subject = login.getSubject();

        System.out.println(subject);
        final String current_user = System.getProperty("user.name");
        boolean got_user = false;
        for (Principal p : subject.getPrincipals())
            if (p.getName().equals(current_user))
                got_user = true;

        assertTrue(subject.getPrincipals().size() >= 1);
        assertThat(got_user, equalTo(true));

        System.out.println("Primary user name: " +
                SecuritySupport.getSubjectName(subject));
    }

    /** Should work as long as passwords.conf isn't changed */
    @Test
    public void fileDemo() throws Exception
    {
        final LoginContext login = new LoginContext("file",
                new UnattendedCallbackHandler(USER_NAME, "$fred"));
        login.login();
        final Subject subject = login.getSubject();

        System.out.println(subject);
        boolean got_user = false;
        for (Principal p : subject.getPrincipals())
            if (p.getName().equals(USER_NAME))
                got_user = true;

        // FileLoginModule should return exactly one Principal named "fred"
        assertThat(got_user, equalTo(true));
        assertThat(subject.getPrincipals().size(), equalTo(1));
        assertThat(subject.getPrincipals().iterator().next().getName(), equalTo("fred"));
    }

    /** Only works for SNS LDAP, requires correct name/password */
    @Test
    public void SNS_LDAP_Demo() throws Exception
    {
        final LoginContext login = new LoginContext("SNS_LDAP", new TextCallbackHandler());
        login.login();
        final Subject subject = login.getSubject();
        System.out.println(subject);
        assertTrue(subject.getPrincipals().size() > 0);
        System.out.println("Subject name: " + SecuritySupport.getSubjectName(subject));
    }
}
