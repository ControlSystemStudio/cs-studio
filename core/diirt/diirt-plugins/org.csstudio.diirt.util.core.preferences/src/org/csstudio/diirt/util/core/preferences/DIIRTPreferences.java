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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.csstudio.diirt.util.core.preferences.ExceptionUtilities.CompoundIOException;
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

    public static final Logger LOGGER                       = Logger.getLogger(DIIRTPreferences.class.getName());
    public static final String PREF_CONFIGURATION_DIRECTORY = "diirt.home";
    public static final String QUALIFIER                    = "org.csstudio.diirt.util.core.preferences";

    private static final boolean BOOLEAN_DEFAULT_DEFAULT  = false;
    private static final String  DEFAULT_PREFIX           = "_default_.";
    private static final double  DOUBLE_DEFAULT_DEFAULT   = 0.0;
    private static final float   FLOAT_DEFAULT_DEFAULT    = 0.0f;
    private static final int     INTEGER_DEFAULT_DEFAULT  = 0;
    private static final long    LONG_DEFAULT_DEFAULT     = 0L;
    private static final String  PLATFORM_URI_PREFIX      = "platform:";
    private static final String  PREF_DEFAULT_INITIALIZED = "diirt.default.initialized";
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
            } catch ( IOException | NullPointerException | IllegalArgumentException | URISyntaxException ex ) {

                String message = NLS.bind(Messages.DIIRTPreferences_verifyDIIRTPath_resolvingPath_message, path);

                LOGGER.log(Level.WARNING, MessageFormat.format("{0}\n{1}", message, ExceptionUtilities.reducedStackTrace(ex, "org.csstudio")));

                return message;

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
     * @throws URISyntaxException If {@code path} cannot be resolved to a filesystem pathname.
     */
    public static String resolvePlatformPath ( String path )
            throws MalformedURLException, IOException, NullPointerException, IllegalArgumentException, URISyntaxException
    {

        LOGGER.log(Level.FINE, "About to resolve platform path [path: {0}].", path);

        if ( path == null ) {
            throw new NullPointerException("Null 'path'.");
        } else if ( StringUtils.isBlank(path) ) {
            throw new IllegalArgumentException("Empty path.");
        }

        if ( path.contains(USER_HOME_PARAMETER) ) {
            path = path.replace(USER_HOME_PARAMETER, System.getProperty("user.home"));
        }

        if ( path.startsWith(PLATFORM_URI_PREFIX) ) {

            URL resolvedURL = FileLocator.resolve(new URL(path));

            LOGGER.log(Level.FINE, "Resolved URL\n    before: {0}\n     after: {1}", new Object[] { path, resolvedURL.toString() });

            String escapedURL = htmlEncode(resolvedURL.toExternalForm());

            LOGGER.log(Level.FINE, "Escaped URL\n    before: {0}\n     after: {1}", new Object[] { resolvedURL.toString(), escapedURL });

            URI resolvedURI = new URI(escapedURL);

            LOGGER.log(Level.FINE, "Resolved URI\n    before: {0}\n     after: {1}", new Object[] { escapedURL, resolvedURI.toString() });

            File resolvedFile = URIUtil.toFile(resolvedURI);

            LOGGER.log(Level.FINE, "Resolved File\n    before: {0}\n     after: {1}", new Object[] { resolvedURI.toString(), resolvedFile.toString() });

            String resolvedString = resolvedFile.getCanonicalPath();

            LOGGER.log(Level.FINE, "Resolved String\n    before: {0}\n     after: {1}", new Object[] { resolvedFile.toString(), resolvedString });

            return resolvedString;

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
                        LOGGER.log(Level.WARNING, MessageFormat.format("Unable to setup fallback DIIRT directory [{0}].\n{1}", location.toString(), ExceptionUtilities.reducedStackTrace(ex, "org.csstudio")));
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

                } catch ( NullPointerException | IllegalArgumentException | IOException | URISyntaxException ex ) {
                    LOGGER.log(Level.WARNING, MessageFormat.format("Unable to resolve DIIRT directory [{0}].\n{1}", diirtHome, ExceptionUtilities.reducedStackTrace(ex, "org.csstudio")));
                }
            }

            setBoolean(PREF_DEFAULT_INITIALIZED, true);

            try {
                flush();
            } catch ( BackingStoreException ex ) {
                LOGGER.log(Level.WARNING, "Unable to flush preferences.\n{0}", ExceptionUtilities.reducedStackTrace(ex, "org.csstudio"));
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
        } catch ( JAXBException | IOException ex ) {
            LOGGER.log(Level.WARNING, MessageFormat.format("Problems opening and/or reading datasource.xml [{0}].\n{1}", diirtHome, ExceptionUtilities.reducedStackTrace(ex, "org.csstudio")));
        }

        try {
            ca = ChannelAccess.fromFile(diirtHome);
        } catch ( JAXBException | IOException ex ) {
            LOGGER.log(Level.WARNING, MessageFormat.format("Problems opening and/or reading ca/ca.xml [{0}].\n{1}", diirtHome, ExceptionUtilities.reducedStackTrace(ex, "org.csstudio")));
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
     * Returns whether the named preference is known to this preference store.
     *
     * @param name The name of the preference.
     * @return {@code true} if either a current value or a default value is
     *         known for the named preference, and {@code false} otherwise.
     */
    public boolean contains ( String name ) {

        String value;

        if ( preferencesService != null ) {
            value = preferencesService.getString(QUALIFIER, name, null, null);
        } else {
            value = getPreferences().get(name, null);
        }

        if ( value == null ) {
            if ( preferencesService != null ) {
                value = preferencesService.getString(QUALIFIER, defaultPreferenceName(name), null, null);
            } else {
                value = getPreferences().get(defaultPreferenceName(name), null);
            }
        }

        return ( value != null );

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
        getPreferences().putBoolean(name, value);
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
        getPreferences().putDouble(name, value);
    }

    /**
     * Sets the current value of the float-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new current value of the preference.
     */
    public void setFloat ( String name, float value ) {
        getPreferences().putFloat(name, value);
    }

    /**
     * Sets the current value of the integer-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new current value of the preference.
     */
    public void setInteger ( String name, int value ) {
        getPreferences().putInt(name, value);
    }

    /**
     * Sets the current value of the long-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new current value of the preference.
     */
    public void setLong ( String name, long value ) {
        getPreferences().putLong(name, value);
    }

    /**
     * Sets the current value of the string-valued preference with the
     * given name.
     *
     * @param name The name of the preference.
     * @param value The new current value of the preference.
     */
    public void setString ( String name, String value ) {
        getPreferences().put(name, value);
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
    public void toFiles ( File diirtHome ) throws IOException {

        if ( diirtHome == null || !diirtHome.exists() || !diirtHome.isDirectory() ) {
            return;
        }

        new DataSources(this).toFile(diirtHome);
        new ChannelAccess(this).toFile(diirtHome);

        String confDir = getString(PREF_CONFIGURATION_DIRECTORY);

        if ( StringUtils.isNoneBlank(confDir) ) {

            File confFile = new File(confDir);

            if ( confFile.exists() && confFile.isDirectory() ) {
                copyFiles(
                    confFile,
                    diirtHome,
                    Arrays.asList(
                        new File(DataSources.DATASOURCES_DIR, DataSources.DATASOURCES_FILE),
                        new File(new File(DataSources.DATASOURCES_DIR, ChannelAccess.CA_DIR), ChannelAccess.CA_FILE)
                    )
                );
            }

        }

    }

    /**
     * Export the current DIIRT configuration creating the relative files.
     *
     * @param diirtHome    The DIIRT configuration directory.
     * @param deleteOnExit If {@code true} the {@link Runtime#addShutdownHook(Thread)} is
     *                     invoked to add a thread that will delete the given {@code diirtHome}
     *                     when the application exits.
     * @throws JAXBException If there were some marshalling problems.
     * @throws IOException  If an error occurred writing the configuration.
     */
    public void toFiles ( File diirtHome, boolean deleteOnExit ) throws IOException {

        if ( deleteOnExit ) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> deleteQuietly(diirtHome)));
        }

        toFiles(diirtHome);

    }

    /**
     * Copy recursively all the files in the {@code from} the given directory
     * {@code to} the given one, {@code excluding} everything inside the
     * provided {@link List}.
     *
     * @param from      The directory source of the files to be copied.
     * @param to        The directory where files must be copied into.
     * @param excluding The {@link List} of files/folder that must not be copied.
     *                  Inside this list, pathnames must be relative to the
     *                  {@code from} directory.
     * @throws IOException If {@code to} is not a directory or some problems
     *              occurs while copying the files.
     * @throws CompoundIOException If problems occurs copying files.
     */
    private static void copyFiles ( final File from, final File to, final List<File> excluding ) throws IOException, CompoundIOException {

        if ( from == null || !from.exists() || !from.isDirectory() ) {
            return;
        }

        if ( to == null ) {
            throw new NullPointerException("'to' directory.");
        } else if ( !to.exists() ) {
            if ( !to.mkdirs() ) {
                throw new IOException(MessageFormat.format("Unable to create 'to' directory [{0}].", to.getPath()));
            }
        }

        List<Exception> exceptions = new ArrayList<>();

        Arrays.asList(from.listFiles()).stream().filter(f -> !excluding.stream().anyMatch(ff -> f.toString().endsWith(ff.toString()))).forEach(f -> {
            if ( f.isDirectory() ) {

                File destination = new File(to, f.getName());

                try {
                    copyFiles(f, destination, excluding);
                } catch ( IOException ioex ) {
                    exceptions.add(new IOException(MessageFormat.format("Unable to create to copy files [from: {0}, to: {1}].", f.toString(), destination.toString())));
                }
            } else {

                Path source = f.toPath();
                Path destination = new File(to, f.getName()).toPath();

                try {
                    Files.copy(source, destination, StandardCopyOption.COPY_ATTRIBUTES);
                } catch ( IOException ioex ) {
                    exceptions.add(new IOException(MessageFormat.format("Unable to create to copy a file [from: {0}, to: {1}].", source.toString(), destination.toString())));
                }

            }
        });

        if ( !exceptions.isEmpty() ) {
            throw new CompoundIOException(MessageFormat.format("Problems copying files [from: {0}, to: {1}].", from.toString(), to.toString()), exceptions);
        }

    }

    /**
     * This method is copied from {@link IOUtils} because when it is
     * used (inside the runtime shutdown hook) all OSGI modules are
     * already disposed.
     *
     * @param directory
     * @throws IOException
     */
    private static void cleanDirectory ( final File directory ) throws IOException {

        final File[] files = verifiedListFiles(directory);
        IOException exception = null;

        for ( final File file : files ) {
            try {
                forceDelete(file);
            } catch ( final IOException ioe ) {
                exception = ioe;
            }
        }

        if ( null != exception ) {
            throw exception;
        }

    }

    /**
     * This method is copied from {@link IOUtils} because when it is
     * used (inside the runtime shutdown hook) all OSGI modules are
     * already disposed.
     *
     * @param directory
     * @throws IOException
     */
    private static void deleteDirectory ( final File directory ) throws IOException {

        if ( !directory.exists() ) {
            return;
        }


        if ( !Files.isSymbolicLink(directory.toPath()) ) {
            cleanDirectory(directory);
        }

        if ( !directory.delete() ) {
            throw new IOException("Unable to delete directory " + directory + ".");
        }

    }

    /**
     * This method is copied from {@link IOUtils} because when it is
     * used (inside the runtime shutdown hook) all OSGI modules are
     * already disposed.
     *
     * @param file
     * @return
     */
    private static boolean deleteQuietly ( final File file ) {

        if ( file == null ) {
            return false;
        }

        try {
            if ( file.isDirectory() ) {
                cleanDirectory(file);
            }
        } catch ( final Exception ignored ) {
        }

        try {
            return file.delete();
        } catch ( final Exception ignored ) {
            return false;
        }

    }

    /**
     * This method is copied from {@link IOUtils} because when it is
     * used (inside the runtime shutdown hook) all OSGI modules are
     * already disposed.
     *
     * @param file
     * @throws IOException
     */
    private static void forceDelete ( final File file ) throws IOException {

        if ( file.isDirectory() ) {
            deleteDirectory(file);
        } else {

            final boolean filePresent = file.exists();

            if ( !file.delete() ) {

                if ( !filePresent ) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }

                throw new IOException("Unable to delete file: " + file);

            }
        }

    }

    /**
     * Encode the given URL string.
     * <p>
     * <b>Note:</b> {@link URIUtil#toFile(URI)} requires the URI to be encoded
     * otherwise the filesystem mapping will work. The whole URI must be encoded
     * but in a sensible way: this is why {@link URLEncoder#encode(String)}
     * cannot be used.
     * <p>
     * This implementation is based on testing each "switch case" on MacOS X,
     * Linux (CentOS 7.1) and Windows 7 against {@link URIUtil#toFile(URI)}.
     *
     * @param s The string to be encoded.
     * @return The encoded string.
     */
    private static String htmlEncode ( String s ) {

        if ( StringUtils.isBlank(s) ) {
            return s;
        }

        StringBuilder sb = new StringBuilder(s.length() + 16);

        for ( int i = 0; i < s.length(); i++ ) {

            char c = s.charAt(i);

            switch ( c ) {
                case ' ':
                    sb.append("%20");
                    break;
                case '!':
                    sb.append("%21");
                    break;
                case '#':
                    sb.append("%23");
                    break;
                case '$':
                    sb.append("%24");
                    break;
                case '%':
                    sb.append("%25");
                    break;
                case '&':
                    sb.append("%26");
                    break;
                case '\'':
                    sb.append("%27");
                    break;
                case '(':
                    sb.append("%28");
                    break;
                case ')':
                    sb.append("%29");
                    break;
                case '*':
                    sb.append("%2A");
                    break;
                case '+':
                    sb.append("%2B");
                    break;
                case ',':
                    sb.append("%2C");
                    break;
                case '<':
                    sb.append("%3C");
                    break;
                case '=':
                    sb.append("%3D");
                    break;
                case '>':
                    sb.append("%3E");
                    break;
                case '?':
                    sb.append("%3F");
                    break;
                case '@':
                    sb.append("%40");
                    break;
                case '[':
                    sb.append("%5B");
                    break;
                case ']':
                    sb.append("%5D");
                    break;
                case '^':
                    sb.append("%5E");
                    break;
                case '`':
                    sb.append("%60");
                    break;
                case '{':
                    sb.append("%7B");
                    break;
                case '|':
                    sb.append("%7C");
                    break;
                case '}':
                    sb.append("%7D");
                    break;
                case '"':   //  %22
                case '-':   //  %2D
                case '.':   //  %2E
                case '/':   //  %2F
                case ':':   //  %3A
                case ';':   //  %3B
                case '\\':  //  %5C
                case '_':   //  %5F
                case '~':   //  %7E
                default:
                    sb.append(c);
            }

        }

        return sb.toString();

    }

    /**
     * This method is copied from {@link IOUtils} because when it is
     * used (inside the runtime shutdown hook) all OSGI modules are
     * already disposed.
     *
     * @param directory
     * @return
     * @throws IOException
     */
    private static File[] verifiedListFiles ( File directory ) throws IOException {

        if ( !directory.exists() ) {
            throw new IllegalArgumentException(directory + " does not exist");
        }

        if ( !directory.isDirectory() ) {
            throw new IllegalArgumentException(directory + " is not a directory");
        }

        final File[] files = directory.listFiles();

        if ( files == null ) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }

        return files;

    }

    private static class DIIRTPreferencesSingleInstance {
        static final DIIRTPreferences INSTANCE = new DIIRTPreferences();
    }

}
