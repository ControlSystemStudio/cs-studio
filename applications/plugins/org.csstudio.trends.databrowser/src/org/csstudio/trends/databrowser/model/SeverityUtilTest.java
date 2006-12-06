package org.csstudio.trends.databrowser.model;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.value.Severity;

public class SeverityUtilTest extends TestCase
{
    public void testSeverityUtil() throws Exception
    {
        Severity inv = SeverityFactory.getInvalid();
        Assert.assertEquals(Messages.Sevr_INVALID, inv.toString());
        Assert.assertEquals(false, inv.isOK());
        Assert.assertEquals(true, inv.isInvalid());
        System.out.println(inv);
        
        // Get same instance?
        Severity inv2 = SeverityFactory.getInvalid();
        Assert.assertTrue(inv == inv2);
    }
}
