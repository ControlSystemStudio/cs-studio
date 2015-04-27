/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine;

import java.util.logging.Logger;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/** Plugin Activator
 *  @author Kay Kasemir
 */
public class Activator extends Plugin
{
	/** Plug-in ID defined in MANIFEST.MF */
	public static final String ID = "org.csstudio.archive.engine"; //$NON-NLS-1$

	/** The shared instance */
	private static Activator plugin;

    private static Logger logger = Logger.getLogger(ID);

	/** {@inheritDoc} */
	@Override
    public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/** @return the shared instance */
	public static Activator getDefault()
	{
		return plugin;
	}

	/** @return Logger for plugin ID */
	public static Logger getLogger()
	{
	    return logger;
	}
}
