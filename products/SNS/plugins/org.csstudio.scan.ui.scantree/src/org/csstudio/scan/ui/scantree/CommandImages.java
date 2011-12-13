/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import org.csstudio.scan.command.ScanCommand;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/** Image registry of icons for scan commands
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CommandImages
{
    final private static ImageRegistry registry = new ImageRegistry();

    public static Image getImage(final ScanCommand command)
    {
        final String command_name = command.getClass().getName();
        Image image = registry.get(command_name);
        if (image == null)
        {
            // Construct image name for class name
            int sep = command_name.lastIndexOf('.');
            final String imagefile = "icons/" + command_name.substring(sep+1).toLowerCase() + ".gif";
            final ImageDescriptor descriptor = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, imagefile);
            if (descriptor == null)
                return null;
            // Remember in registry
            registry.put(command_name, descriptor);
            image = registry.get(command_name);
        }
        return image;
    }
}
