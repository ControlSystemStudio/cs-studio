package org.csstudio.swt.chart.axes;

import junit.framework.TestCase;

public class LogTicksTest extends TestCase
{

    @SuppressWarnings("nls")
    public void testCompute()
    {
        Ticks ticks = new LogTicks();
        double low = 1, high = 100;
        System.out.println(low + " ... " + high);
        ticks.compute(low, high, null, 500);
        System.out.println(ticks);
        double t = ticks.getStart();
        while (t <= high)
        {
            System.out.println(ticks.format(t));
            t = ticks.getNext(t);
        }

        low = 0;
        high = 100;
        System.out.println(low + " ... " + high);
        ticks.compute(low, high, null, 500);
        System.out.println(ticks);
        t = ticks.getStart();
        while (t <= high)
        {
            System.out.println(ticks.format(t));
            t = ticks.getNext(t);
        }

        low = 1e-10;
        high = 1e-7;
        System.out.println(low + " ... " + high);
        ticks.compute(low, high, null, 500);
        System.out.println(ticks);
        t = ticks.getStart();
        while (t <= high)
        {
            System.out.println(ticks.format(t));
            t = ticks.getNext(t);
        }
    }

}
