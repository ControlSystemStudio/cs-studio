/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.sysmon;

import org.csstudio.apputil.ringbuffer.RingBuffer;
import org.csstudio.utility.plotwidget.PlotSamples;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/** A ring buffer for SysInfo items that also provides them to the plot.
 *  @author Kay Kasemir
 */
public class SysInfoBuffer implements PlotSamples
{
    final private RingBuffer<SysInfo> buffer;

    SysInfoBuffer(final int size)
    {
        buffer = new RingBuffer<SysInfo>(size);
    }

    public void add(final SysInfo info)
    {
        buffer.add(info);
    }

    @Override
    public int getTraceCount()
    {
        return 2;
    }

    public Color getTotalColor()
    {
        return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
    }

    public Color getFreeColor()
    {
        return Display.getCurrent().getSystemColor(SWT.COLOR_GREEN);
    }

    @Override
    public Color getColor(final int trace)
    {
        switch (trace)
        {
        case 0:
            return getTotalColor();
        default:
            return getFreeColor();
        }
    }

    @Override
    public int getSampleCount()
    {
        return buffer.size();
    }

    @Override
    public double[] getValues(final int i)
    {
        final SysInfo info = buffer.get(i);
        return new double[] { info.getTotalMB(), info.getFreeMB() };
    }
}
