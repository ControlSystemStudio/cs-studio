/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.core.preferences;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess;
import org.csstudio.diirt.util.core.preferences.pojo.DataSources;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.util.NLS;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Singleton to access RIIT preferences.
 *
 * @author claudiorosati, European Spallation Source ERIC
 * @version 1.0.0 15 Dec 2016
 */
public final class DIIRTPreferences {

    public static final String PREF_CONFIGURATION_DIRECTORY = "diirt.home";
    public static final Logger LOGGER                       = Logger.getLogger(DIIRTPreferences.class.getName());

    private static final boolean BOOLEAN_DEFAULT_DEFAULT  = false;
    private static final String  DEFAULT_PREFIX           = "_default_.";
    private static final double  DOUBLE_DEFAULT_DEFAULT   = 0.0;
    private static final float   FLOAT_DEFAULT_DEFAULT    = 0.0f;
    private static final int     INTEGER_DEFAULT_DEFAULT  = 0;
    private static final long    LONG_DEFAULT_DEFAULT     = 0L;
    private static final String  PLATFORM_URI_PREFIX      = "platform:";
    private static final String  PREF_DEFAULT_INITIALIZED = "diirt.default.initialized";
    private static final String  QUALIFIER                = "org.csstudio.diirt.util.preferences";
    private static final String  STRING_DEFAULT_DEFAULT   = "";
    private static final String  USER_HOME_PARAMETER      = "@user.home";

    private final IScopeContext       scopeContext;
    private final IPreferencesService preferencesService;

    /**
     * Return the name of the property used to store the default value
     * of the one having the given name.
     *
     * @param preferenceName The preference name.
     * @return The name of the preference used to store the default value of the
     *         given one.
     */
    public static String defaultPreferenceName ( String preferenceName ) {
        return MessageFormat.format("{0}{1}", DEFAULT_PREFIX, preferenceName);
    }

    /**
     * @return The singleton instance of this class.
     */
    public static DIIRTPreferences get ( ) {
        return DIIRTPreferencesSingleInstance.INSTANCE;
    }

    /**
     * Check if the given name is used to save default values or not.
     *
     * @param preferenceName The preference name.
     * @return {@code true} if the given name is used to save default values.
     */
    public static boolean isDefaultPreferenceName ( String preferenceName ) {
        return StringUtils.defaultString(preferenceName).startsWith(DEFAULT_PREFIX);
    }

    /**
     * Verify if the selected path is a valid location for DIIRT configuration:
     * <ul>
     * <li>path exists.</li>
     * <li><path contains the {@code datasources/datasources.xml} file.</li>
     * </ul>
     *
     * @param path The path to be verified.
     * @return {@code null} if the {@code path} is valid, otherwise the error
     *         message explaining why the given {@code path} is not valid.
     */
    public static String resolveAndVerifyDIIRTPath ( String path ) {

        if ( path == null ) {
            return Messages.DIIRTPreferences_verifyDIIRTPath_nullPath_message;
        } else if ( StringUtils.isBlank(path) ) {
            return Messages.DIIRTPreferences_verifyDIIRTPath_blankPath_message;
        } else {

            String beforeResolving = path;

            try {
                path = resolvePlatformPath(path);
            } catch ( IOException | NullPointerException | IllegalArgumentException ex ) {
                return NLS.bind(Messages.DIIRTPreferences_verifyDIIRTPath_resolvingPath_message, path);
            }

            if ( !StringUtils.equals(beforeResolving, path) ) {
                LOGGER.log(Level.CONFIG, "DIIRT home path resolved [before: {0}, after: {1}].", new Object[] { beforeResolving, path });
            }

            if ( !Files.exists(Paths.get(path)) ) {
                return NLS.bind(Messages.DIIRTPreferences_verifyDIIRTPath_pathNotExists_message, path);
            } else if ( !Files.exists(Paths.get(path, DataSources.DATASOURCES_DIR + File.separator + DataSources.DATASOURCES_FILE)) ) {
                return NLS.bind(Messages.DIIRTPreferences_verifyDIIRTPath_pathNotValid_message, path);
            }

        }

        return null;

    }

