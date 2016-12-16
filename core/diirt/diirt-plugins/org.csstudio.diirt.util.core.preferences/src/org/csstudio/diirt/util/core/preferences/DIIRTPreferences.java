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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.csstudio.diirt.util.core.preferences.pojo.DataSources;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
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
public class DIIRTPreferences {

    public static final Logger LOGGER = Logger.getLogger(DIIRTPreferences.class.getName());

    private static final boolean BOOLEAN_DEFAULT_DEFAULT  = false;
    private static final String  DEFAULT_PREFIX           = "_default_.";
    private static final double  DOUBLE_DEFAULT_DEFAULT   = 0.0;
    private static final float   FLOAT_DEFAULT_DEFAULT    = 0.0f;
    private static final int     INTEGER_DEFAULT_DEFAULT  = 0;
    private static final long    LONG_DEFAULT_DEFAULT     = 0L;
    private static final String  PLATFORM_URI_PREFIX      = "platform:";
    private static final String  PREF_DEFAULT_INITIALIZED = "diirt.default.initialized";
    private static final String  STRING_DEFAULT_DEFAULT   = ""; //$NON-NLS-1$
    private static final String  USER_HOME_PARAMETER      = "@user.home";

    private final IScopeContext scopeContext;
    private final String        qualifier = Activator.getContext().getBundle().getSymbolicName();

    /**
     * Return the name of the property used to store the default value
     * of the one having the given name.
     *
     * @param preferenceName The preference name.
     * @return The name of the preference used to store the default value of the
     *         given one.
     */
    public static final String defaultPreferenceName ( String preferenceName ) {
        return MessageFormat.format("{0}{1}", DEFAULT_PREFIX, preferenceName);
    }

    /**
     * @return The singleton instance of this class.
     */
    public final static DIIRTPreferences get ( ) {
        return DIIRTPreferencesSingleInstance.INSTANCE;
    }

