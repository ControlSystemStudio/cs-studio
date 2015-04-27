package org.csstudio.diag.postanalyser.math;

import java.util.Arrays;

import org.junit.Test;

/** Test of the window filters
 *  @author Kay Kasemir
 */
public class FilterTest
{
    @SuppressWarnings("nls")
    @Test
    public void testFilter()
    {
        final int N = 50;
        final double input[] = new double[N];
        Arrays.fill(input, 1.0);
        
        for (Filter.Type type : Filter.Type.values())
        {
            final double output[] = Filter.window(input, type);
            System.out.println("# " + type.toString());
            for (int i = 0; i < output.length; i++)
                System.out.println(output[i]);
            System.out.println("\n\n");
        }
        System.out.println("# Paste to file, then use gnuplot");
        System.out.println("#  plot 'x' index 1 with lines");
    }
}
