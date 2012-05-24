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

import org.csstudio.scan.command.WaitCommand;
import org.csstudio.scan.condition.DeviceValueCondition;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.SimulatedDevice;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanContext;
import org.csstudio.scan.server.SimulationContext;

/** {@link ScanCommandImpl} that delays the scan until a device reaches a certain value
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class WaitCommandImpl extends ScanCommandImpl<WaitCommand>
{
	/** Initialize
	 *  @param command Command description
	 */
    public WaitCommandImpl(final WaitCommand command)
    {
        super(command);
    }

    /** {@inheritDoc} */
    @Override
    public String[] getDeviceNames()
    {
        return new String[] { command.getDeviceName() };
    }

	/** {@inheritDoc} */
	@Override
    public void simulate(final SimulationContext context) throws Exception
    {
		final SimulatedDevice device = context.getDevice(command.getDeviceName());

		// Estimate execution time
		double original = device.readDouble();
		final double desired_value;
		switch (command.getComparison())
		{
		case INCREASE_BY:
			if (Double.isNaN(original))
			{
				original = 0.0;
		    	device.write(original);
			}
			desired_value = original + command.getDesiredValue();
			break;
		case DECREASE_BY:
			if (Double.isNaN(original))
			{
				original = 0.0;
		    	device.write(original);
			}
			desired_value = original - command.getDesiredValue();
			break;
		default:
			desired_value = command.getDesiredValue();
			break;
		}
		final double time_estimate = device.getChangeTimeEstimate(desired_value);

		// Show command
		final StringBuilder buf = new StringBuilder();
	    buf.append(command.toString());
	    if (! Double.isNaN(original))
	    	buf.append(" [was ").append(original).append("]");
    	context.logExecutionStep(buf.toString(), time_estimate);

    	// Set to (simulated) new value
    	device.write(desired_value);
    }

    /** {@inheritDoc} */
	@Override
    public void execute(final ScanContext context) throws Exception
    {
        final Device device = context.getDevice(command.getDeviceName());

        final DeviceValueCondition condition =
            new DeviceValueCondition(device, command.getComparison(),
                    command.getDesiredValue(), command.getTolerance(),
                    command.getTimeout());
        condition.await();
        context.workPerformed(1);
    }
}
