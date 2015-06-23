package org.csstudio.diag.postanalyser.math;

/** Correlate two series (x1[], y1[]) and (x2[], y2[]).
 *  <p>
 *  If the series share the same x_[] values, that's easy,
 *  and the result is the (y1[], y2[]) for x1[i] == x2[i].
 *  <p>
 *  If the x_[] values differ, we perform staircase interpolation,
 *  using the same y_[i] for x=x_[i]...x_[i+1].
 *
 *  @author Kay Kasemir
 */
public class Correlator
{
    private double[] corr_y1;
    private double[] corr_y2;

    /** Helper for holding a (y1, y2) tuple.
     *  Sorts by y1.
     */
    private class CorrY1Y2 implements Comparable<CorrY1Y2>
    {
        final double y1, y2;
        public CorrY1Y2(double y1, double y2)
        {
            this.y1 = y1;
            this.y2 = y2;
        }

        @Override
        public int compareTo(final CorrY1Y2 o)
        {
            return Double.compare(y1, o.y1);
        }
    }

    /** Correlate inputs
     *  @param x1 X values of first input
     *  @param y1 Y values of first input
     *  @param x2 X values of second input
     *  @param y2 Y values of second input
     */
    @SuppressWarnings("nls")
    public Correlator(final double x1[], final double y1[],
            final double x2[], final double y2[])
    {
        // Input check
        if (x1.length != y1.length)
            throw new IllegalArgumentException(
                    "x1 length " + x1.length + " != y1 length " + y1.length);
        if (x2.length != y2.length)
            throw new IllegalArgumentException(
                    "x2 length " + x2.length + " != y2 length " + y2.length);
        if (x1.length < 1)
            throw new IllegalArgumentException("First data series is empty");
        if (x2.length < 1)
            throw new IllegalArgumentException("Second data series is empty");

        int i1 = 0, i2 = 0;                // Index into x1/y1 resp. x2/y2 arrays
        double val1 = Double.NaN;          // Value of x1/y1 series for the common x
        double val2 = Double.NaN;          // Value of x2/y2 series for the common x
        // Correlated data must be sorted by y1
        // because that will become the 'x' axis.
        final SortedArrayList<CorrY1Y2> corr = new SortedArrayList<CorrY1Y2>();
        while (i1 < x1.length  &&  i2 < x2.length)
        {
            // Find the next common 'x'
            final double x = Math.min(x1[i1], x2[i2]);
            // See if first series has point that's "valid" at x
            if (x1[i1] <= x)
                val1 = y1[i1++];
            // Similar...
            if (x2[i2] <= x)
                val2 = y2[i2++];
            // Add 'correlated' sample when both series have a value.
            if (! (Double.isNaN(val1) ||  Double.isNaN(val2)))
                corr.insert(new CorrY1Y2(val1, val2));
        }
        // Convert to plain arrays
        corr_y1 = new double[corr.size()];
        corr_y2 = new double[corr.size()];
        for (int i=0; i<corr_y1.length; ++i)
        {
            corr_y1[i] = corr.get(i).y1;
            corr_y2[i] = corr.get(i).y2;
        }
    }

    /** @return Correlated y1 samples of x1/y1 for matching x */
    public double[] getCorrY1()
    {
        return corr_y1;
    }

    /** @return Correlated y2 samples of x1/y1 for matching x */
    public double[] getCorrY2()
    {
        return corr_y2;
    }
}
