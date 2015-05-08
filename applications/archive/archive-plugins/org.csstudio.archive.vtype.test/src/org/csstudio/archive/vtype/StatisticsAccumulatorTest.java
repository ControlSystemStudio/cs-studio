/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

/** JUnit test of the {@link StatisticsAccumulator}
 *  @author Kay Kasemir
 */
public class StatisticsAccumulatorTest
{
    @Test
    public void testStatistics()
    {
        final StatisticsAccumulator stats = new StatisticsAccumulator();
        // Example numbers from http://en.wikipedia.org/wiki/Standard_deviation
        final double[] data = new double[] { 2, 4, 4, 4, 5, 5, 7, 9 };
        for (double value : data)
            stats.add(value);

        assertThat(stats.getNSamples(), equalTo(data.length));
        assertThat(stats.getMin(), equalTo(2.0));
        assertThat(stats.getMax(), equalTo(9.0));
        assertThat(stats.getAverage(), equalTo(5.0));
        assertThat(stats.getStdDev(), equalTo(2.0));
    }
}
