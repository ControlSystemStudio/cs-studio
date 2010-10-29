/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.util.time;

import static org.junit.Assert.*;

import org.junit.Test;

/** Test of the <code>Throttle</code>
 *  @author Kay Kasemir
 */
public class ThrottleUnitTest
{
    @Test
    public void testRollover()
    {
        final long start = Long.MAX_VALUE;
        // Even when we roll over...
        final long now = start + 2;
        // the basic time difference should still work?!
        assertEquals(2, now - start);
    }
    
    @Test
    public void testIsPermitted() throws Exception
    {
        final Throttle throttle = new Throttle(1.0);
        // Permit initial message
        assertTrue(throttle.isPermitted());
        // But not another one...
        assertFalse(throttle.isPermitted());
        Thread.sleep(1100);
        // .. until about one second passes
        assertTrue(throttle.isPermitted());
    }
}
