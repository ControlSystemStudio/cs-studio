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

import org.csstudio.security.internal.InstallLocationPasswordProvider;
import org.csstudio.security.preferences.SecurePreferences;
import org.csstudio.security.preferences.SecurePreferences.Type;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.junit.Test;

/** JUnit Plug-in test of secure preferences
 * 
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SecurePreferencesHeadlessTest
{
    @Test
    public void listPasswordProviders() throws Exception
    {
        final IConfigurationElement[] extensions = Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.equinox.security.secureStorage");
        boolean found = false;
        for (IConfigurationElement extension : extensions)
        {
            final String clazz = extension.getAttribute("class");
            System.out.println(
                "Priority " + extension.getAttribute("priority") +
                ": " + clazz +
                " provided by " + extension.getContributor().getName()
                );
            if (clazz.equals(InstallLocationPasswordProvider.class.getName()))
                found = true;
        }
        assertThat(found, equalTo(true));
    }
    
    @Test
    public void testSecurePreferences() throws Exception
    {
    	final Type type = SecurePreferences.Type.Instance;
		ISecurePreferences prefs = SecurePreferences.getSecurePreferences(type);
        ISecurePreferences node = prefs.node(SecuritySupport.ID);
        
        // See if there's a value written by previous run
        System.out.println("Previous value: " + node.get("test_setting", "nothing"));
        
        // Write a value
        node.put("test_setting", "secret_value", true);
        prefs.flush();

        // Depending on there the secure store is located,
        // it might contain just the SecuritySupport.ID that
        // we just added, or several additional entries
        boolean found = false;
        for (String entry : prefs.childrenNames())
        {
            System.out.println("Secure preferences: " + entry);
            if (SecuritySupport.ID.equals(entry))
                found = true;
        }
        assertThat(found, equalTo(true));
        
        // Start over, read
		prefs = SecurePreferences.getSecurePreferences(type);
        node = prefs.node(SecuritySupport.ID);
        String value = node.get("test_setting", null);
        assertThat(value, equalTo("secret_value"));
    }
}
