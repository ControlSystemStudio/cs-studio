/*******************************************************************************
* Copyright (c) 2010-2016 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.imagelib;

import java.util.logging.Logger;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the Image Library plug-in life cycle
 * @author Greg Darcourt (Sopra Group)
 */
public class Activator extends AbstractUIPlugin {

    /**
     * The Symbol plug-in ID
     */
    public static final String PLUGIN_ID = "org.csstudio.opibuilder.imagelib";

    /**
     * The shared instance of the Symbol plug-in
     */
    private static Activator plugin;

    /**
     * The logger
     */
    private static final Logger LOGGER = Logger.getLogger(PLUGIN_ID);

    /**
     * The default constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Get the shared instance of the Symbol plug-in.
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * Get the logger for Symbol plug-in.
     * @return the logger
     */
    public static Logger getLogger() {
        return LOGGER;
    }
}
