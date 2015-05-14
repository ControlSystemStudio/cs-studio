/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.preferences;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.security.SecurityPreferences;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;

/** Wrapper for Eclipse {@link ISecurePreferences}
 *
 *  <p>Helps with obtaining secure preferences in
 *  various locations
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SecurePreferences
{
    /** File name for instance or install option */
    final public static String FILENAME = "secure_store.dat";

    /** Where {@link SecurePreferences} should be located */
    public enum Type
    {
        /** Use the default location, which is typically
         *  the user's home directory.
         *  @see SecurePreferencesFactory#getDefault()
         *
         *  <p>Advantage: It's the default
         *  <p>Disadvantage: You won't always know
         *  where the preferences are.
         */
        Default,

        /** Use the Eclipse 'instance', i.e. the workspace.
         *
         * <p>Advantage: You know where it is, and each workspace
         * will have its own settings.
         * <p>Disadvantage: Each workspace has different settings.
         */
        Instance,

        /** Use the Eclipse 'install' location, i.e. where the product is installed.
         *
         * <p>Advantage: You know where it is, and every workspace for that
         * product will have the same settings. Good for a "global" setup.
         * <p>Disadvantage: Ordinary users cannot (should not) have write permissions.
         */
        Install
    }

    /** Prevent instantiation */
    private SecurePreferences()
    {
        // NOP
    }

    /** Obtain secure preferences based on settings of this plugin
     *  @return ISecurePreferences
     *  @throws IOException on error
     */
    public static ISecurePreferences getSecurePreferences() throws IOException
    {
        return getSecurePreferences(SecurityPreferences.getSecurePreferenceLocation());
    }

    /** Obtain secure preferences
     *  @param type Where they should be located
     *  @return ISecurePreferences
     *  @throws IOException on error
     */
    public static ISecurePreferences getSecurePreferences(final Type type) throws IOException
    {
        switch (type)
        {
        case Instance:
            return SecurePreferencesFactory.open(
                    Platform.getInstanceLocation().getDataArea(FILENAME),
                    null);
        case Install:
            return SecurePreferencesFactory.open(
                    Platform.getInstallLocation().getDataArea(FILENAME),
                    null);
        default:
            return SecurePreferencesFactory.getDefault();
        }
    }

    /** Read preference setting from secure storage, falling back to normal preferences
     *  @param plugin_id Plugin that holds the preferences
     *  @param key Preference key
     *  @param default_value Default value
     *  @return Preference setting
     */
    public static String get(final String plugin_id, final String key, final String default_value)
    {
        try
        {
            // Check secure preferences
            final String value = getSecurePreferences().node(plugin_id).get(key, null);
            if (value != null)
                return value;
            // Fall back to normal preferences
            final IPreferencesService service = Platform.getPreferencesService();
            if (service != null)
                return service.getString(plugin_id, key, default_value, null);
        }
        catch (Exception ex)
        {
            Logger.getLogger(SecurePreferences.class.getName())
                .log(Level.WARNING, "Cannot read " + plugin_id + "/" + key, ex);
        }
        // Give up
        return default_value;
    }

    /** Set preference setting in secure storage
     *  @param plugin_id Plugin that holds the preferences
     *  @param key Preference key
     *  @param value Value to write
     *  @throws Exception on error
     */
    public static void set(final String plugin_id, final String key, final String value) throws Exception
    {
        final ISecurePreferences prefs = getSecurePreferences();
        prefs.node(plugin_id).put(key, value, true);
        prefs.flush();
    }

    /** Set preference setting in secure storage
     *  @param setting Path to setting, must be "plugin_id/key"
     *  @param value Value to write
     *  @throws Exception on error
     */
    public static void set(final String setting, final String value) throws Exception
    {
        final int sep = setting.indexOf("/");
        if (sep < 0)
            throw new Exception("Expecting 'plugin_id/key', got " + setting);
        final String plugin_id = setting.substring(0, sep);
        final String key = setting.substring(sep + 1);
        set(plugin_id, key, value);
    }
}
