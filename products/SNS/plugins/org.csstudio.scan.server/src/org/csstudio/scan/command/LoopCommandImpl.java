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

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.condition.DeviceValueCondition;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.server.ScanContext;
import org.csstudio.scan.server.ScanServer;

/** Command that performs a loop
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LoopCommandImpl extends LoopCommand implements CommandImpl
{
    /** Serialization ID */
    private static final long serialVersionUID = ScanServer.SERIAL_VERSION;

    final private boolean reverse;
	final private List<CommandImpl> implementation;

	/** Initialize
	 *  @param device_name Device to update with the loop variable
	 *  @param start Initial loop value
	 *  @param end Final loop value
	 *  @param stepsize Increment of the loop variable
	 *  @param body Loop body commands
	 *  @throws Exception on error implementing the loop body
	 */
	public LoopCommandImpl(final String device_name, final double start,
			final double end, final double stepsize,
			final List<ScanCommand> body) throws Exception
    {
	    super(device_name, start, end, stepsize, body);
		reverse = (start <= end  &&  this.stepsize < 0) ||
		               (start >= end  &&  this.stepsize > 0);
		implementation = CommandImplFactory.implement(body);
    }

	/** Initialize
     *  @param device_name Device to update with the loop variable
     *  @param start Initial loop value
     *  @param end Final loop value
     *  @param stepsize Increment of the loop variable
     *  @param body Optional loop body commands
     *  @throws Exception on error implementing the loop body
     */
    public LoopCommandImpl(final String device_name, final double start,
            final double end, final double stepsize,
            final ScanCommand... body) throws Exception
    {
        this(device_name, start, end, stepsize, Arrays.asList(body));
    }
	
    /** Initialize
     *  @param command Command description
     */
    public LoopCommandImpl(final LoopCommand command) throws Exception
    {
        this(command.getDeviceName(), command.getStart(), command.getEnd(),
             command.getStepSize(), command.getBody());
    }

    /** {@inheritDoc} */
	@Override
    public int getWorkUnits()
    {
        final int iterations = 1 + (int) Math.round(Math.abs((end - start) / stepsize));
        int body_units = 0;
        for (CommandImpl command : implementation)
            body_units += command.getWorkUnits();
        if (body_units == 0)
            return iterations;
        return iterations * body_units;
    }

    /** {@inheritDoc} */
	@Override
	public void execute(final ScanContext context) throws Exception
	{
		final Device device = context.getDevice(device_name);
        final DeviceValueCondition reach_value =
                new DeviceValueCondition(device, start, stepsize/10.0);

		Logger.getLogger(getClass().getName()).log(Level.FINE,
				"Loop: {0} = {1} .. {2}, stepping {3}",
				new Object[] { device_name, start, end, stepsize });

		final double start = Math.min(this.start, this.end);
        final double end   = Math.max(this.start, this.end);

		if (stepsize > 0)
    		for (double value = start; value <= end; value += stepsize)
    		    executeStep(context, device, reach_value, value);
		else // Stepsize is < 0, so stepping down
            for (double value = end; value >= start; value += stepsize)
                executeStep(context, device, reach_value, value);

		// Revert direction for next iteration of the complete loop?
		if (reverse)
		    stepsize = -stepsize;
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
        // Execute loop body
        try
        {
        	context.execute(implementation);
        }
        catch (InterruptedException ex)
        {   // Pass interruption of body command up
            throw ex;
        }
        catch (Throwable ex)
        {
        	final String message = toString() + " body failed";
        	Logger.getLogger(getClass().getName()).log(Level.WARNING, message, ex);
        	throw new Exception(message, ex);
        }
        // If there are no commands that inc. the work units, do it yourself
        if (implementation.size() <= 0)
            context.workPerformed(1);
    }

    /** {@inheritDoc} */
	@Override
	public String toString()
	{
	    return "Loop '" + device_name + "' = " + start + " ... " + end + ", step " + stepsize +
	            (reverse ? " (reversing)" : "");
	}
}
