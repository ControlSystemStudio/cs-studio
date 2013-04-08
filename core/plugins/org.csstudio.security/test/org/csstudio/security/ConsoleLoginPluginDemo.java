/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CountDownLatch;

import javax.security.auth.Subject;

import org.csstudio.security.authentication.LoginJob;
import org.csstudio.security.authorization.Authorizations;
import org.junit.Test;

import com.sun.security.auth.callback.TextCallbackHandler;

/** Console (text-based) demo of logging in
 *
 *  <p>Must run as Plug-In JUnit test, where preferences determine
 *  the JAAS config file, JAAS config name, authorization provider.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ConsoleLoginPluginDemo implements SecurityListener
{
    final private CountDownLatch updates = new CountDownLatch(1);
    
    @Override
    public void changedSecurity(final Subject subject,
            final boolean is_current_user, final Authorizations authorizations)
    {
        System.out.println("Security Settings changed: ");
        System.out.println(subject);
        System.out.println(authorizations);
        updates.countDown();
    }

    @Test
    public void loginDemo() throws Exception
    {
        SecuritySupport.addListener(this);
        final LoginJob login = new LoginJob(new TextCallbackHandler());
        login.schedule();
        
        updates.await();
        
        SecuritySupport.removeListener(this);
        
        assertThat(SecuritySupport.getSubject(), not(nullValue()));
    }
}
