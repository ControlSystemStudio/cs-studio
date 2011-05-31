/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.epics;

import junit.framework.TestCase;

import org.csstudio.data.values.ISeverity;
import org.junit.Test;

/** Test for the SeverityUtil class.
 *  @author Kay Kasemir
 */
public class SeverityUtilTest extends TestCase
{
    @Test
    @SuppressWarnings("nls")
    public void testSeverityUtil() throws Exception
    {
        final ISeverity ok = SeverityUtil.forCode(0);
        assertEquals("OK", ok.toString());
        assertEquals(true, ok.isOK());
        //System.out.println(ok);

        // Get cached instance?
        final ISeverity ok2 = SeverityUtil.forCode(0);
        assertTrue(ok == ok2);

        final ISeverity inv = SeverityUtil.forCode(3);
        assertEquals("INVALID", inv.toString());
        assertEquals(false, inv.isOK());
        assertEquals(true, inv.isInvalid());
        //System.out.println(inv);

        // Get cached instance?
        final ISeverity inv2 = SeverityUtil.forCode(3);
        assertTrue(inv == inv2);
    }
}
