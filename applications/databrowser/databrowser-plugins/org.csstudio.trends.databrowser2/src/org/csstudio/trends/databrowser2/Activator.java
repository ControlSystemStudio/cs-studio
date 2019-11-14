/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2;

import java.util.Dictionary;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.swt.rtplot.util.NamedThreadFactory;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.csstudio.utility.singlesource.UIHelper.UI;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** Eclipse Plugin Activator
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Activator extends AbstractUIPlugin
{
    /** Plug-in ID defined in MANIFEST.MF */
    final public static String PLUGIN_ID = "org.csstudio.trends.databrowser2";

    /** Checkbox images */
    final public static String ICON_UNCHECKED = "icons/unchecked.gif",
                               ICON_CHECKED = "icons/checked.gif";

    /** Singleton instance */
    private static Activator plugin;

    /** Logger for this plugin */
    private static Logger logger = Logger.getLogger(PLUGIN_ID);

    final public static ExecutorService thread_pool = Executors.newCachedThreadPool(new NamedThreadFactory("DataBrowserJobs"));
    final public static ExecutorService sql_thread_pool = Executors.newSingleThreadExecutor(new NamedThreadFactory("DataBrowserSQL"));

    /** Width of the display in pixels. Used to scale negative plot_bins */
    public static int display_pixel_width = 0;

    /** {@inheritDoc} */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);

        // Determine width of widest monitor
        try {
            for (Monitor monitor : Display.getCurrent().getMonitors())
            {
                final int wid = monitor.getBounds().width;
                if (wid > display_pixel_width)
                    display_pixel_width = wid;
            }
        } catch (RuntimeException e) {
            // Ignore, warning will be printed below.
        }
        if (display_pixel_width <= 0)
        {
            logger.log(Level.WARNING, "Cannot determine display pixel width, using 1000");
            display_pixel_width = 1000;
        }

        if (SingleSourcePlugin.getUIHelper().getUI() == UI.RAP)
        {
            // Is this necessary?
            // RAPCorePlugin adds the "server" scope for all plugins,
            // but starts too late...
            Platform.getPreferencesService().setDefaultLookupOrder(
                PLUGIN_ID, null,
                new String[]
                {
                        InstanceScope.SCOPE,
                        ConfigurationScope.SCOPE,
                        "server",
                        DefaultScope.SCOPE
                });
        }
        plugin = this;
    }

    /** {@inheritDoc} */
    @Override
    public void stop(BundleContext context) throws Exception
    {
        plugin = null;
        super.stop(context);
    }

    /** @return the shared instance */
    public static Activator getDefault()
    {
        return plugin;
    }

    /** @return Thread pool */
    public static ExecutorService getThreadPool()
    {
        return thread_pool;
    }

    /** @return Thread pool */
    public static ExecutorService getSqlThreadPool()
    {
        return sql_thread_pool;
    }

    /** Obtain image descriptor from file within plugin.
     *  @param path Path within plugin to image file
     *  @return {@link ImageDescriptor}
     */
    public ImageDescriptor getImageDescriptor(final String path)
    {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    /** Obtain image from file within plugin.
     *  Uses registry to avoid duplicates and for disposal
     *  @param path Path within plugin to image file
     *  @return {@link Image}
     */
    public Image getImage(final String path)
    {
        Image image = getImageRegistry().get(path);
        if (image == null)
        {
            image = getImageDescriptor(path).createImage();
            getImageRegistry().put(path, image);
        }
        return image;
    }

    /** @return Version code */
    public String getVersion()
    {
        final Dictionary<String, String> headers = getBundle().getHeaders();
        return headers.get("Bundle-Version");
    }

    /** @return Logger for this plugin */
    public static Logger getLogger()
    {
        return logger;
    }
}
