/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Not really activator, just holds ID etc.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Activator
{
	/** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.utility.chat";
    
    /** @param path Path within plugin to image
     *  @return {@link ImageDescriptor}
     */
    public static ImageDescriptor getImage(final String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
	}
}
