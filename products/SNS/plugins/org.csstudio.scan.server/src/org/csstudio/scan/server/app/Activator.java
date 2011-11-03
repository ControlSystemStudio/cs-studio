/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 * 
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.server.app;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/** Plugin activator
 *  @author Kay Kasemir
 */
public class Activator extends Plugin
{
    /** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "yabes.server";
    
    /** Singleton instance */
    private static Activator instance = null;

    /** {@inheritDoc} */
	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		setInstance(this);
	}

	/** Static setter to please findbugs */
	private static void setInstance(final Activator instance)
	{
		Activator.instance = instance;
	}

	/** @return Singleton instance */
	public static Activator getInstance()
	{
		return instance;
	}
}
