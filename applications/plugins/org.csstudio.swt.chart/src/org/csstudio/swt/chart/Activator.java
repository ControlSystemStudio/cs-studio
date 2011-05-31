/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin activator.
 *  @author Kay Kasemir
 */
public class Activator extends Plugin
{
    /** Plugin ID, defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.swt.chart"; //$NON-NLS-1$

    private static Activator plugin;

    /** Logger */
    private static Logger logger = Logger.getLogger(ID);

    /** Constructor */
    public Activator()
    {
        plugin = this;
    }

    /** @return The singleton instance. */
    static public Activator getDefault()
    {
        return plugin;
    }

    /** @return Logger for Plugin ID */
    public static Logger getLogger()
    {
        return logger;
    }

    /** Returns an image descriptor for the image file.
     *  <p>
     *  Usually, this is the image found via the the given plug-in
     *  relative path.
     *  But this implementation also supports a hack for testing:
     *  If no plugin is running, because for example this is an SWT-only
     *  test, the path is used as is, i.e. relative to the current
     *  directory.
     *
     *  @param path the path
     *  @return the image descriptor
     */
    @SuppressWarnings("nls")
    public static ImageDescriptor getImageDescriptor(String path)
    {
        // If the plugin is running, get descriptor from the bundle
        if (plugin != null)
            return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
        // ... otherwise, this is an SWT-only test without the plugin:
        try
        {
            final Display display = Display.getCurrent();
            final Image img = new Image(display, path);
            return ImageDescriptor.createFromImage(img);
        }
        catch (Exception e)
        {
            getLogger().log(Level.SEVERE, "Cannot load image '" + path + "'", e);
        }
        return null;
    }
}
