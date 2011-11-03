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
import java.util.Arrays;
import java.util.List;

import org.csstudio.scan.server.ScanServer;

/** Command that performs a loop
 *
 *  <p>The loop steps from a start to an end value
 *  by some step size, for example 1 to 5 by 1: 1, 2, 3, 4, 5.
 *
 *  <p>It stops at the end value, for example 1 to 6 by 2: 1, 3, 5
 *
 *  <p>When the start is larger than the end and the step size is negative,
 *  it will ramp down,
 *  for example from 5 to 1 by -1: 5, 4, 3, 2, 1.
 *
 *  <p>When the order of start and end does not match the step direction,
 *  for example the start is smaller than the end,
 *  but the step is negative,
 *  this enables a 'reverse' toggle:
 *  The direction of the loop will change every time it is executed.
 *
 *  @author Kay Kasemir
 */
public class LoopCommand extends BaseCommand
{
    /** Serialization ID */
    final  private static long serialVersionUID = ScanServer.SERIAL_VERSION;

    final private String device_name;
	final private double start;
	final private double end;
	final private double stepsize;
	final private List<ScanCommand> body;

	/** Initialize
     *  @param device_name Device to update with the loop variable
     *  @param start Initial loop value
     *  @param end Final loop value
     *  @param stepsize Increment of the loop variable
     *  @param body Optional loop body commands
     */
    public LoopCommand(final String device_name, final double start,
            final double end, final double stepsize,
            final ScanCommand... body)
    {
        this.device_name = device_name;
        this.stepsize = stepsize;
        this.start = start;
        this.end = end;
        this.body = Arrays.asList(body);
    }

	/** @return Device name */
    public String getDeviceName()
    {
        return device_name;
    }

    /** @return Loop start value */
    public double getStart()
    {
        return start;
    }

    /** @return Loop end value */
    public double getEnd()
    {
        return end;
    }

    /** @return Loop step size */
    public double getStepSize()
    {
        return stepsize;
    }

    /** @return Descriptions for loop body */
    public List<ScanCommand> getBody()
    {
        return body;
    }

    /** {@inheritDoc} */
    @Override
    protected void printIndented(final PrintStream out, final int level)
    {
        super.printIndented(out, level);
        for (ScanCommand b : body)
        {   // Anticipate that Command might be implemented without BaseCommand
            if (b instanceof BaseCommand)
                ((BaseCommand)b).printIndented(out, level + 1);
            else
                b.dump(out);
        }
    }

    /** {@inheritDoc} */
	@Override
	public String toString()
	{
	    return "Loop '" + device_name + "' = " + start + " ... " + end + ", step "  + stepsize;
	}
}
