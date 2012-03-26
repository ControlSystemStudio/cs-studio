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
package org.csstudio.scan.commandimpl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.data.values.IValue;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.data.ScanSampleFactory;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanContext;

/** {@link ScanCommandImpl} that reads data from devices and logs it
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LogCommandImpl extends ScanCommandImpl<LogCommand>
{
	/** Initialize
	 *  @param command Command description
	 */
	public LogCommandImpl(final LogCommand command)
    {
	    super(command);
    }

    /** {@inheritDoc} */
	@Override
    public String[] getDeviceNames()
	{
	    return command.getDeviceNames();
	}

	/** {@inheritDoc} */
	@Override
	public void execute(final ScanContext context) throws Exception
	{
        Logger.getLogger(getClass().getName()).log(Level.FINE, "{0}", command);
		final Logger logger = Logger.getLogger(getClass().getName());

		final long serial = ScanSampleFactory.getNextSerial();
		final String[] device_names = command.getDeviceNames();
		final int length = device_names.length;
		for (int i=0; i<length; ++i)
		{
			final String device_name = device_names[i];
			final Device device = context.getDevice(device_name);
			final IValue value = device.read();
			logger.log(Level.FINER, "Log: {0} = {1}",
					new Object[] { device.toString(), value });
			context.logSample(ScanSampleFactory.createSample(device_name, serial, value));
		}
        context.workPerformed(1);
	}
}
