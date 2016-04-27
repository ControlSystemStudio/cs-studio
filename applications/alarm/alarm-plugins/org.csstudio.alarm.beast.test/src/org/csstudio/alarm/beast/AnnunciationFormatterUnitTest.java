/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** JUnit test of the
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AnnunciationFormatterUnitTest
{
    @Test
    public void testAnnunciationFormat()
    {
        // Plain description turns into <severity> alarm: ...
        String message = AnnunciationFormatter.format("   Tank Temperature  ", "MINOR", "110");
        System.out.println(message);
        assertEquals("MINOR alarm: Tank Temperature", message);

        // '*' selects custom format
        message = AnnunciationFormatter.format("*   Tank Temperature at {1} which is {0}   ", "MINOR", "110");
        System.out.println(message);
        assertEquals("Tank Temperature at 110 which is MINOR", message);

        // 'Priority' flag '!' from the format must be at the start of the overall message
        message = AnnunciationFormatter.format(" !  Tank Temperature", "MINOR", "110");
        System.out.println(message);
        assertEquals("!MINOR alarm: Tank Temperature", message);

        // Allow both '*!' and '!*' for custom format with priority
        message = AnnunciationFormatter.format("*!Tank Temperature at {1} which is {0}", "MINOR", "110");
        System.out.println(message);
        assertEquals("!Tank Temperature at 110 which is MINOR", message);

        message = AnnunciationFormatter.format("   !  *  Tank Temperature at {1} which is {0}", "MINOR", "110");
        System.out.println(message);
        assertEquals("!Tank Temperature at 110 which is MINOR", message);

        // Allow the use of {, } in the alarm description and only parse {0}, {1}, {2},...
        message = AnnunciationFormatter.format("   !  * PV SR{temp:I} at {1} which is {0}", "MINOR", "110");
        //System.out.println(message);
        assertEquals("!PV SR{temp:I} at 110 which is MINOR", message);

        message = AnnunciationFormatter.format("   !  * PV SR{temp{wire}:I} at {1} which is {0}", "MINOR", "110");
        //System.out.println(message);
        assertEquals("!PV SR{temp{wire}:I} at 110 which is MINOR", message);
    }

    @Test
    public void testPriority()
    {
        assertFalse(AnnunciationFormatter.hasPriority("   Tank Temperature high! "));
        assertFalse(AnnunciationFormatter.hasPriority("*   Tank Temperature at {1} which is {0} "));

        assertTrue(AnnunciationFormatter.hasPriority("! Tank Temperature high! "));
        assertTrue(AnnunciationFormatter.hasPriority("*! Tank Temperature at {1} "));
        assertTrue(AnnunciationFormatter.hasPriority(" *  ! Tank Temperature at {1} "));
        assertTrue(AnnunciationFormatter.hasPriority(" !  * Tank Temperature at {1} "));
    }
}
