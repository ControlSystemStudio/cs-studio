/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/** Not an actual Plugin Activator, but providing plugin-related helpers
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Activator extends AbstractUIPlugin
{
    /** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.rap.rtplot";

    private static Activator plugin;

    final private static Logger logger =  Logger.getLogger(ID);

    public static Logger getLogger()
    {
        return logger;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        plugin = this;
        super.start(context);
    }

    /** @return the shared instance */
    public static Activator getDefault()
    {
        return plugin;
    }

    /** Obtain image descriptor from file within plugin.
     *  @param iconName the icon file name (located in icons folder)
     *  @return {@link ImageDescriptor}
     */
    public ImageDescriptor getImageDescriptor(String iconName)
    {
        String path = "icons/" + iconName.toLowerCase() + ".png";
        return imageDescriptorFromPlugin(ID, path);
    }

    /** Obtain image from file within plugin.
     *  Uses registry to avoid duplicates and for disposal
     *  @param iconName the icon file name
     *  @return {@link Image}
     */
    public Image getImage(String iconName)
    {
        String path = "icons/" + iconName.toLowerCase() + ".png";
        Image image = getImageRegistry().get(path);
        if (image == null)
        {
            ImageDescriptor desc = getImageDescriptor(iconName);
            getImageRegistry().put(path, desc);
            image = getImageRegistry().get(path);
        }
        return image;
    }

    @Deprecated
    public static ImageDescriptor getIcon(final String base_name)
    {
        String path = "icons/" + base_name + ".png";
        try
        {
            final Bundle bundle = Platform.getBundle(ID);
            final URL image_url = bundle.getEntry(path);
            return ImageDescriptor.createFromURL(image_url);
        }
        catch (Throwable ex)
        {   // ... otherwise, this is an SWT-only test without the plugin.
            // Might be run within the org.csstudio.ui.rtplot plugin directory
            // or the org.csstudio.ui.rtplot.test fragment dir, so
            // always go back to the plugin dir.
            path = "../" + ID + "/" + path;
            try
            {
                final Display display = Display.getCurrent();
                final Image img = new Image(display, path);
                return ImageDescriptor.createFromImage(img);
            }
            catch (Exception e)
            {
                logger.log(Level.SEVERE, "Cannot load image '" + path + "'", e);
            }
        }
        return null;
    }
}
