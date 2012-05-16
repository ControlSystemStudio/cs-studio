/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.commandimpl;

import org.csstudio.scan.command.ScriptCommandContext;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.server.ScanContext;

/** Implementation of the {@link ScriptCommandContext}
 *
 *  <p>Exposes what's needed for scripts from the {@link ScanContext}
 *
 *  @author Kay Kasemir
 */
public class ScriptCommandContextImpl extends ScriptCommandContext
{
	final private ScanContext context;

	/** Initialize
	 *  @param context {@link ScanContext} of the command executing the script
	 */
	public ScriptCommandContextImpl(final ScanContext context)
	{
		this.context = context;
	}

	/** {@inheritDoc} */
	@Override
	public ScanData getScanData() throws Exception
	{
		return context.getScanData();
	}

	/** {@inheritDoc} */
	@Override
	public void write(final String device_name, final Object value, final String readback,
	        final boolean wait, final double tolerance, final double timeout) throws Exception
	{
		context.write(device_name, value, readback, wait, tolerance, timeout);
	}
}