    /**
     * Return a valid path string (to be used with {@link File}'s methods) resolving
     * the given {@code path} against the {@link #PLATFORM_URI_PREFIX} protocol, or
     * the {@link #USER_HOME_PARAMETER} macro.
     *
     * @param path The path to be resolved.
     * @return A filename valid for {@link File} operations.
     * @throws MalformedURLException If {@code path} starts with
     *             {@link #PLATFORM_URI_PREFIX} but is not a valid URL.
     * @throws IOException If {@code path} cannot be resolved.
     * @throws NullPointerException If {@code path} is {@code null}.
     * @throws IllegalArgumentException If {@code path} is empty or blank.
     */
    public static String resolvePlatformPath ( String path )
            throws MalformedURLException, IOException, NullPointerException, IllegalArgumentException
    {

        if ( path == null ) {
            throw new NullPointerException("Null 'path'.");
        } else if ( StringUtils.isBlank(path) ) {
            throw new IllegalArgumentException("Empty path.");
        }

        if ( path.contains(USER_HOME_PARAMETER) ) {
            path = path.replace(USER_HOME_PARAMETER, System.getProperty("user.home"));
        }

        if ( path.startsWith(PLATFORM_URI_PREFIX) ) {
            return FileLocator.resolve(new URL(path)).getPath().toString();
        } else {
            return new File(path).getCanonicalPath();
        }

    }

    private DIIRTPreferences ( ) {

        scopeContext = InstanceScope.INSTANCE;
        preferencesService = Platform.getPreferencesService();

        if ( !getBoolean(PREF_DEFAULT_INITIALIZED) ) {

            String diirtHome = getString(PREF_CONFIGURATION_DIRECTORY);
            String message = resolveAndVerifyDIIRTPath(diirtHome);

            if ( message != null ) {

                LOGGER.warning(message);

                Location location = Platform.getInstanceLocation();

                if ( location != null ) {
                    try {
                        diirtHome = URIUtil.toFile(URIUtil.append(location.getURL().toURI(), "diirt")).toString();
                    } catch ( URISyntaxException ex ) {
                        LOGGER.log(Level.WARNING, MessageFormat.format("Unable to setup fallback DIIRT directory [{0}].", location.toString()), ex);
                    }
                }

                message = resolveAndVerifyDIIRTPath(diirtHome);

                if ( message != null ) {
                    LOGGER.warning(message);
                }

            }

            if ( message == null ) {
                try {

                    diirtHome = resolvePlatformPath(diirtHome);

                    fromFiles(new File(diirtHome));
                    setString(PREF_CONFIGURATION_DIRECTORY, diirtHome);

                } catch ( NullPointerException | IllegalArgumentException | IOException ex ) {
                    LOGGER.log(Level.WARNING, MessageFormat.format("Unable to resolve DIIRT directory [{0}].", diirtHome), ex);
                }
            }

            setBoolean(PREF_DEFAULT_INITIALIZED, true);

            try {
                flush();
            } catch ( BackingStoreException ex ) {
                LOGGER.log(Level.WARNING, "Unable to flush preferences.", ex);
            }

        }

    }

    /**
     * This constructor is available for test fragments.
     *
     * @param scopeContext The {@link IScopeContext} to be used. <b>Don't
     *            use</b> the {@link InstanceScope#INSTANCE} contest: use
     *            {@link #get()} instead.
     */
    public DIIRTPreferences ( IScopeContext scopeContext ) {
        this.scopeContext = scopeContext;
        this.preferencesService = null;
    }

