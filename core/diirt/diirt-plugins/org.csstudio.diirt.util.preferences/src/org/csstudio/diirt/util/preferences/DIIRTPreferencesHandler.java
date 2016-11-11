/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.preferences.ScopedPreferenceStore;


/**
 * Handle the DIIRT preferences, reading them from the configuration files
 * and applying the overrides.
 *
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 4 Nov 2016
 */
public final class DIIRTPreferencesHandler {

    public static final String PROP_CONFIGURATIONDIRECTORY = "configurationDirectory";
    public static final String PROP_OVERRIDE               = "override";

    private static final Logger LOGGER              = Logger.getLogger(DIIRTPreferencesHandler.class.getName());
    private static final String PLATFORM_URI_PREFIX = "platform:";

    public static DIIRTPreferencesHandler get ( ) {
        return DIIRTPreferencesHandlerInstance.SINGLETON_INSTANCE;
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
        } else if (!Files.exists(Paths.get(path))) {
            return NLS.bind(Messages.DPH_verifyDIIRTPath_pathNotExists_message, path);
        } else if ( ! Files.exists(Paths.get(path, "datasources/datasources.xml")) ) {
            return NLS.bind(Messages.DPH_verifyDIIRTPath_pathNotValid_message, path);
        }

        return null;

    }

    private static String resolvePlatformPath ( String path ) throws MalformedURLException, IOException {
        if ( path != null && !path.isEmpty() ) {
            if ( path.startsWith(PLATFORM_URI_PREFIX) ) {
                return FileLocator.resolve(new URL(path)).getPath().toString();
            } else {
                return path;
            }
        } else {
            return "root";
        }
    }

    private String                configurationDirectory = null;
//    private ScopedPreferenceStore store                  = new ScopedPreferenceStore(InstanceScope.INSTANCE, "org.csstudio.diirt.util.preferences");
    private ScopedPreferenceStore store;

    private final transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private DIIRTPreferencesHandler ( ) {

        store = new ScopedPreferenceStore(InstanceScope.INSTANCE, "org.csstudio.diirt.util.preferences");

        String storedDirectory = store.getString("diirt.home");

        try {
            configurationDirectory = resolvePlatformPath(storedDirectory);
        } catch ( IOException ex ) {
            LOGGER.log(Level.WARNING, MessageFormat.format("Unable to resolve stored path [{0}]. Null is used instead.", storedDirectory), ex);
            configurationDirectory = null;
        }

        String warning = verifyDIIRTPath(configurationDirectory);

        if ( warning != null ) {
            LOGGER.warning(warning);
        }

    }

    public void addPropertyChangeListener ( PropertyChangeListener listener ) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener ( String propertyName, PropertyChangeListener listener ) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * @return The path pointing at the configuration directory, that
     *         should contain a {@code datasource} sub-directory with the
     *         {@code datasource.xml} file and a sub-sub-directory for each
     *         data source supported containing the corresponding configuration
     *         file(s).
     *         Can be {@code null}, or pointing to a non valid directory, in
     *         which case hard-coded defaults will be used for the
     *         configuration.
     */
    public String getConfigurationDirectory ( ) {
        return configurationDirectory;
    }

    public PropertyChangeListener[] getPropertyChangeListeners ( ) {
        return propertyChangeSupport.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners ( String propertyName ) {
        return propertyChangeSupport.getPropertyChangeListeners(propertyName);
    }

    public boolean hasPropertyChangeListeners ( String propertyName ) {
        return propertyChangeSupport.hasListeners(propertyName);
    }

    public void removePropertyChangeListener ( PropertyChangeListener listener ) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener ( String propertyName, PropertyChangeListener listener ) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * @param configurationDirectory The new configuration directory. Can be
     *            {@code null} or pointing to an invalid
     *            directory.
     * @see #getConfigurationDirectory()
     */
    public void setConfigurationDirectory ( String configurationDirectory ) {

        String oldConfigurationDirectory = this.configurationDirectory;

        this.configurationDirectory = configurationDirectory;

        propertyChangeSupport.firePropertyChange(PROP_CONFIGURATIONDIRECTORY, oldConfigurationDirectory, configurationDirectory);

    }

    private interface DIIRTPreferencesHandlerInstance {
        DIIRTPreferencesHandler SINGLETON_INSTANCE = new DIIRTPreferencesHandler();
    }

}
