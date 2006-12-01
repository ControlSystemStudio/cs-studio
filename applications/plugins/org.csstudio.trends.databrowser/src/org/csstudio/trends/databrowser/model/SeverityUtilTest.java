package org.csstudio.trends.databrowser.model;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.archive.Severity;

public class SeverityUtilTest extends TestCase
{
    @SuppressWarnings("nls")
    public void testSeverityUtil() throws Exception
    {
        Severity ok = SeverityUtil.get(0, "OK");
        Assert.assertEquals("OK", ok.toString());
        Assert.assertEquals(true, ok.isOK());
        System.out.println(ok);
        
        // Get cached instance?
        Severity ok2 = SeverityUtil.get(0, "OK");
        Assert.assertTrue(ok == ok2);
        

        Severity inv = SeverityUtil.get(3, "Invalid");
        Assert.assertEquals("Invalid", inv.toString());
        Assert.assertEquals(false, inv.isOK());
        Assert.assertEquals(true, inv.isInvalid());
        System.out.println(inv);
        
        // Get cached instance?
        Severity inv2 = SeverityUtil.get(3, "Invalid");
        Assert.assertTrue(inv == inv2);

    }
}
