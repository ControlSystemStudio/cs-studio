package org.csstudio.platform.data;

import junit.framework.TestCase;

import org.csstudio.platform.data.IValue.Quality;
import org.junit.Test;

/** Some very basic tests of the Sample implementation.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ValueTest extends TestCase
{
    @Test
	public void testEquality() throws Exception
	{
        ISeverity ok = ValueFactory.createOKSeverity();
        ISeverity bad = ValueFactory.createInvalidSeverity();
		ITimestamp now = TimestampFactory.now();

        INumericMetaData meta =
            ValueFactory.createNumericMetaData(0, 10, 0, 0, 0, 0, 3, "socks");
        
        double values[];
		values = new double[1];
		values[0] = 3.14;
		final Quality quality = IValue.Quality.Original;
        IValue a = ValueFactory.createDoubleValue(now, ok, "OK", meta,
                                                  quality, values);

        values = new double[1];
		values[0] = 3.14;
        IValue b = ValueFactory.createDoubleValue(now, ok, "OK", meta,
                                                  quality, values);
        
        values = new double[1];
		values[0] = 42.0;
        IValue c = ValueFactory.createDoubleValue(now, bad, "Error", meta,
                                                  quality, values);
        
		assertFalse(a == b);
		assertTrue(a.equals(a));
		assertTrue(a.equals(b));
		assertTrue(b.equals(a));
		assertFalse(a.equals(c));
		assertFalse(b.equals(c));
        
        assertEquals("3.140", a.format());
        assertEquals("42.000", c.format());

        assertEquals("3.1400", a.format(IValue.Format.Decimal, 4));
        assertEquals("3", a.format(IValue.Format.Decimal, 0));
        assertEquals("3.14E0", a.format(IValue.Format.Exponential, 2));
        assertEquals("3.140E0", a.format(IValue.Format.Exponential, 3));
        
        IEnumeratedMetaData enum_meta = ValueFactory.createEnumeratedMetaData(
            new String[] { "One", "Two" } );
        IValue en = ValueFactory.createEnumValue(now, ok, "OK", enum_meta,
                                                 quality, new int[] { 1 });
        assertEquals("Two", en.format());
    }
}
