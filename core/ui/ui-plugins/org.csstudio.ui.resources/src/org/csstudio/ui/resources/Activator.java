/*******************************************************************************
 * Copyright (c) 2010-2015 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.ui.resources;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Activator {
   
    /** Plug-in ID defined in MANIFEST.MF */
    public static final String ID = "org.csstudio.ui.resources"; //$NON-NLS-1$

    /**
     * @param path the path to the image file relative to the root of this plugin
     * @return an image descriptor for the image file at the given plug-in relative path.
     * 
     */
    public static ImageDescriptor getImageDescriptor(final String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
    }
}