    /**
     * Forces any changes in the preferences to the persistent store.
     * <p>
     * Once this method returns successfully, it is safe to assume that all
     * changes made in the preferences prior to the method invocation have
     * become permanent.
     * </p>
     *
     * @throws BackingStoreException If this operation cannot be completed due
     *             to a failure in the backing store, or inability to
     *             communicate with it.
     */
    public void flush ( ) throws BackingStoreException {
        getPreferences().flush();
    }

    /**
     * Update this preferences store reading from the DIIRT home directory the
     * configuration files.
     *
     * @param diirtHome The DIIRT configuration directory.
     */
    public void fromFiles ( File diirtHome ) {

        DataSources ds = new DataSources();
        ChannelAccess ca = new ChannelAccess();

        try {
            ds = DataSources.fromFile(diirtHome);
            ca = ChannelAccess.fromFile(diirtHome);
        } catch ( JAXBException | IOException ex ) {
            LOGGER.log(Level.WARNING, MessageFormat.format("Problems opening and/or reading file(s) [{0}].", diirtHome), ex);
        }

        ds.updateDefaultsAndValues(this);
        ca.updateDefaultsAndValues(this);

        try {
            flush();
        } catch ( BackingStoreException ex ) {
            LOGGER.log(Level.WARNING, "Unable to flush preference store.", ex);
        }

    }

    /**
     * @return The resolved path to the DIIRT home.
     */
    public String getDIIRTHome ( ) {

        String diirtHome = getString(PREF_CONFIGURATION_DIRECTORY);
        String message = resolveAndVerifyDIIRTPath(diirtHome);

        if ( message != null ) {
            LOGGER.warning(message);
        }

        return diirtHome;

    }

    /**
     * Returns the current value of the boolean-valued preference with the given
     * name.
     *
     * @param name The name of the preference.
     * @return The boolean-valued preference.
     */
    public boolean getBoolean ( String name ) {
        if ( preferencesService != null ) {
            return preferencesService.getBoolean(QUALIFIER, name, getDefaultBoolean(name), null);
        } else {
            return getPreferences().getBoolean(name, getDefaultBoolean(name));
        }
    }

    /**
     * Returns the default value for the boolean-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @return The default value of the named preference.
     */
    public boolean getDefaultBoolean ( String name ) {
        if ( preferencesService != null ) {
            return preferencesService.getBoolean(QUALIFIER, defaultPreferenceName(name), BOOLEAN_DEFAULT_DEFAULT, null);
        } else {
            return getPreferences().getBoolean(defaultPreferenceName(name), BOOLEAN_DEFAULT_DEFAULT);
        }
    }

    /**
     * Returns the default value for the double-valued preference with the given
     * name.
     *
     * @param name The name of the preference.
     * @return The default value of the named preference.
     */
    public double getDefaultDouble ( String name ) {
        if ( preferencesService != null ) {
            return preferencesService.getDouble(QUALIFIER, defaultPreferenceName(name), DOUBLE_DEFAULT_DEFAULT, null);
        } else {
            return getPreferences().getDouble(defaultPreferenceName(name), DOUBLE_DEFAULT_DEFAULT);
        }
    }

    /**
     * Returns the default value for the float-valued preference
     * with the given name.
     *
     * @param name The name of the preference.
     * @return The default value of the named preference.
     */
    public float getDefaultFloat ( String name ) {
        if ( preferencesService != null ) {
            return preferencesService.getFloat(QUALIFIER, defaultPreferenceName(name), FLOAT_DEFAULT_DEFAULT, null);
        } else {
            return getPreferences().getFloat(defaultPreferenceName(name), FLOAT_DEFAULT_DEFAULT);
        }
    }

    /**
     * Returns the default value for the integer-valued preference
     * with the given name.
     *
     * @param name The name of the preference.
     * @return The default value of the named preference.
     */
    public int getDefaultInteger ( String name ) {
        if ( preferencesService != null ) {
            return preferencesService.getInt(QUALIFIER, defaultPreferenceName(name), INTEGER_DEFAULT_DEFAULT, null);
        } else {
            return getPreferences().getInt(defaultPreferenceName(name), INTEGER_DEFAULT_DEFAULT);
        }
    }

