package org.csstudio.swt.chart;

import java.util.ArrayList;

/** Simple implementation of the sample sequence interface.
 *  @see ChartSampleSequence
 *  @author Kay Kasemir
 */
public class ChartSampleSequenceContainer implements ChartSampleSequence
{
    private ArrayList<ChartSampleContainer> samples
        = new ArrayList<ChartSampleContainer>();
    
    /** Add a new sample with x/y coords. */
    public void add(double x, double y)
    {
        add(ChartSample.TYPE_NORMAL, x, y);
    }

    /** Add a new sample with type and x/y coords. */
    public void add(int type, double x, double y)
    {
        add(type, x, y, null);
    }

    /** Add a new sample with type and x/y coords. */
    public void add(int type, double x, double y, String info)
    {
        samples.add(new ChartSampleContainer(type, x, y, info));
    }
    
    // @see Series
    public int size()
    {
        return samples.size();
    }

    // @see Series
    public ChartSample get(int i)
    {
        return samples.get(i);
    }
}
