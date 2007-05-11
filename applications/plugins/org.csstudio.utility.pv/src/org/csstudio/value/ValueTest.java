package org.csstudio.value;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.value.Value.Format;

/** Some very basic tests of the Sample implementation.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ValueTest extends TestCase
{
	public void testEquality() throws Exception
	{
		ITimestamp now = TimestampFactory.now();
		Severity ok = new Severity()
        {
            public String toString()
            {   return "OK";  }

            public boolean hasValue()
            {   return true; }

            public boolean isInvalid()
            {   return false;  }

            public boolean isMajor()
            {   return false;  }

            public boolean isMinor()
            {   return false;  }

            public boolean isOK()
            {   return true; }
        };
        Severity bad = new Severity()
        {
            public String toString()
            {   return "Bad";  }

            public boolean hasValue()
            {   return true; }

            public boolean isInvalid()
            {   return false;  }

            public boolean isMajor()
            {   return true;  }

            public boolean isMinor()
            {   return false;  }

            public boolean isOK()
            {   return false; }
        };

        MetaData meta = new NumericMetaData(10, 0, 9, 1, 8, 2, 3, "socks");
        
        double values[];
		values = new double[1];
		values[0] = 3.14;
		Value a = new DoubleValue(now, ok, "OK", meta, values);
		values = new double[1];
		values[0] = 3.14;
		Value b = new DoubleValue(now, ok, "OK", meta, values);
		values = new double[1];
		values[0] = 42.0;
		Value c = new DoubleValue(now, bad, "Error", meta, values);
		
		Assert.assertFalse(a == b);
		Assert.assertTrue(a.equals(a));
		Assert.assertTrue(a.equals(b));
		Assert.assertTrue(b.equals(a));
		Assert.assertFalse(a.equals(c));
		Assert.assertFalse(b.equals(c));
        
        Assert.assertEquals("3.140", a.format());
        Assert.assertEquals("42.000", c.format());

        Assert.assertEquals("3.1400", a.format(Format.Decimal, 4));
        Assert.assertEquals("3", a.format(Format.Decimal, 0));
        Assert.assertEquals("3.14E0", a.format(Format.Exponential, 2));
        Assert.assertEquals("3.140E0", a.format(Format.Exponential, 3));
    }
}
