package org.csstudio.diag.postanalyser.math;

import static org.junit.Assert.*;

import org.junit.Test;

/** Unit test of correlator.
 *  @author Kay Kasemir
 */
public class CorrelatorTest
{
    @SuppressWarnings("nls")
    @Test
    public void testCorrelator()
    {
        final double x1[] = new double [] { 0.0,      1.0,      2.0,      3.0 };
        final double y1[] = new double [] { 1.0,      2.0,      3.0,      4.0 };
        final double x2[] = new double [] {      0.5,      1.5,      2.5,     3.5 };
        final double y2[] = new double [] {      1.0,      2.0,      3.0,     4.0 };
        
        final Correlator corr = new Correlator(x1, y1, x2, y2);
        final int N = corr.getCorrY1().length;
        for (int i=0; i<N; ++i)
            System.out.format("%5.1f\t%5.1f\n",
                    corr.getCorrY1()[i], corr.getCorrY2()[i]);
        assertEquals(6, N);
        assertEquals(3.0, corr.getCorrY1()[3], 0.01);
        assertEquals(2.0, corr.getCorrY2()[3], 0.01);
    }
}
