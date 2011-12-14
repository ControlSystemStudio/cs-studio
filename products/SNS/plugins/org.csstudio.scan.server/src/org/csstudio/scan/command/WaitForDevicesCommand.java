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
package org.csstudio.scan.command;

import java.io.PrintStream;
import java.util.logging.Logger;

import org.csstudio.scan.condition.Condition;
import org.csstudio.scan.condition.WaitForDevicesCondition;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.server.ScanContext;
import org.csstudio.scan.server.ScanServer;

/** {@link CommandImpl} that delays the scan until all {@link Device}s are 'ready'
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class WaitForDevicesCommand extends BaseCommand implements CommandImpl
{
    /** Serialization ID */
    private static final long serialVersionUID = ScanServer.SERIAL_VERSION;

    /** {@inheritDoc} */
    @Override
    public int getWorkUnits()
    {
        return 1;
    }

	/** {@inheritDoc} */
	@Override
    public void execute(final ScanContext context) throws Exception
    {
		Logger.getLogger(getClass().getName()).fine("Waiting for devices");

		final Condition ready = new WaitForDevicesCondition(context.getDevices());
		ready.await();
        context.workPerformed(1);
    }

    /** {@inheritDoc} */
	@Override
    public void writeXML(final PrintStream out, final int level)
    {
	    writeIndent(out, level);
	    out.println("<waitfordevices/>");
    }

    /** {@inheritDoc} */
	@Override
	public String toString()
	{
	    return "Wait for devices";
	}
}