    /**
     * Returns the default value for the long-valued preference
     * with the given name.
     *
     * @param name The name of the preference.
     * @return The default value of the named preference.
     */
    public long getDefaultLong ( String name ) {
        if ( preferencesService != null ) {
            return preferencesService.getLong(QUALIFIER, defaultPreferenceName(name), LONG_DEFAULT_DEFAULT, null);
        } else {
            return getPreferences().getLong(defaultPreferenceName(name), LONG_DEFAULT_DEFAULT);
        }
    }

    /**
     * Returns the default value for the string-valued preference
     * with the given name.
     *
     * @param name The name of the preference.
     * @return The default value of the named preference.
     */
    public String getDefaultString ( String name ) {
        if ( preferencesService != null ) {
            return preferencesService.getString(QUALIFIER, defaultPreferenceName(name), STRING_DEFAULT_DEFAULT, null);
        } else {
            return getPreferences().get(defaultPreferenceName(name), STRING_DEFAULT_DEFAULT);
        }
    }

    /**
     * Returns the current value of the double-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @return The double-valued preference.
     */
    public double getDouble ( String name ) {
        if ( preferencesService != null ) {
            return preferencesService.getDouble(QUALIFIER, name, getDefaultDouble(name), null);
        } else {
            return getPreferences().getDouble(name, getDefaultDouble(name));
        }
    }

    /**
     * Returns the current value of the float-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @return The float-valued preference.
     */
    public float getFloat ( String name ) {
        if ( preferencesService != null ) {
            return preferencesService.getFloat(QUALIFIER, name, getDefaultFloat(name), null);
        } else {
            return getPreferences().getFloat(name, getDefaultFloat(name));
        }
    }

    /**
     * Returns the current value of the integer-valued preference with the given
     * name.
     *
     * @param name The name of the preference.
     * @return The int-valued preference.
     */
    public int getInteger ( String name ) {
        if ( preferencesService != null ) {
            return preferencesService.getInt(QUALIFIER, name, getDefaultInteger(name), null);
        } else {
            return getPreferences().getInt(name, getDefaultInteger(name));
        }
    }

    /**
     * Returns the current value of the long-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @return The long-valued preference.
     */
    public long getLong ( String name ) {
        if ( preferencesService != null ) {
            return preferencesService.getLong(QUALIFIER, name, getDefaultLong(name), null);
        } else {
            return getPreferences().getLong(name, getDefaultLong(name));
        }
    }

    /**
     * @return Return the {@link IEclipsePreferences} node. It can then be used
     *         to add listeners and visitors to the preferences.
     */
    public IEclipsePreferences getPreferences() {
        return scopeContext.getNode(QUALIFIER);
    }

    /**
     * Returns the current value of the string-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @return The string-valued preference.
     */
    public String getString ( String name ) {
        if ( preferencesService != null ) {
            return preferencesService.getString(QUALIFIER, name, getDefaultString(name), null);
        } else {
            return getPreferences().get(name, getDefaultString(name));
        }
    }

    /**
     * Sets the current value of the boolean-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new current value of the preference.
     */
    public void setBoolean ( String name, boolean value ) {

        boolean oldValue = getBoolean(name);

        if ( oldValue != value ) {
            if ( getDefaultBoolean(name) == value ) {
                getPreferences().remove(name);
            } else {
                getPreferences().putBoolean(name, value);
            }
        }

    }

    /**
     * Sets the default value for the boolean-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param defaultObject The new default value for the preference.
     */
    public void setDefaultBoolean ( String name, boolean value ) {
        getPreferences().putBoolean(defaultPreferenceName(name), value);
    }

    /**
     * Sets the default value for the double-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new default value for the preference.
     */
    public void setDefaultDouble ( String name, double value ) {
        getPreferences().putDouble(defaultPreferenceName(name), value);
    }

