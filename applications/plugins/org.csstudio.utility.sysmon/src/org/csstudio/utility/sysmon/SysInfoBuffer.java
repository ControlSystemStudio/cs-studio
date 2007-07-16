package org.csstudio.utility.sysmon;

import org.csstudio.utility.plotwidget.PlotSamples;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/** A ring buffer for SysInfo items that also provides them to the plot.
 *  @author Kay Kasemir
 */
public class SysInfoBuffer implements PlotSamples
{
    private RingBuffer<SysInfo> buffer = new RingBuffer<SysInfo>(60);

    public void add(SysInfo info)
    {
        buffer.add(info);
    }
    
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

    public Color getColor(int trace)
    {
        switch (trace)
        {
        case 0:
            return getTotalColor();
        default:
            return getFreeColor();
        }
    }

    public int getSampleCount()
    {
        return buffer.size();
    }

    public double[] getValues(int i)
    {
        final SysInfo info = buffer.get(i);
        return new double[] { info.getTotalMB(), info.getFreeMB() };
    }
}
