package org.csstudio.archive.util;

import junit.framework.Assert;
import junit.framework.TestCase;

/** Test for some of the TimestampUtil methods.
 *  @author Kay Kasemir
 */
public class TimestampUtilTest extends TestCase
{
    @SuppressWarnings("nls")
    public void testParseSeconds() throws Exception
    {
        Assert.assertEquals(42.0, TimestampUtil.parseSeconds("42"), 0.01);
        Assert.assertEquals(10.0, TimestampUtil.parseSeconds(" 10  "), 0.01);
        Assert.assertEquals(3600.2, TimestampUtil.parseSeconds("  3600.2 "), 0.01);

        Assert.assertEquals(60.0, TimestampUtil.parseSeconds("1:0"), 0.01);
        Assert.assertEquals(120.0, TimestampUtil.parseSeconds(" 02:000"), 0.01);
        Assert.assertEquals(90.0, TimestampUtil.parseSeconds("   1:30"), 0.01);

        Assert.assertEquals(60.0*60.0, TimestampUtil.parseSeconds(" 1: 0:0"), 0.01);
        Assert.assertEquals(60.0*60.0 + 30*60 + 20, TimestampUtil.parseSeconds("1:30:20"), 0.01);
        Assert.assertEquals(24*60.0*60.0, TimestampUtil.parseSeconds("24:0:  0"), 0.01);
    }

    @SuppressWarnings("nls")
    public void testParseOddballs() throws Exception
    {
        Assert.assertEquals(-42.0, TimestampUtil.parseSeconds("-42"), 0.01);

        Assert.assertEquals(-90.0, TimestampUtil.parseSeconds("-1:30"), 0.01);

        Assert.assertEquals(-24*60.0*60.0, TimestampUtil.parseSeconds("-24:0:0"), 0.01);

        boolean caught = false;
        try
        {
            TimestampUtil.parseSeconds("    ");
        }
        catch (Exception e)
        {
            caught = true;
        }
        Assert.assertTrue(caught);

        caught = false;
        try
        {
            TimestampUtil.parseSeconds("  1:2:3:4  ");
        }
        catch (Exception e)
        {
            caught = true;
        }
        Assert.assertTrue(caught);

    }

}
