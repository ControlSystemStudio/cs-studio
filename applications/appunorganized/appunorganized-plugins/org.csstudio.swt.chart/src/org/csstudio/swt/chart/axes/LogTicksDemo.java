/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.axes;

import org.junit.Test;

/** {@link LogTicks} Demo
 *  @author Kay Kasemir
 */
public class LogTicksDemo
{
    @SuppressWarnings("nls")
    @Test
    public void testCompute()
    {
        final Ticks ticks = new LogTicks();
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
