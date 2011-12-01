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

import org.csstudio.scan.command.WaitForValueCommand;
import org.csstudio.scan.condition.DeviceValueCondition;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.server.ScanContext;
import org.csstudio.scan.server.ScanServer;

/** {@link CommandImpl} that delays the scan until a device reaches a certain value
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class WaitForValueCommandImpl extends WaitForValueCommand implements CommandImpl
{
    /** Serialization ID */
    private static final long serialVersionUID = ScanServer.SERIAL_VERSION;

    /** Initialize
     *  @param device_name Name of device to check
     *  @param desired_value Desired value of the device
     *  @param tolerance Numeric tolerance when checking value
     */
	public WaitForValueCommandImpl(final String device_name,
	        final double desired_value, final double tolerance)
    {
	    super(device_name, desired_value, tolerance);
    }

	/** Initialize
	 *  @param command Command description
	 */
    public WaitForValueCommandImpl(final WaitForValueCommand command)
    {
        this(command.getDeviceName(), command.getDesiredValue(), command.getTolerance());
    }

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
		Logger.getLogger(getClass().getName()).log(Level.FINE, "Wait for {0} to reach {1}",
				new Object[] { getDeviceName(), getDesiredValue() });
        final Device device = context.getDevice(getDeviceName());

        final DeviceValueCondition condition = new DeviceValueCondition(device, getDesiredValue(), getTolerance());
        condition.await();
        context.workPerformed(1);
    }
}
