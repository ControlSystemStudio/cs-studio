/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang3.StringUtils;
import org.csstudio.diirt.util.preferences.pojo.ChannelAccess;
import org.csstudio.diirt.util.preferences.pojo.DataSources;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * Handle the DIIRT preferences, reading them from the configuration files
 * and applying the overrides.
 *
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 14 Nov 2016
 */
public class DIIRTPreferencesPlugin extends AbstractUIPlugin {

    public static final String     CANCEL_PREFIX                  = "cancel.";
    public static final String     DEFAULT_PREFIX                 = "default.";
    public static final Logger     LOGGER                         = Logger.getLogger(DIIRTPreferencesPlugin.class.getName());
    public static final String     PLATFORM_URI_PREFIX            = "platform:";
    public static final String     PREF_CONFIGURATION_DIRECTORY   = "diirt.home";
    public static final String     PREF_DEFAULT_INITIALIZED       = "diirt.default.initialized";
    public static final String     USER_HOME_PARAMETER            = "@user.home";

    private static DIIRTPreferencesPlugin instance    = null;
    private static boolean                firstAccess = true;

    public static String defaultPreferenceName ( String preferenceName ) {
        return MessageFormat.format("{0}{1}", DEFAULT_PREFIX, preferenceName);
    }

    public static DIIRTPreferencesPlugin get ( ) {

        if ( firstAccess && instance != null ) {
            instance.getPreferenceStore().getString(PREF_CONFIGURATION_DIRECTORY);
            firstAccess = false;
        }

        return instance;

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
            return Messages.DPH_verifyDIIRTPath_nullPath_message;
        } else if ( StringUtils.isBlank(path) ) {
            return Messages.DPH_verifyDIIRTPath_blankPath_message;
        } else if ( !Files.exists(Paths.get(path)) ) {
            return NLS.bind(Messages.DPH_verifyDIIRTPath_pathNotExists_message, path);
        } else if ( !Files.exists(Paths.get(path, DataSources.DATASOURCES_DIR + File.separator + DataSources.DATASOURCES_FILE)) ) {
            return NLS.bind(Messages.DPH_verifyDIIRTPath_pathNotValid_message, path);
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

    public DIIRTPreferencesPlugin ( ) {
        instance = this;
    }

    /**
     * Export the current DIIRT configuration creating the relative files.
     *
     * @param parent The parent directory of the exported configuration.
     * @throws JAXBException If there were some marshalling problems.
     * @throws IOException  If an error occurred writing the configuration.
     * @throws IllegalArgumentException  If an error occurred evaluating enumerations.
     * @throws XMLStreamException If there were some marshalling problems.
     */
    public void exportConfiguration ( File parent ) throws JAXBException, IOException, IllegalArgumentException, XMLStreamException {

        if ( parent == null || !parent.exists() || !parent.isDirectory() ) {
            return;
        }

        IPreferenceStore store = getPreferenceStore();

        new DataSources(store).toFile(parent);
        new ChannelAccess(store).toFile(parent);

    }

    /**
     * @return The resolved path to the DIIRT home.
     */
    public String getDIIRTHome ( ) {
        return getDIIRTHome(getPreferenceStore());
    }

    /**
     * Update all stored defaults, current defaults and values from the files in the
     * given DIIRT configuration directory.
     *
     * @param confDir The DIIRT configuration directory.
     * @param store   The preference store.
     */
    public void updateDefaultsAndValues ( String confDir, IPreferenceStore store ) {

        if ( StringUtils.isBlank(confDir) ) {
            LOGGER.warning("Null, empty or blank 'confDir'");
            return;
        } else {
            try {
                confDir = resolvePlatformPath(confDir);
            } catch ( NullPointerException | IllegalArgumentException | IOException ex ) {
                LOGGER.log(Level.WARNING, MessageFormat.format("Path cannot be resolved [{0}].", confDir), ex);
                return;
            }
        }

        File confFolder = new File(confDir);
        DataSources ds = new DataSources();
        ChannelAccess ca = new ChannelAccess();

        try {
            ds = DataSources.fromFile(confFolder);
            ca = ChannelAccess.fromFile(confFolder);
        } catch ( JAXBException | IOException ex ) {
            LOGGER.log(Level.WARNING, MessageFormat.format("Problems opening and/or reading file(s) [{0}].", confDir), ex);
        }

        ds.updateDefaultsAndValues(store);
        ca.updateDefaultsAndValues(store);

        try {
            ((IPersistentPreferenceStore) store).save();
        } catch ( IOException ex ) {
            DIIRTPreferencesPlugin.LOGGER.log(Level.WARNING, "Unable to flush preference store.", ex);
        }

    }

    /**
     * Update current defaults and values from the stored ones.
     *
     * @param store The preference store.
     */
    public void updateValues ( IPreferenceStore store ) {
        DataSources.updateValues(store);
        ChannelAccess.updateValues(store);
    }



    @Override
    protected void initializeDefaultPreferences ( IPreferenceStore store ) {

        // Don't move the following statement: it is required at this time.
        String diirtHome = getDIIRTHome(store);

        synchronized ( this ) {
            if ( !store.getBoolean(PREF_DEFAULT_INITIALIZED) ) {

                if ( verifyDIIRTPath(diirtHome) == null ) {
                    updateDefaultsAndValues(diirtHome, store);
                }

                store.setValue(PREF_DEFAULT_INITIALIZED, true);

                try {
                    ((IPersistentPreferenceStore) store).save();
                } catch ( IOException ex ) {
                  DIIRTPreferencesPlugin.LOGGER.log(Level.WARNING, "Unable to flush preference store.", ex);
                }

            } else {
                updateValues(store);
            }
        }

    }

    private String getDIIRTHome ( IPreferenceStore store ) {

        String diirtHome = store.getString(PREF_CONFIGURATION_DIRECTORY);

        try {

            String resolvedDir = resolvePlatformPath(diirtHome);

            if ( !StringUtils.equals(diirtHome, resolvedDir) ) {

                LOGGER.log(Level.CONFIG, "DIIRT home path resolved [before: {0}, after: {1}].", new Object[] { diirtHome, resolvedDir });

                diirtHome = resolvedDir;

                store.putValue(PREF_CONFIGURATION_DIRECTORY, diirtHome);

                try {
                    ( (IPersistentPreferenceStore) store ).save();
                } catch ( IOException ex ) {
                    DIIRTPreferencesPlugin.LOGGER.log(Level.WARNING, "Unable to flush preference store.", ex);
                }

            }

        } catch ( IOException ex ) {
            LOGGER.log(Level.WARNING, MessageFormat.format("Unable to resolve DIIRT home [{0}].", diirtHome), ex);
        }

        return diirtHome;

    }

}
