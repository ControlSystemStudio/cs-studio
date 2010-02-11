package org.csstudio.trends.databrowser.model;

import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;

/** Unit-test helper for creating samples
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TestSampleBuilder
{
    final public static ISeverity ok = ValueFactory.createOKSeverity();
    // final private static long start = System.currentTimeMillis();

    /** @param i Numeric value as well as pseudo-timestamp
     *  @return IValue sample that has value and time based on input parameter
     */
    public static IValue makeValue(final int i)
    {
        // return ValueFactory.createDoubleValue(TimestampFactory.fromMillisecs(start + i*500),
        return ValueFactory.createDoubleValue(TimestampFactory.fromDouble(i),
               ok, "Test", PlotSample.dummy_meta, IValue.Quality.Original, new double[] { i });
    }

    /** @param i Pseudo-timestamp
     *  @return IValue sample that has error text with time based on input parameter
     */
    public static IValue makeError(final int i, final String error)
    {
        final ISeverity no_value = new ISeverity()
        {
            public boolean hasValue()  { return false; }
            public boolean isInvalid() { return true;  }
            public boolean isMajor()   { return false; }
            public boolean isMinor()   { return false; }
            public boolean isOK()      { return false; }
            @Override
            public String toString()   { return "INVALID"; }
        };
        // return ValueFactory.createDoubleValue(TimestampFactory.fromMillisecs(start + i*500),
        return ValueFactory.createDoubleValue(TimestampFactory.fromDouble(i),
                no_value, error, PlotSample.dummy_meta, IValue.Quality.Original,
                new double[] { Double.NaN });
    }

    
    /** @param i Numeric value as well as pseudo-timestamp
     *  @return IValue sample that has value and time based on input parameter
     */
    public static PlotSample makePlotSample(int i)
    {
        return new PlotSample("Test", makeValue(i));
    }

    /** Create array of samples
     *  @param start First value/time stamp
     *  @param end   Last value/time stamp (exclusive)
     */
    public static PlotSample[] makePlotSamples(final int start, final int end)
    {
        int N = end - start;
        final PlotSample result[] = new PlotSample[N];
        for (int i=0; i<N; ++i)
            result[i] = makePlotSample(start + i);
        return result;
    }
}
