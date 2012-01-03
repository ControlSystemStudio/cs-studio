/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin activator
 *  @author Kay Kasemir
 */
public class Activator extends AbstractUIPlugin
{
	/** Plug-in ID defined in MANIFEST.MF */
	public static final String PLUGIN_ID = "org.csstudio.scan.ui.scantree"; //$NON-NLS-1$
	
	/** @param path Path to plugin image
	 *  @return {@link ImageDescriptor}
	 */
    public static ImageDescriptor getImageDescriptor(final String path)
    {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
}