    /**
     * Check if the given name is used to save default values or not.
     *
     * @param preferenceName The preference name.
     * @return {@code true} if the given name is used to save default values.
     */
    public static final boolean isDefaultPreferenceName ( String preferenceName ) {
        return StringUtils.defaultString(preferenceName).startsWith(DEFAULT_PREFIX);
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
            throw new NullPointerException("Null path'");
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
    public static String verifyDIIRTPath ( String path ) {

        if ( path == null ) {
            return Messages.DIIRTPreferences_verifyDIIRTPath_nullPath_message;
        } else if ( StringUtils.isBlank(path) ) {
            return Messages.DIIRTPreferences_verifyDIIRTPath_blankPath_message;
        } else if ( !Files.exists(Paths.get(path)) ) {
            return NLS.bind(Messages.DIIRTPreferences_verifyDIIRTPath_pathNotExists_message, path);
        } else if ( !Files.exists(Paths.get(path, DataSources.DATASOURCES_DIR + File.separator + DataSources.DATASOURCES_FILE)) ) {
            return NLS.bind(Messages.DIIRTPreferences_verifyDIIRTPath_pathNotValid_message, path);
        }

        return null;

    }

    private DIIRTPreferences ( ) {

        scopeContext = InstanceScope.INSTANCE;

        if ( !getBoolean(PREF_DEFAULT_INITIALIZED) ) {

            File defaultDIIRTConfig = null;
            Location location = Platform.getInstanceLocation();

            if ( location != null ) {
                try {
                    defaultDIIRTConfig = URIUtil.toFile(URIUtil.append(location.getURL().toURI(), "diirt"));
                } catch ( URISyntaxException e ) {
                }
            }









            //  TODO: CR: initialize
        }

    }

    /**
     * This constructor is available for test fragments.
     *
     * @param scopeContext The {@link IScopeContext} to be used. <b>Don't
     *            use</b> the {@link InstanceScope#INSTANCE} contest: use
     *            {@link #get()} instead.
     */
    DIIRTPreferences ( IScopeContext scopeContext ) {
        this.scopeContext = scopeContext;
    }

    /**
     * Returns whether the named preference is known.
     *
     * @param name The name of the preference.
     * @return {@code true} if either a current value or a default
     *         value is known for the named preference, {@code false} otherwise.
     */
    public final boolean contains ( String name ) {

        try {

            List<String> names = Arrays.asList(getPreferences().keys());

            if ( names.contains(name) || names.contains(defaultPreferenceName(name)) ) {
                return true;
            }

        } catch ( BackingStoreException ex ) {
            LOGGER.log(Level.WARNING, MessageFormat.format("Unable to check if the given preference name is contained [{0}].", name), ex);
        }

        return false;

    }

    /**
     * Returns whether a default for the named preference is known.
     *
     * @param name The name of the preference.
     * @return {@code true} if either a default value is known for the named
     *         preference, {@code false} otherwise.
     */
    public final boolean containsDefault ( String name ) {

        try {

            List<String> names = Arrays.asList(getPreferences().keys());

            if ( names.contains(defaultPreferenceName(name)) ) {
                return true;
            }

        } catch ( BackingStoreException ex ) {
            LOGGER.log(Level.WARNING, MessageFormat.format("Unable to check if the given preference name is contained [{0}].", name), ex);
        }

        return false;

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
    public final void flush ( ) throws BackingStoreException {
        getPreferences().flush();
    }

    /**
     * @return The resolved path to the DIIRT home.
     */
    public String getDIIRTHome ( ) {
//  TODO: CR: to be implemented
        return null;
    }

    /**
     * Returns the current value of the boolean-valued preference with the given
     * name.
     *
     * @param name The name of the preference.
     * @return The boolean-valued preference.
     */
    public final boolean getBoolean ( String name ) {
        return getPreferences().getBoolean(name, getDefaultBoolean(name));
    }

    /**
     * Returns the default value for the boolean-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @return The default value of the named preference.
     */
    public final boolean getDefaultBoolean ( String name ) {
        return getPreferences().getBoolean(defaultPreferenceName(name), BOOLEAN_DEFAULT_DEFAULT);
    }

    /**
     * Returns the default value for the double-valued preference with the given
     * name.
     *
     * @param name The name of the preference.
     * @return The default value of the named preference.
     */
    public final double getDefaultDouble ( String name ) {
        return getPreferences().getDouble(defaultPreferenceName(name), DOUBLE_DEFAULT_DEFAULT);
    }

    /**
     * Returns the default value for the float-valued preference
     * with the given name.
     *
     * @param name The name of the preference.
     * @return The default value of the named preference.
     */
    public final float getDefaultFloat ( String name ) {
        return getPreferences().getFloat(defaultPreferenceName(name), FLOAT_DEFAULT_DEFAULT);
    }

    /**
     * Returns the default value for the integer-valued preference
     * with the given name.
     *
     * @param name The name of the preference.
     * @return The default value of the named preference.
     */
    public final int getDefaultInteger ( String name ) {
        return getPreferences().getInt(defaultPreferenceName(name), INTEGER_DEFAULT_DEFAULT);
    }

    /**
     * Returns the default value for the long-valued preference
     * with the given name.
     *
     * @param name The name of the preference.
     * @return The default value of the named preference.
     */
    public final long getDefaultLong ( String name ) {
        return getPreferences().getLong(defaultPreferenceName(name), LONG_DEFAULT_DEFAULT);
    }

    /**
     * Returns the default value for the string-valued preference
     * with the given name.
     *
     * @param name The name of the preference.
     * @return The default value of the named preference.
     */
    public final String getDefaultString ( String name ) {
        return getPreferences().get(defaultPreferenceName(name), STRING_DEFAULT_DEFAULT);
    }

    /**
     * Returns the current value of the double-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @return The double-valued preference.
     */
    public final double getDouble ( String name ) {
        return getPreferences().getDouble(name, getDefaultDouble(name));
    }

    /**
     * Returns the current value of the float-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @return The float-valued preference.
     */
    public final float getFloat ( String name ) {
        return getPreferences().getFloat(name, getDefaultFloat(name));
    }

    /**
     * Returns the current value of the integer-valued preference with the given
     * name.
     *
     * @param name The name of the preference.
     * @return The int-valued preference.
     */
    public final int getInteger ( String name ) {
        return getPreferences().getInt(name, getDefaultInteger(name));
    }

    /**
     * Returns the current value of the long-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @return The long-valued preference.
     */
    public final long getLong ( String name ) {
        return getPreferences().getLong(name, getDefaultLong(name));
    }

    /**
     * @return Return the {@link IEclipsePreferences} node. It can then be used
     *         to add listeners and visitors to the preferences.
     */
    public final IEclipsePreferences getPreferences() {
        return scopeContext.getNode(qualifier);
    }

    /**
     * Returns the current value of the string-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @return The string-valued preference.
     */
    public final String getString ( String name ) {
        return getPreferences().get(name, getDefaultString(name));
    }

    /**
     * Sets the current value of the boolean-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new current value of the preference.
     */
    public final void setBoolean ( String name, boolean value ) {

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
    public final void setDefaultBoolean ( String name, boolean value ) {
        getPreferences().putBoolean(name, value);
    }

    /**
     * Sets the default value for the double-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new default value for the preference.
     */
    public final void setDefaultDouble ( String name, double value ) {
        getPreferences().putDouble(name, value);
    }

    /**
     * Sets the default value for the float-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new default value for the preference.
     */
    public final void setDefaultFloat ( String name, float value ) {
        getPreferences().putFloat(name, value);
    }

    /**
     * Sets the default value for the integer-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new default value for the preference.
     */
    public final void setDefaultInteger ( String name, int value ) {
        getPreferences().putInt(name, value);
    }

    /**
     * Sets the default value for the long-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new default value for the preference.
     */
    public final void setDefaultLong ( String name, long value ) {
        getPreferences().putLong(name, value);
    }

    /**
     * Sets the default value for the string-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new default value for the preference.
     */
    public final void setDefaultString ( String name, String value ) {
        getPreferences().put(name, value);
    }

    /**
     * Sets the current value of the double-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new current value of the preference.
     */
    public final void setDouble ( String name, double value ) {

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
    public final void setValue ( String name, float value ) {

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
    public final void setInteger ( String name, int value ) {

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
    public final void setLong ( String name, long value ) {

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
    public final void setString ( String name, String value ) {

        String oldValue = getString(name);

        if ( StringUtils.equals(oldValue, value) ) {
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
    public final void setToDefault ( String name ) {
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
    public final void synch ( ) throws BackingStoreException {
        getPreferences().sync();
    }

    private interface DIIRTPreferencesSingleInstance {
        static final DIIRTPreferences INSTANCE = new DIIRTPreferences();
    }

}