    /**
     * Sets the default value for the float-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new default value for the preference.
     */
    public void setDefaultFloat ( String name, float value ) {
        getPreferences().putFloat(defaultPreferenceName(name), value);
    }

    /**
     * Sets the default value for the integer-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new default value for the preference.
     */
    public void setDefaultInteger ( String name, int value ) {
        getPreferences().putInt(defaultPreferenceName(name), value);
    }

    /**
     * Sets the default value for the long-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new default value for the preference.
     */
    public void setDefaultLong ( String name, long value ) {
        getPreferences().putLong(defaultPreferenceName(name), value);
    }

    /**
     * Sets the default value for the string-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new default value for the preference.
     */
    public void setDefaultString ( String name, String value ) {
        getPreferences().put(defaultPreferenceName(name), value);
    }

    /**
     * Sets the current value of the double-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new current value of the preference.
     */
    public void setDouble ( String name, double value ) {

        double oldValue = getDouble(name);

        if ( oldValue != value ) {
            if ( getDefaultDouble(name) == value ) {
                getPreferences().remove(name);
            } else {
                getPreferences().putDouble(name, value);
            }
        }

    }

    /**
     * Sets the current value of the float-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new current value of the preference.
     */
    public void setFloat ( String name, float value ) {

        float oldValue = getFloat(name);

        if ( oldValue != value ) {
            if ( getDefaultFloat(name) == value ) {
                getPreferences().remove(name);
            } else {
                getPreferences().putFloat(name, value);
            }
        }

    }

    /**
     * Sets the current value of the integer-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new current value of the preference.
     */
    public void setInteger ( String name, int value ) {

        int oldValue = getInteger(name);

        if ( oldValue != value ) {
            if ( getDefaultInteger(name) == value ) {
                getPreferences().remove(name);
            } else {
                getPreferences().putInt(name, value);
            }
        }

    }

    /**
     * Sets the current value of the long-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new current value of the preference.
     */
    public void setLong ( String name, long value ) {

        long oldValue = getLong(name);

        if ( oldValue != value ) {
            if ( getDefaultLong(name) == value ) {
                getPreferences().remove(name);
            } else {
                getPreferences().putLong(name, value);
            }
        }

    }

    /**
     * Sets the current value of the string-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new current value of the preference.
     */
    public void setString ( String name, String value ) {

        String oldValue = getString(name);

        if ( ! StringUtils.equals(oldValue, value) ) {
            if ( StringUtils.equals(getDefaultString(name), value) ) {
                getPreferences().remove(name);
            } else {
                getPreferences().put(name, value);
            }
        }

    }

    /**
     * Sets the current value of the preference with the given name back
     * to its default value.
     *
     * @param name The name of the preference.
     */
    public void setToDefault ( String name ) {
        setString(name, getDefaultString(name));
    }

    /**
     * Ensures that future reads of the preferences reflect any changes that
     * were committed to the persistent store (from any VM) prior to the sync
     * invocation. As a side-effect, forces any changes to the persistent store,
     * as if the {@link #flush()} method had been invoked.
     *
     * @throws BackingStoreException If this operation cannot be completed due
     *             to a failure in the backing store, or inability to
     *             communicate with it.
     */
    public void synch ( ) throws BackingStoreException {
        getPreferences().sync();
    }

    /**
     * Export the current DIIRT configuration creating the relative files.
     *
     * @param diirtHome The DIIRT configuration directory.
     * @throws JAXBException If there were some marshalling problems.
     * @throws IOException  If an error occurred writing the configuration.
     */
    public void toFiles ( File diirtHome ) throws IOException, JAXBException {

        if ( diirtHome == null || !diirtHome.exists() || !diirtHome.isDirectory() ) {
            return;
        }

        new DataSources(this).toFile(diirtHome);
        new ChannelAccess(this).toFile(diirtHome);

    }

    private static class DIIRTPreferencesSingleInstance {
        static final DIIRTPreferences INSTANCE = new DIIRTPreferences();
    }

}
