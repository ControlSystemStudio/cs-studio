package org.csstudio.diag.postanalyser.math;

import org.junit.Test;
import static org.junit.Assert.*;

/** Unit test for SortedArrayList
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SortedArrayListTest
{
    @Test
    public void testInserts()
    {
        SortedArrayList<Double> arr = new SortedArrayList<Double>();

        add(arr, 5.0);  // Initial
        add(arr, 10.0); // end
        add(arr, 4.0);  // front
        add(arr, 6.0);  // after middle
        add(arr, 4.5);  // before middle
        add(arr, 4.5);  // same
        add(arr, 1.0);  // front
        add(arr, 12.0); // end

    }

    private void add(final SortedArrayList<Double> arr, final double new_val)
    {
        arr.insert(new_val);
        for (int i=0; i<arr.size(); ++i)
        {
            final Double val = arr.get(i);
            System.out.println(val);
            if (i > 0)
                assertTrue("sorted", val >= arr.get(i-1).doubleValue());
        }
        System.out.println();
    }
}
