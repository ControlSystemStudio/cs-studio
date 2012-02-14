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

import org.csstudio.scan.device.DeviceInfo;
import org.w3c.dom.Element;

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
 *  <p>The loop checks if the device actually reaches the desired value
 *  with a timeout.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LoopCommand extends ScanCommand
{
    /** Configurable properties of this command */
    final private static ScanCommandProperty[] properties = new ScanCommandProperty[]
    {
        new ScanCommandProperty("device_name", "Device Name", DeviceInfo.class),
        new ScanCommandProperty("start", "Initial Value", Double.class),
        new ScanCommandProperty("end", "Final Value", Double.class),
        new ScanCommandProperty("step_size", "Step Size", Double.class),
        ScanCommandProperty.READBACK,
        ScanCommandProperty.WAIT,
        ScanCommandProperty.TOLERANCE,
        ScanCommandProperty.TIMEOUT,
    };

    private String device_name;
    private double start;
    private double end;
    private double stepsize;
    private String readback = "";
    private boolean wait = true;
    private double tolerance = 0.1;
    private double timeout = 0.0;

	private List<ScanCommand> body;

    /** Initialize empty loop */
    public LoopCommand()
    {
        this("device", 0, 10, 1, new ScanCommand[0]);
    }

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
        this.start = start;
        this.end = end;
        setStepSize(stepsize);
        this.body = Arrays.asList(body);
    }

    /** Initialize
     *  @param device_name Device to update with the loop variable
     *  @param start Initial loop value
     *  @param end Final loop value
     *  @param stepsize Increment of the loop variable
     *  @param body Loop body commands
     */
    public LoopCommand(final String device_name, final double start,
            final double end, final double stepsize,
            final List<ScanCommand> body)
    {
        this.device_name = device_name;
        setStepSize(stepsize);
        this.start = start;
        this.end = end;
        this.body = body;
    }

    /** {@inheritDoc} */
    @Override
    public ScanCommandProperty[] getProperties()
    {
        return properties;
    }

	/** @return Device name */
    public String getDeviceName()
    {
        return device_name;
    }

    /** @param device_name Name of device */
    public void setDeviceName(final String device_name)
    {
        this.device_name = device_name;
    }

    /** @return Loop start value */
    public double getStart()
    {
        return start;
    }

    /** @param start Initial loop value */
    public void setStart(final Double start)
    {
        this.start = start;
    }

    /** @return Loop end value */
    public double getEnd()
    {
        return end;
    }

    /** @param end Final loop value */
    public void setEnd(final Double end)
    {
        this.end = end;
    }

    /** @return Loop step size */
    public double getStepSize()
    {
        return stepsize;
    }

    /** @param stepsize Increment of the loop variable */
    public void setStepSize(final Double stepsize)
    {
        if (stepsize != 0.0)
            this.stepsize = stepsize;
        else
            this.stepsize = 1.0;
        // Use fraction of stepsize for tolerance
        tolerance = Math.abs(this.stepsize / 10.0);
    }

    /** @return Name of readback device */
    public String getReadback()
    {
        return readback;
    }

    /** @param readback Name of readback device */
    public void setReadback(final String readback)
    {
        this.readback = readback;
    }

    /** @return Wait for readback to match? */
    public boolean getWait()
    {
        return wait;
    }

    /** @param wait Wait for readback to match? */
    public void setWait(final Boolean wait)
    {
        this.wait = wait;
    }

    /** @return Tolerance */
    public double getTolerance()
    {
        return tolerance;
    }

    /** @param tolerance Tolerance */
    public void setTolerance(final Double tolerance)
    {
        this.tolerance = Math.max(0.0, tolerance);
    }

    /** @return Timeout in seconds */
    public double getTimeout()
    {
        return timeout;
    }

    /** @param timeout Time out in seconds */
    public void setTimeout(final Double timeout)
    {
        this.timeout = Math.max(0.0, timeout);
    }

    /** @return Descriptions for loop body */
    public List<ScanCommand> getBody()
    {
        return body;
    }

    /** @param body Loop body commands */
    public void setBody(final List<ScanCommand> body)
    {
        this.body = body;
    }

    /** {@inheritDoc} */
    @Override
    public void writeXML(final PrintStream out, final int level)
    {
        writeIndent(out, level);
        out.println("<loop>");
        writeIndent(out, level+1);
        out.println("<device>" + device_name + "</device>");
        writeIndent(out, level+1);
        out.println("<start>" + start + "</start>");
        writeIndent(out, level+1);
        out.println("<end>" + end + "</end>");
        writeIndent(out, level+1);
        out.println("<step>" + stepsize + "</step>");
        writeIndent(out, level+1);
        if (! readback.isEmpty())
        {
            writeIndent(out, level+1);
            out.println("<readback>" + readback + "</readback>");
        }
        if (! wait)
        {
            writeIndent(out, level+1);
            out.println("<wait>" + wait + "</wait>");
        }
        if (tolerance > 0.0)
        {
            writeIndent(out, level+1);
            out.println("<tolerance>" + tolerance + "</tolerance>");
        }
        if (timeout > 0.0)
        {
            writeIndent(out, level+1);
            out.println("<timeout>" + timeout + "</timeout>");
        }
        out.println("<body>");
        for (ScanCommand cmd : body)
            cmd.writeXML(out, level + 2);
        writeIndent(out, level+1);
        out.println("</body>");
        writeIndent(out, level);
        out.println("</loop>");
    }

    /** {@inheritDoc} */
    @Override
    public void readXML(final SimpleScanCommandFactory factory, final Element element) throws Exception
    {
        // Read body first, so we don't update other loop params if this fails
        final Element body_node = DOMHelper.findFirstElementNode(element.getFirstChild(), "body");
        final List<ScanCommand> body = factory.readCommands(body_node.getFirstChild());

        setDeviceName(DOMHelper.getSubelementString(element, "device"));
        setStart(DOMHelper.getSubelementDouble(element, "start"));
        setEnd(DOMHelper.getSubelementDouble(element, "end"));
        setStepSize(DOMHelper.getSubelementDouble(element, "step"));
        setReadback(DOMHelper.getSubelementString(element, "readback", ""));
        setWait(Boolean.parseBoolean(DOMHelper.getSubelementString(element, "wait", "true")));
        setTolerance(DOMHelper.getSubelementDouble(element, "tolerance", 0.1));
        setTimeout(DOMHelper.getSubelementDouble(element, "timeout", 0.0));
        setBody(body);
    }

    /** {@inheritDoc} */
	@Override
	public String toString()
	{
        final StringBuilder buf = new StringBuilder();
        buf.append("Loop '").append(device_name).append("' = ")
            .append(start).append(" ... ").append(end).append(", step ").append(stepsize);
        if (wait)
        {
            buf.append(" (wait for '");
            if (readback.isEmpty())
                buf.append(device_name);
            else
                buf.append(readback);
            if (tolerance > 0)
                buf.append("' +-").append(tolerance);
            if (timeout > 0)
                buf.append(", ").append(timeout).append(" sec timeout");
            buf.append(")");
        }
        return buf.toString();
	}
}
