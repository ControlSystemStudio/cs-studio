/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.navigator.applaunch;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin info
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Activator
{
	/** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.navigator.applaunch";
	
	/** @param path Image path
	 *  @return {@link ImageDescriptor}
	 */
	public static ImageDescriptor getImageDescriptor(final String path)
	{
		// Similar:
		// URL url = new URL("platform:/plugin/" + Activator.ID + "/" + path);
		// return  ImageDescriptor.createFromURL(url);
		return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
	}
}
