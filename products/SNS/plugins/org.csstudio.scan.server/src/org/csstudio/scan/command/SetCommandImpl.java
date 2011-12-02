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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.server.ScanContext;
import org.csstudio.scan.server.ScanServer;

/** {@link CommandImpl} that sets a device to a value
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SetCommandImpl extends SetCommand implements CommandImpl
{
    /** Serialization ID */
    private static final long serialVersionUID = ScanServer.SERIAL_VERSION;

    /** Initialize
     *  @param device_name Name of device
     *  @param value Value to write to the device
     */
    public SetCommandImpl(final String device_name, Object value)
    {
        super(device_name, value);
    }

    /** Initialize
     *  @param command Command description
     */
    public SetCommandImpl(final SetCommand command)
    {
        this(command.getDeviceName(), command.getValue());
    }

    /** {@inheritDoc} */
	@Override
    public int getWorkUnits()
    {
        return 1;
    }

	/** {@inheritDoc} */
	@Override
    public void execute(final ScanContext context)  throws Exception
    {
		Logger.getLogger(getClass().getName()).log(Level.FINE, "Set {0} to {1}",
				new Object[] { getDeviceName(), getValue() });
		final Device device = context.getDevice(getDeviceName());
		device.write(getValue());

		// Note we do NOT wait for the device to reach the value
		// If that is desired, a separate WaitForValueCommand is needed
		context.workPerformed(1);
    }
}
