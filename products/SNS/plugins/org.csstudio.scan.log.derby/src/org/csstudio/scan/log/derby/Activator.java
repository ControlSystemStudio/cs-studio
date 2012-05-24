/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log.derby;

import java.io.FileInputStream;
import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/** Plugin activator
 *
 *  <p>Starts and stops embedded Derby instance
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Activator implements BundleActivator
{
	/** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.scan.log.derby";

	private static Bundle bundle = null;

	// Please FindBugs about static access
	private static void setBundle(final Bundle bundle)
	{
		Activator.bundle = bundle;
	}

	/** {@inheritDoc} */
	@Override
    public void start(final BundleContext context) throws Exception
    {
		setBundle(context.getBundle());
		DerbyDataLogger.startup();
    }

	/** {@inheritDoc} */
	@Override
    public void stop(final BundleContext context) throws Exception
    {
		DerbyDataLogger.shutdown();
    }

	/** Open stream to file, either from bundle when running as plugin
	 *  or via direct file access when running as test
	 *  @param filename File name within bundle
	 *  @return InputStream
	 *  @throws Exception on error
	 */
	public static InputStream openStream(final String filename) throws Exception
    {
		if (bundle == null)
			return new FileInputStream(filename);
		else
			return FileLocator.openStream(bundle, new Path(filename), false);
    }
}
