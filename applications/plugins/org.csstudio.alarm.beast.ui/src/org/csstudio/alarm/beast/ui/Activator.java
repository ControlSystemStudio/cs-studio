/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui;

import java.util.logging.Logger;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Not really a plugin activator, only used to get images and keep ID
 *  @author Kay Kasemir
 */
public class Activator
{
	/** Plug-in ID defined in MANIFEST.MF */
	public static final String ID = "org.csstudio.alarm.beast.ui"; //$NON-NLS-1$

    /** @return Returns an image descriptor for the image file at the given plug-in
     *  relative path.
     *  @param path The path
     */
    public static ImageDescriptor getImageDescriptor(final String path)
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
    }

    /** @return Logger for plugin ID */
    public static Logger getLogger()
    {
        return Logger.getLogger(ID);
    }
}
