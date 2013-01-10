/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.product;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin-related, not really an activator at this point
 *  @author Kay Kasemir
 */
public class Activator
{
	/** Plugin ID defined in MANIFEST.MF */
	final public static String PLUGIN_ID = "org.csstudio.utility.product"; //$NON-NLS-1$

	/** @param path Image file path within plugin
	 *  @return {@link ImageDescriptor}
	 */
	public static ImageDescriptor getImageDescriptor(final String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
