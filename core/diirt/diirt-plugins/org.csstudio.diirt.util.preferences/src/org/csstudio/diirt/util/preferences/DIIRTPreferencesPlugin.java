/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;


import java.text.MessageFormat;
import java.util.logging.Logger;

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

    private static DIIRTPreferencesPlugin instance    = null;
//    private static boolean                firstAccess = true;

    public static String defaultPreferenceName ( String preferenceName ) {
        return MessageFormat.format("{0}{1}", DEFAULT_PREFIX, preferenceName);
    }

    public static DIIRTPreferencesPlugin get ( ) {

//        if ( firstAccess && instance != null ) {
//            instance.getPreferenceStore().getString(PREF_CONFIGURATION_DIRECTORY);
//            firstAccess = false;
//        }

        return instance;

    }

    public DIIRTPreferencesPlugin ( ) {
        instance = this;
    }

    /**
     * Update all stored defaults, current defaults and values from the files in the
     * given DIIRT configuration directory.
     *
     * @param confDir The DIIRT configuration directory.
     * @param store   The preference store.
     */
//    public void updateDefaultsAndValues ( String confDir, IPreferenceStore store ) {
//
//        if ( StringUtils.isBlank(confDir) ) {
//            LOGGER.warning("Null, empty or blank 'confDir'");
//            return;
//        } else {
//            try {
//                confDir = resolvePlatformPath(confDir);
//            } catch ( NullPointerException | IllegalArgumentException | IOException ex ) {
//                LOGGER.log(Level.WARNING, MessageFormat.format("Path cannot be resolved [{0}].", confDir), ex);
//                return;
//            }
//        }
//
//        File confFolder = new File(confDir);
//        DataSources ds = new DataSources();
//        ChannelAccess ca = new ChannelAccess();
//
//        try {
//            ds = DataSources.fromFile(confFolder);
//            ca = ChannelAccess.fromFile(confFolder);
//        } catch ( JAXBException | IOException ex ) {
//            LOGGER.log(Level.WARNING, MessageFormat.format("Problems opening and/or reading file(s) [{0}].", confDir), ex);
//        }
//
//        ds.updateDefaultsAndValues(store);
//        ca.updateDefaultsAndValues(store);
//
//        try {
//            ((IPersistentPreferenceStore) store).save();
//        } catch ( IOException ex ) {
//            DIIRTPreferencesPlugin.LOGGER.log(Level.WARNING, "Unable to flush preference store.", ex);
//        }
//
//    }

//    /**
//     * Update current defaults and values from the stored ones.
//     *
//     * @param store The preference store.
//     */
//    public void updateValues ( IPreferenceStore store ) {
//        DataSources.updateValues(store);
//        ChannelAccess.updateValues(store);
//    }

//    @Override
//    protected void initializeDefaultPreferences ( IPreferenceStore store ) {
//
//        // Don't move the following statement: it is required at this time.
//        String diirtHome = getDIIRTHome(store);
//
//        synchronized ( this ) {
//            if ( !store.getBoolean(PREF_DEFAULT_INITIALIZED) ) {
//
//                if ( verifyDIIRTPath(diirtHome) == null ) {
//                    updateDefaultsAndValues(diirtHome, store);
//                }
//
//                store.setValue(PREF_DEFAULT_INITIALIZED, true);
//
//                try {
//                    ((IPersistentPreferenceStore) store).save();
//                } catch ( IOException ex ) {
//                  DIIRTPreferencesPlugin.LOGGER.log(Level.WARNING, "Unable to flush preference store.", ex);
//                }
//
//            } else {
//                updateValues(store);
//            }
//        }
//
//    }

//    private String getDIIRTHome ( IPreferenceStore store ) {
//
//        String diirtHome = store.getString(PREF_CONFIGURATION_DIRECTORY);
//
//        try {
//
//            String resolvedDir = resolvePlatformPath(diirtHome);
//
//            if ( !StringUtils.equals(diirtHome, resolvedDir) ) {
//
//                LOGGER.log(Level.CONFIG, "DIIRT home path resolved [before: {0}, after: {1}].", new Object[] { diirtHome, resolvedDir });
//
//                diirtHome = resolvedDir;
//
//                store.putValue(PREF_CONFIGURATION_DIRECTORY, diirtHome);
//
//                try {
//                    ( (IPersistentPreferenceStore) store ).save();
//                } catch ( IOException ex ) {
//                    DIIRTPreferencesPlugin.LOGGER.log(Level.WARNING, "Unable to flush preference store.", ex);
//                }
//
//            }
//
//        } catch ( IOException ex ) {
//            LOGGER.log(Level.WARNING, MessageFormat.format("Unable to resolve DIIRT home [{0}].", diirtHome), ex);
//        }
//
//        return diirtHome;
//
//    }

}
