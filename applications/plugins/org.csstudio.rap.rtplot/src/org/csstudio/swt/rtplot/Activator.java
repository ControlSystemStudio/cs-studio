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
import org.osgi.framework.Bundle;

/** Not an actual Plugin Activator, but providing plugin-related helpers
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Activator
{
    /** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.rap.rtplot";

    final private static Logger logger =  Logger.getLogger(ID);

    public static Logger getLogger()
    {
        return logger;
    }

    public static ImageDescriptor getIcon(final String base_name)
    {
        String path = "icons/" + base_name + ".png";
        try
        {
            final Bundle bundle = Platform.getBundle(ID);
            final URL image_url = bundle.getEntry(path);
            return ImageDescriptor.createFromURL(image_url);
            
//            return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);				
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
