package org.csstudio.utility.pv.epics;

import junit.framework.TestCase;

import org.csstudio.platform.data.ISeverity;
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
        ISeverity ok = SeverityUtil.forCode(0);
        assertEquals("", ok.toString());
        assertEquals(true, ok.isOK());
        System.out.println(ok);
        
        // Get cached instance?
        ISeverity ok2 = SeverityUtil.forCode(0);
        assertTrue(ok == ok2);
        
        ISeverity inv = SeverityUtil.forCode(3);
        assertEquals("INVALID", inv.toString());
        assertEquals(false, inv.isOK());
        assertEquals(true, inv.isInvalid());
        System.out.println(inv);
        
        // Get cached instance?
        ISeverity inv2 = SeverityUtil.forCode(3);
        assertTrue(inv == inv2);
    }
}
