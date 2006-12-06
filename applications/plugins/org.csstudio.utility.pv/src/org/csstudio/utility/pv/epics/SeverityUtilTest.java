package org.csstudio.utility.pv.epics;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.value.Severity;

public class SeverityUtilTest extends TestCase
{
    @SuppressWarnings("nls")
    public void testSeverityUtil() throws Exception
    {
        Severity ok = SeverityUtil.forCode(0);
        Assert.assertEquals("", ok.toString());
        Assert.assertEquals(true, ok.isOK());
        System.out.println(ok);
        
        // Get cached instance?
        Severity ok2 = SeverityUtil.forCode(0);
        Assert.assertTrue(ok == ok2);
        

        Severity inv = SeverityUtil.forCode(3);
        Assert.assertEquals("INVALID", inv.toString());
        Assert.assertEquals(false, inv.isOK());
        Assert.assertEquals(true, inv.isInvalid());
        System.out.println(inv);
        
        // Get cached instance?
        Severity inv2 = SeverityUtil.forCode(3);
        Assert.assertTrue(inv == inv2);

    }
}
