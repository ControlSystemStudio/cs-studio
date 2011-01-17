/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin Activator
 *  @author Kay Kasemir
 */
public class Activator extends AbstractUIPlugin
{
    /** Plug-in ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.apputil.ui"; //$NON-NLS-1$

    /** @return Returns an image descriptor for the image file at the given plug-in
     *  relative path.
     *  @param path The path
     */
    public static ImageDescriptor getImageDescriptor(final String path)
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
    }
}
