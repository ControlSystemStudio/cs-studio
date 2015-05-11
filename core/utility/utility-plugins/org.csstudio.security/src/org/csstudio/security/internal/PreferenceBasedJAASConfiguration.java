/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;

import org.csstudio.java.string.StringSplitter;

/** JAAS Configuration based on Eclipse preferences
 *
 *  @author Kay Kasemir
 *  @author Xihui Chen - org.csstudio.platform.internal.jassauthentication.preference.PreferencesHelper
 */
@SuppressWarnings("nls")
public class PreferenceBasedJAASConfiguration extends Configuration
{
    final private AppConfigurationEntry[] configurations;

    public PreferenceBasedJAASConfiguration(final String jaas_config) throws Exception
    {
        final Logger logger = Logger.getLogger(getClass().getName());
        // Each login module is separated by ";"
        final String[] module_settings =
                StringSplitter.splitIgnoreInQuotes(jaas_config, ';', false);
        configurations = new AppConfigurationEntry[module_settings.length];
        for (int i=0; i<module_settings.length; ++i)
        {
            final String module_setting = module_settings[i];
            logger.fine(module_setting);

            final String settings[] = StringSplitter.splitIgnoreInQuotes(module_setting, ' ', true);
            if (settings.length < 2)
                throw new Exception("Expect at least 'ModuleName Flag', got " + module_setting);
            final String module_name = settings[0];
            final LoginModuleControlFlag flag = getFlag(settings[1]);

            logger.fine(module_name);
            logger.fine(flag.toString());

            final Map<String, String> options = new HashMap<>();
            for (int s=2; s<settings.length; ++s)
            {
                final String[] option_value = StringSplitter.splitIgnoreInQuotes(settings[s], '=', true);
                if (option_value.length != 2)
                    throw new Exception("Expecting 'option=\"value\"'");
                options.put(option_value[0], option_value[1]);
            }
            logger.fine(options.toString());

            configurations[i] = new AppConfigurationEntry(module_name, flag, options);
        }
    }

    /** @param flag Login module flag as text
     *  @return {@link LoginModuleControlFlag}
     *  @throws Exception on error
     */
    private static LoginModuleControlFlag getFlag(final String flag) throws Exception
    {
        if ("REQUIRED".equalsIgnoreCase(flag))
            return LoginModuleControlFlag.REQUIRED;
        if ("REQUISITE".equalsIgnoreCase(flag))
            return LoginModuleControlFlag.REQUISITE;
        if ("SUFFICIENT".equalsIgnoreCase(flag))
            return LoginModuleControlFlag.SUFFICIENT;
        if ("OPTIONAL".equalsIgnoreCase(flag))
            return LoginModuleControlFlag.OPTIONAL;
        throw new Exception("Invalid LoginModule flag " + flag);
    }

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(final String name)
    {
        // Use the currently logged-in user on Linux and Mac OS X?
        if ("unix".equals(name))
            return new AppConfigurationEntry[]
            {
                new AppConfigurationEntry("com.sun.security.auth.module.UnixLoginModule", LoginModuleControlFlag.REQUIRED, Collections.<String,String>emptyMap())
            };
        else if ("windows".equals(name))
            return new AppConfigurationEntry[]
            {
                new AppConfigurationEntry("com.sun.security.auth.module.NTLoginModule", LoginModuleControlFlag.REQUIRED, Collections.<String,String>emptyMap())
            };

        // Else: Ignore the name.
        // Not using a JAAS config file with named entries, but
        // received the configurations to use in a 'jaas_config' preference.
        return configurations;
    }
}
