package org.csstudio.swt.chart;

/** A value range, low...high
 *  @author Kay Kasemir
 */
public class Range
{
    /** The range */
    private double low, high;

    /** Construct range with given low and high ends. */
    public Range(double low, double high)
    {
        this.low = low;
        this.high = high;
    }

    /** @return low end of range */
    public double getLow()
    {
        return low;
    }

    /** @param low New low end. */
    public void setLow(double low)
    {
        this.low = low;
    }

    /** @return high end of range */
    public double getHigh()
    {
        return high;
    }

    /** @param high New high end. */
    public void setHigh(double high)
    {
        this.high = high;
    }

    /** Compare range to other range
     *  @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        Range other = (Range) obj;
        return other.low == low  &&  other.high == high;
    }

    /** Convert range to printable string. */
    @Override
    public String toString()
    {
        return low + " ... " + high; //$NON-NLS-1$
    }
}
