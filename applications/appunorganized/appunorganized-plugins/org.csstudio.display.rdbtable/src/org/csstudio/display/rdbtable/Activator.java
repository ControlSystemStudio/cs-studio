/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.rdbtable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/** Plug-in activator
 *  @author Kay Kasemir
 */
public class Activator extends AbstractUIPlugin
{
    /** Plugin ID registered in MANIFEST.MF */
    final public static String ID = "org.csstudio.display.rdbtable"; //$NON-NLS-1$

    /** Creates and returns a new image descriptor for an image file located
     *  within this plug-in.
     *
     * @param image_file_path
     *            the relative path of the image file, relative to the root of
     *            the plug-in; the path must be legal
     * @return an image descriptor, or <code>null</code> if no image could be
     *         found
     * @see AbstractUIPlugin#imageDescriptorFromPlugin(String, String)
     */
    public static ImageDescriptor getImageDescriptor(final String image_file_path)
    {
        return imageDescriptorFromPlugin(ID, image_file_path);
    }
}
