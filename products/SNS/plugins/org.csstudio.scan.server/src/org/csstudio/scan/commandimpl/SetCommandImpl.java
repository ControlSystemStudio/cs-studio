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
import org.csstudio.data.values.ValueUtil;
import org.csstudio.scan.command.Comparison;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.condition.DeviceValueCondition;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.SimulatedDevice;
import org.csstudio.scan.device.ValueConverter;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanContext;
import org.csstudio.scan.server.SimulationContext;

/** {@link ScanCommandImpl} that sets a device to a value
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SetCommandImpl extends ScanCommandImpl<SetCommand>
{
    /** Initialize
     *  @param command Command description
     */
    public SetCommandImpl(final SetCommand command)
    {
        super(command);
    }

    /** {@inheritDoc} */
    @Override
    public String[] getDeviceNames()
    {
        final String readback = command.getReadback();
        if (readback.isEmpty())
            return new String[] { command.getDeviceName() };
        return new String[] { command.getDeviceName(), readback };
    }

	/** {@inheritDoc} */
	@Override
    public void simulate(final SimulationContext context) throws Exception
    {
		final SimulatedDevice device = context.getDevice(command.getDeviceName());

		// Get previous and desired values
		final double original = ValueUtil.getDouble(device.read());
		final double desired;
		if (command.getValue() instanceof Number)
			desired = ((Number) command.getValue()).doubleValue();
		else
			desired = Double.NaN;

		// Estimate execution time
		final double time_estimate = command.getWait()
				? device.getChangeTimeEstimate(desired)
				: 0.0;

		// Show command
		final StringBuilder buf = new StringBuilder();
	    buf.append("Set '").append(command.getDeviceName()).append("' = ").append(command.getValue());
	    command.appendConditionDetail(buf);
	    if (! Double.isNaN(original))
	    	buf.append(" [was ").append(original).append("]");
    	context.logExecutionStep(buf.toString(), time_estimate);

    	// Set to (simulated) new value
    	device.write(command.getValue());
    }

	/** {@inheritDoc} */
	@Override
    public void execute(final ScanContext context)  throws Exception
    {
		Logger.getLogger(getClass().getName()).log(Level.FINE, "{0}", command);
		final Device device = context.getDevice(command.getDeviceName());

		// Separate read-back device, or use 'set' device?
		final Device readback;
		if (command.getReadback().isEmpty())
		    readback = device;
		else
		    readback = context.getDevice(command.getReadback());

		//  Wait for the device to reach the value?
		final DeviceValueCondition condition;
		if (command.getWait())
		{
		    final double desired;
		    if (command.getValue() instanceof Number)
		        desired = ((Number)command.getValue()).doubleValue();
		    else
		        throw new Exception("Value must be numeric to support 'wait'");

		    condition = new DeviceValueCondition(readback, Comparison.EQUALS, desired,
	                command.getTolerance(), command.getTimeout());
		}
		else
		    condition = null;

		// Perform write
		device.write(command.getValue());

		// Wait?
		if (condition != null)
		    condition.await();

		// Log the value
		final IValue value = readback.read();
		context.logSample(ValueConverter.createSample(readback.getInfo().getAlias(), value));

		context.workPerformed(1);
    }
}
