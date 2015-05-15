package org.csstudio.diag.postanalyser.model;

import java.util.Arrays;

import org.csstudio.swt.chart.ChartSample;
import org.csstudio.swt.chart.ChartSampleContainer;
import org.csstudio.swt.chart.ChartSampleSequence;
import org.csstudio.swt.chart.Range;

/** Represents <code>x</code> and <code>y</code> arrays as
 *  <code>ChartSampleSequence</code>.
 *  @author Kay Kasemir
 */
public class XYChartSamples implements ChartSampleSequence
{
    private double[] x;
    private double[] y;

    /** Construct with <code>x</code> and <code>y</code> arrays.
     *  @exception IllegalArgumentException when arrays differ in length
     */
    @SuppressWarnings("nls")
    public XYChartSamples(final double x[], final double y[])
    {
        if (x.length != y.length)
            throw new IllegalArgumentException(
                    "x length " + x.length + " != y length " + y.length);
        this.x = x;
        this.y = y;
    }

    /* @see org.csstudio.swt.chart.ChartSampleSequence#get(int) */
    @Override
    public ChartSample get(int i)
    {
        return new ChartSampleContainer(x[i], y[i]);
    }

    /* @see org.csstudio.swt.chart.ChartSampleSequence#getDefaultRange() */
    @Override
    public Range getDefaultRange()
    {
        return null;
    }

    /* @see org.csstudio.swt.chart.ChartSampleSequence#size() */
    @Override
    public int size()
    {
        return y.length;
    }

    /** @return the x */
    public double[] getX()
    {
        return x;
    }

    /** @return the y */
    public double[] getY()
    {
        return y;
    }

    /** Reduce the samples to start ... end on the 'x' axis */
    public void crop(final double start, final double end)
    {
        int i0 = Math.abs(Arrays.binarySearch(x, start));
        int i1 = Math.abs(Arrays.binarySearch(x, end));
        if (i0 < 0)
            i0 = 0;
        if (i1 > x.length)
            i1 = x.length;
        int N = i1 - i0;
        final double new_x[] = new double[N];
        final double new_y[] = new double[N];
        System.arraycopy(x, i0, new_x, 0, N);
        System.arraycopy(y, i0, new_y, 0, N);
        x = new_x;
        y = new_y;
    }

    /** Adjust all Y values to given base line */
    public void baseline(double baseline)
    {
        final int N = y.length;
        for (int i=0; i<N; ++i)
            y[i] -= baseline;
    }
}
