/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.util.stats;

import org.junit.Test;
import static org.junit.Assert.*;

/** JUnit test of {@link Average}
 *  @author Kay Kasemir
 */
public class AverageUnitTest
{
    private static final double EPS = 0.001;

    @Test
    public void testAverage()
    {
        Average average = new Average();
        assertEquals(0.0, average.get(), EPS);
        
        average.update(100.0);
        assertEquals(100.0, average.get(), EPS);

        average.update(50.0);
        assertEquals(100.0 * 0.9 + 50.0 * 0.1, average.get(), EPS);

        average.update(50.0);
        assertEquals((100.0 * 0.9 + 50.0 * 0.1)*0.9 + 50.0 * 0.1,
                     average.get(), EPS);
    }
}
