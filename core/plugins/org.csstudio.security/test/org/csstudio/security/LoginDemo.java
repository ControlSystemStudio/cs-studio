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
import static org.csstudio.utility.test.HamcrestMatchers.*;

import java.io.IOException;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;

import org.junit.Test;
import org.junit.Before;

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
    
    private boolean asked_for_user = false;
    private boolean asked_for_pass = false;
    
    private class TestCallbackHandler implements CallbackHandler
    {
        @Override
        public void handle(final Callback[] callbacks) throws IOException,
                UnsupportedCallbackException
        {
            for (Callback callback : callbacks)
            {
                if (callback instanceof NameCallback)
                {
                    final NameCallback nc = (NameCallback) callback;
                    System.out.println("Providing user...");
                    asked_for_user = true;
                    nc.setName(USER_NAME);
                }
                else if (callback instanceof PasswordCallback)
                {
                    final PasswordCallback nc = (PasswordCallback) callback;
                    System.out.println("Providing password...");
                    asked_for_pass = true;
                    nc.setPassword("$fred".toCharArray());
                }
            }
        }
    };
    
    
    @Before
    public void setup()
    {
        System.setProperty("java.security.auth.login.config", "jaas.conf");
    }
    
    /** Should work on all Linux (Mac OS) systems */
    @Test
    public void systemDemo() throws Exception
    {
        
        final LoginContext login = new LoginContext("unix", new TestCallbackHandler());
        login.login();
        final Subject subject = login.getSubject();
        
        System.out.println(subject);
        final String current_user = System.getProperty("user.name");
        boolean got_user = false;
        for (Principal p : subject.getPrincipals())
            if (p.getName().equals(current_user))
                got_user = true;
        
        // Should not ask for user, password
        assertThat(asked_for_user, equalTo(false));
        assertThat(asked_for_pass, equalTo(false));
        
        assertThat(subject.getPrincipals().size(), greaterThanOrEqualTo(1));
        assertThat(got_user, equalTo(true));
    }

    
    /** Should work as long as passwords.conf isn't changed */
    @Test
    public void fileDemo() throws Exception
    {
        
        final LoginContext login = new LoginContext("file", new TestCallbackHandler());
        login.login();
        final Subject subject = login.getSubject();
        
        System.out.println(subject);
        boolean got_user = false;
        for (Principal p : subject.getPrincipals())
            if (p.getName().equals(USER_NAME))
                got_user = true;
        
        // Should have asked for user, password
        assertThat(asked_for_user, equalTo(true));
        assertThat(asked_for_pass, equalTo(true));
        
        // FileLoginModule should return exactly one Principal named "fred"
        assertThat(got_user, equalTo(true));
        assertThat(subject.getPrincipals().size(), equalTo(1));
        assertThat(subject.getPrincipals().iterator().next().getName(), equalTo("fred"));
    }

    /** Only works for SNS UCAMS, requires correct name/passwor */
    @Test
    public void UCAMS_Demo() throws Exception
    {
        
        final LoginContext login = new LoginContext("Plain_JAAS_SNS_UCAMS", new TextCallbackHandler());
        login.login();
        final Subject subject = login.getSubject();
        
        System.out.println(subject);
        assertThat(subject.getPrincipals().size(), greaterThan(0));
    }
}
