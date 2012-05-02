/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log.derby;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/** Plugin activator
 *
 *  <p>Starts and stops embedded Derby instance
 *  @author Kay Kasemir
 */
public class Activator implements BundleActivator
{
	/** {@inheritDoc} */
	@Override
    public void start(final BundleContext context) throws Exception
    {
		DerbyDataLog.startup();
    }

	/** {@inheritDoc} */
	@Override
    public void stop(final BundleContext context) throws Exception
    {
		DerbyDataLog.shutdown();
    }
}
