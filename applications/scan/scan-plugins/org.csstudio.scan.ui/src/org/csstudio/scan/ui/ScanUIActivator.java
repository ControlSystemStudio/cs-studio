/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui;

import java.util.logging.Logger;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin activator (not really used as such, just holds IDs)
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanUIActivator
{
	/** Plug-in ID defined in MANIFEST.MF */
    public static final String PLUGIN_ID = "org.csstudio.scan.ui";

	/** ID of the Scan Monitor View */
    public static final String ID_SCAN_MONITOR_VIEW = "org.csstudio.scan.ui.scanmonitor";

    /** ID of the Scan Monitor View */
    public static final String ID_SCAN_PLOT_VIEW = "org.csstudio.scan.ui.plot.view";

    /** The Constant logger. */
    final private static Logger logger = Logger.getLogger(PLUGIN_ID);
    
	/** @param path Path to plugin image
	 *  @return {@link ImageDescriptor}
	 */
    public static ImageDescriptor getImageDescriptor(final String path)
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
    
	/** @return Logger for plugin ID */
	public static Logger getLogger()
	{
	    return logger;
	}
    
}
