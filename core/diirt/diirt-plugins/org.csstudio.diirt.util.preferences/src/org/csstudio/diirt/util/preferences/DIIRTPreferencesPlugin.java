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
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
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

    public static final String[][] AVAILABLE_DATA_SOURCES       = {
        { "None", "none" },
        { "Channel Access", "ca" },
    };
    public static final Logger     LOGGER                       = Logger.getLogger(DIIRTPreferencesPlugin.class.getName());
    public static final String     PLATFORM_URI_PREFIX          = "platform:";
    public static final String     PREF_CONFIGURATION_DIRECTORY = "diirt.home";
    public static final String     PREF_DS_DEFAULT              = "diirt.datasource.default";
    public static final String     PREF_DS_DELIMITER            = "diirt.datasource.delimiter";
    public static final String     USER_HOME_PARAMETER          = "@user.home";

    private static DIIRTPreferencesPlugin instance = null;

    public static DIIRTPreferencesPlugin get ( ) {
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
        } else if ( !Files.exists(Paths.get(path, "datasources/datasources.xml")) ) {
            return NLS.bind(Messages.DPH_verifyDIIRTPath_pathNotValid_message, path);
        }

        return null;

    }

    /**
     * Return a valid path string (to be used with {@link File}'s methods) resolving
     * the given {@code path} against the {@link #PLATFORM_URI_PREFIX} protocol.
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

}
