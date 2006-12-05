package org.csstudio.utility.pv;

import junit.framework.Assert;
import junit.framework.TestCase;

@SuppressWarnings("nls")
public class ValueTest extends TestCase
{
    public void testEnum() throws Exception
    {
        String strings[] = new String[] { "one", "two", "three" };
        EnumeratedMetaData meta = new EnumeratedMetaData(strings);
        Value a = new EnumValue(meta, 0);
        Value b = new EnumValue(meta, 0);
        Value c = new EnumValue(meta, 1);
        Assert.assertEquals("one", a.toString());
        Assert.assertEquals("one", b.toString());
        Assert.assertTrue(a != b);
        Assert.assertTrue(a.match(b, 0.0));
        Assert.assertEquals("two", c.toString());
        Assert.assertEquals(1, c.toDouble(), 0.01);
        Assert.assertEquals(1, c.toInt());
    }

    public void testInt() throws Exception
    {
        NumericMetaData meta = new NumericMetaData(3, "socks");
        Value a = new IntegerValue(meta, 1);
        Value b = new IntegerValue(meta, 1);
        Value c = new IntegerValue(meta, 2);
        
        Assert.assertEquals("1 socks", a.toString());
        Assert.assertEquals("1 socks", b.toString());
        Assert.assertTrue(a != b);
        Assert.assertTrue(a.match(b, 0.0));
        Assert.assertEquals("2 socks", c.toString());
        Assert.assertEquals(2, c.toDouble(), 0.01);
        Assert.assertEquals(2, c.toInt());
    }

    public void testDouble() throws Exception
    {
        NumericMetaData meta = new NumericMetaData(3, "socks");
        Value a = new DoubleValue(meta, 1.0);
        Value b = new DoubleValue(meta, 1.0);
        Value c = new DoubleValue(meta, 2.0);
        
        Assert.assertEquals("1.000 socks", a.toString());
        Assert.assertEquals("1.000 socks", b.toString());
        Assert.assertTrue(a != b);
        Assert.assertTrue(a.match(b, 0.0));
        Assert.assertEquals("2.000 socks", c.toString());
        Assert.assertEquals(2, c.toDouble(), 0.01);
        Assert.assertEquals(2, c.toInt());
    }
}
