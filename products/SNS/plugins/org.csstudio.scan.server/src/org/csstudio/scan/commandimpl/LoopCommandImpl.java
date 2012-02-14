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
 * and is not getEnd()orsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.commandimpl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.command.Comparison;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.condition.DeviceValueCondition;
import org.csstudio.scan.data.ScanSampleFactory;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanCommandImplTool;
import org.csstudio.scan.server.ScanContext;

/** Command that performs a loop
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LoopCommandImpl extends ScanCommandImpl<LoopCommand>
{
    final private boolean reverse;
	final private List<ScanCommandImpl<?>> implementation;
	private int direction = 1;

    /** Initialize
     *  @param command Command description
     */
    public LoopCommandImpl(final LoopCommand command) throws Exception
    {
        super(command);
        reverse = (command.getStart() <= command.getEnd()  &&  command.getStepSize() < 0) ||
                (command.getStart() >= command.getEnd()  &&  command.getStepSize() > 0);
        implementation = ScanCommandImplTool.getInstance().implement(command.getBody());
    }

    /** {@inheritDoc} */
	@Override
    public int getWorkUnits()
    {
        final int iterations = 1 + (int) Math.round(Math.abs((command.getEnd() - command.getStart()) / command.getStepSize()));
        int body_units = 0;
        for (ScanCommandImpl<?> command : implementation)
            body_units += command.getWorkUnits();
        if (body_units == 0)
            return iterations;
        return iterations * body_units;
    }

    /** {@inheritDoc} */
	@Override
	public void execute(final ScanContext context) throws Exception
	{
        Logger.getLogger(getClass().getName()).log(Level.FINE, "{0}", command);
		final Device device = context.getDevice(command.getDeviceName());
        final DeviceValueCondition reach_value =
                new DeviceValueCondition(device, Comparison.EQUALS,
                        command.getStart(), command.getStepSize()/10.0,
                        command.getTimeout());

		final double start = Math.min(command.getStart(), command.getEnd());
        final double end   = Math.max(command.getStart(), command.getEnd());
        final double step  = direction * command.getStepSize();

		if (step > 0)
    		for (double value = start; value <= end; value += step)
    		    executeStep(context, device, reach_value, value);
		else // step is < 0, so stepping down
            for (double value = end; value >= start; value += step)
                executeStep(context, device, reach_value, value);

		// Revert direction for next iteration of the complete loop?
		if (reverse)
		    direction = -direction;
	}

	/** Execute one step of the loop
	 *  @param context
	 *  @param device
	 *  @param reach_value
	 *  @param value
	 *  @throws Exception
	 */
    private void executeStep(final ScanContext context, final Device device,
            final DeviceValueCondition reach_value, double value)
            throws Exception
    {
        // Set device to value for current step of loop
        device.write(value);
        // .. wait for device to reach value
        reach_value.setDesiredValue(value);
        reach_value.await();

        // Log the device's value
        context.logSample(ScanSampleFactory.createSample(device.getInfo().getAlias(), device.read()));

        // Execute loop body
    	context.execute(implementation);

    	// If there are no commands that inc. the work units, do it yourself
        if (implementation.size() <= 0)
            context.workPerformed(1);
    }

    /** {@inheritDoc} */
	@Override
	public String toString()
	{
	    return command.toString() + (reverse ? " (reversing)" : "");
	}
}
