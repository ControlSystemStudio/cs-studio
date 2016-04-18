/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Plugin extends AbstractUIPlugin {
    /** The plug-in ID */
    final public static String ID = "org.csstudio.display.pvtable";

    /** Logger */
    final private static Logger logger = Logger.getLogger(ID);

    /** The shared instance */
    private static Plugin plugin;

    /** The constructor. */
    public Plugin() {
        if (plugin != null)
            throw new IllegalStateException("Plugin is singleton");
        plugin = this;
    }

    /** @return Returns the shared instance. */
    public static Plugin getDefault() {
        return plugin;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /** @return Logger for plugin ID */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * @return Returns an image descriptor for the image file at the given
     *         plug-in relative path.
     * @param path
     *            The path
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        if (plugin == null) { // Support JUnit demo without plugin context
            final File pwd = new File(".");
            try {
                return ImageDescriptor.createFromURL(new URL("file://" + pwd.getAbsolutePath() + "/" + path));
            } catch (MalformedURLException e) {
                return null;
            }
        }
        return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
    }
}
