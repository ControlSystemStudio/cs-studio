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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.csstudio.scan.command.Comparison;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.condition.NumericValueCondition;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.SimulatedDevice;
import org.csstudio.scan.device.VTypeHelper;
import org.csstudio.scan.log.DataLog;
import org.csstudio.scan.server.JythonSupport;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanCommandImplTool;
import org.csstudio.scan.server.ScanContext;
import org.csstudio.scan.server.SimulationContext;
import org.epics.util.time.TimeDuration;

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
     *  @param jython Jython interpreter, may be <code>null</code>
     */
    public LoopCommandImpl(final LoopCommand command, final JythonSupport jython) throws Exception
    {
        super(command, jython);
        reverse = (command.getStart() <= command.getEnd()  &&  command.getStepSize() < 0) ||
                (command.getStart() >= command.getEnd()  &&  command.getStepSize() > 0);
        implementation = ScanCommandImplTool.getInstance().implement(command.getBody(), jython);
    }

    /** Initialize without Jython support
     *  @param command Command description
     */
    public LoopCommandImpl(final LoopCommand command) throws Exception
    {
        this(command, null);
    }
    
    /** {@inheritDoc} */
	@Override
    public long getWorkUnits()
    {
        final long iterations = 1 + Math.round(Math.abs((command.getEnd() - command.getStart()) / command.getStepSize()));
        long body_units = 0;
        for (ScanCommandImpl<?> command : implementation)
            body_units += command.getWorkUnits();
        if (body_units == 0)
            return iterations;
        return iterations * body_units;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getDeviceNames(final ScanContext context) throws Exception
    {
        final Set<String> device_names = new HashSet<String>();
        device_names.add(context.resolveMacros(command.getDeviceName()));
        if (! command.getReadback().isEmpty())
            device_names.add(context.resolveMacros(command.getReadback()));
        for (ScanCommandImpl<?> command : implementation)
        {
            final String[] names = command.getDeviceNames(context);
            for (String name : names)
                device_names.add(name);
        }
        return device_names.toArray(new String[device_names.size()]);
    }

    private double getLoopStart()
    {
    	return Math.min(command.getStart(), command.getEnd());
    }
    private double getLoopEnd()
    {
    	return Math.max(command.getStart(), command.getEnd());
    }
    private double getLoopStep()
    {
    	final double step  = direction * command.getStepSize();
		// Revert direction for next iteration of the complete loop?
		if (reverse)
		    direction = -direction;
		return step;
    }

	/** {@inheritDoc} */
	@Override
    public void simulate(final SimulationContext context) throws Exception
    {
		final SimulatedDevice device = context.getDevice(context.resolveMacros(command.getDeviceName()));

		final double start = getLoopStart();
        final double end   = getLoopEnd();
        final double step  = getLoopStep();
		if (step > 0)
    		for (double value = start; value <= end; value += step)
    			simulateStep(context, device, value);
		else // step is < 0, so stepping down
            for (double value = end; value >= start; value += step)
            	simulateStep(context, device, value);
    }

	/** Simulate one step in the loop iteration
	 *  @param context {@link SimulationContext}
	 *  @param device {@link SimulatedDevice} that the loop modifies
	 *  @param value Value of the loop variable for this iteration
	 *  @throws Exception on error
	 */
    private void simulateStep(final SimulationContext context,
            final SimulatedDevice device, final double value) throws Exception
    {
		// Get previous value
		final double original = device.readDouble();

		// Estimate execution time
		final double time_estimate = command.getWait()
				? device.getChangeTimeEstimate(value)
				: 0.0;

		// Show command
		final StringBuilder buf = new StringBuilder();
	    buf.append("Loop '").append(command.getDeviceName()).append("' = ").append(value);
	    command.appendConditionDetail(buf);
	    if (! Double.isNaN(original))
	    	buf.append(" [was ").append(original).append("]");
	    context.logExecutionStep(context.resolveMacros(buf.toString()), time_estimate);

    	// Set to (simulated) new value
    	device.write(value);

        // Simulate loop body
    	context.simulate(implementation);
    }

	/** {@inheritDoc} */
	@Override
	public void execute(final ScanContext context) throws Exception
	{
		final Device device = context.getDevice(context.resolveMacros(command.getDeviceName()));

	      // Separate read-back device, or use 'set' device?
        final Device readback;
        if (command.getReadback().isEmpty())
            readback = device;
        else
            readback = context.getDevice(context.resolveMacros(command.getReadback()));

        //  Wait for the device to reach the value?
        final NumericValueCondition condition;
        if (command.getWait())
            condition = new NumericValueCondition(readback, Comparison.EQUALS,
                        command.getStart(),
                        command.getTolerance(),
                        TimeDuration.ofSeconds(command.getTimeout()));
        else
            condition = null;

		final double start = getLoopStart();
        final double end   = getLoopEnd();
        final double step  = getLoopStep();
		if (step > 0)
    		for (double value = start; value <= end; value += step)
    		    executeStep(context, device, condition, readback, value);
		else // step is < 0, so stepping down
            for (double value = end; value >= start; value += step)
                executeStep(context, device, condition, readback, value);
	}

	/** Execute one step of the loop
	 *  @param context
	 *  @param device
	 *  @param condition
	 *  @param readback
	 *  @param value
	 *  @throws Exception
	 */
    private void executeStep(final ScanContext context, final Device device,
            final NumericValueCondition condition, final Device readback, double value)
            throws Exception
    {
        // Set device to value for current step of loop
        device.write(value);

        // .. wait for device to reach value
        if (condition != null)
        {
            condition.setDesiredValue(value);
            condition.await();
        }

        // Log the device's value?
        if (context.isAutomaticLogMode())
        {
            final DataLog log = context.getDataLog();
	        final long serial = log.getNextScanDataSerial();
	        log.log(readback.getAlias(), VTypeHelper.createSample(serial, readback.read()));
        }

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
