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

import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.device.SimulatedDevice;
import org.csstudio.scan.server.JythonSupport;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanCommandUtil;
import org.csstudio.scan.server.ScanContext;
import org.csstudio.scan.server.SimulationContext;
import org.epics.util.time.TimeDuration;

/** {@link ScanCommandImpl} that sets a device to a value
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SetCommandImpl extends ScanCommandImpl<SetCommand>
{
    /** {@inheritDoc} */
    public SetCommandImpl(final SetCommand command, final JythonSupport jython) throws Exception
    {
        super(command, jython);
    }

    /** Implemet without Jython support */
    public SetCommandImpl(final SetCommand command) throws Exception
    {
        this(command, null);
    }
    
    /** {@inheritDoc} */
    @Override
    public String[] getDeviceNames(final ScanContext context) throws Exception
    {
        final String readback = command.getReadback();
        if (readback.isEmpty())
            return new String[] { context.resolveMacros(command.getDeviceName()) };
        return new String[] { context.resolveMacros(command.getDeviceName()), context.resolveMacros(readback) };
    }

	/** {@inheritDoc} */
	@Override
    public void simulate(final SimulationContext context) throws Exception
    {
		final SimulatedDevice device = context.getDevice(context.resolveMacros(command.getDeviceName()));

		// Get previous and desired values
		final double original = device.readDouble();
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
    	context.logExecutionStep(context.resolveMacros(buf.toString()), time_estimate);

    	// Set to (simulated) new value
    	device.write(command.getValue());
    }

	/** {@inheritDoc} */
	@Override
    public void execute(final ScanContext context)  throws Exception
    {
	    ScanCommandUtil.write(context,
	            command.getDeviceName(), command.getValue(),
				command.getReadback(), command.getWait(),
				command.getTolerance(),
				TimeDuration.ofSeconds(command.getTimeout()));
		context.workPerformed(1);
    }
}
