/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;

import org.csstudio.security.internal.PreferenceBasedJAASConfiguration;
import org.junit.Test;

/** JUnit test of {@link PreferenceBasedJAASConfiguration}
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PreferenceBasedJAASConfigurationUnitTest
{
    @Test
    public void testParser() throws Exception
    {
        final Logger logger = Logger.getLogger("");
        logger.setLevel(Level.ALL);
        for (Handler handler : logger.getHandlers())
            handler.setLevel(Level.ALL);
        final String jaas_config =
            "com.sun.jmx.remote.security.FileLoginModule Sufficient debug=\"true\" passwordFile=\"passwords.conf\";" +
            "com.sun.security.auth.module.JndiLoginModule Sufficient debug=\"true\" user.provider.url=\"ldap://localhost:389/ou=People,dc=test,dc=ics\" group.provider.url=\"ldap://localhost:389/ou=People,dc=test,dc=ics\";";
        final Configuration configuration = new PreferenceBasedJAASConfiguration(jaas_config);
        final AppConfigurationEntry[] configs = configuration.getAppConfigurationEntry("ignored");

        assertThat(configs.length, equalTo(2));
        assertThat(configs[0].getControlFlag(), equalTo(LoginModuleControlFlag.SUFFICIENT));
        assertThat(configs[0].getLoginModuleName(), equalTo("com.sun.jmx.remote.security.FileLoginModule"));
        assertThat(configs[0].getOptions().get("passwordFile").toString(), equalTo("passwords.conf"));
    }
}
