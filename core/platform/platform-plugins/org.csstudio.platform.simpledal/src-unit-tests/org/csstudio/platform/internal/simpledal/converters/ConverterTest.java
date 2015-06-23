package org.csstudio.platform.internal.simpledal.converters;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ConverterTest {


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testDoubleConverter() throws Exception {
        DoubleConverter doubleConverter = new DoubleConverter();
        // Integer
        int i = 0;
        assertEquals(i, doubleConverter.convert(i), 0.0001);
        i = 12345;
        assertEquals(i, doubleConverter.convert(i), 0.0001);
        i = -12345;
        assertEquals(i, doubleConverter.convert(i), 0.0001);
        // Long
        long l = 0;
        assertEquals(l, doubleConverter.convert(l), 0.0001);
        l = 12345;
        assertEquals(l, doubleConverter.convert(l), 0.0001);
        l = -12345;
        assertEquals(l, doubleConverter.convert(l), 0.0001);
        // Double
        double d = 0;
        assertEquals(d, doubleConverter.convert(d), 0.0001);
        d = 12345;
        assertEquals(d, doubleConverter.convert(d), 0.0001);
        d = -12345;
        assertEquals(d, doubleConverter.convert(d), 0.0001);
        d = 123.45;
        assertEquals(d, doubleConverter.convert(d), 0.0001);
        d = -123.45;
        assertEquals(d, doubleConverter.convert(d), 0.0001);
        // Wrong Object
        assertNuberFormatExeption(doubleConverter, new Object());
        // String
        assertNuberFormatExeption(doubleConverter, "");
        assertNuberFormatExeption(doubleConverter, "1qayXSW,.-+#");
        assertNuberFormatExeption(doubleConverter, "qayXSW,.-+#2454");
        // Int as String
        assertEquals(0, doubleConverter.convert("0"), 0.0001);
        assertEquals(1234567890, doubleConverter.convert("1234567890"), 0.0001);
        assertEquals(-1234567890, doubleConverter.convert("-1234567890"), 0.0001);

        // Float as String
        assertEquals(12345.67890, doubleConverter.convert("12345.67890"), 0.0001);
        assertEquals(-12345.67890, doubleConverter.convert("-12345.67890"), 0.0001);
        assertNuberFormatExeption(doubleConverter, "-12345.-67890");

        // Exp as Stirng
        assertEquals(123400, doubleConverter.convert("1234e2"), 0.0001);
        assertEquals(-123.4, doubleConverter.convert("-1234e-1"), 0.0001);
        assertEquals(123400, doubleConverter.convert("+1234e+2"), 0.0001);
        assertEquals(1234, doubleConverter.convert("+12.34e+2"), 0.0001);
        assertNuberFormatExeption(doubleConverter, "+12.34e+12.3");
        assertEquals(-.01234, doubleConverter.convert("-12.34e-3"), 0.0001);
        assertNuberFormatExeption(doubleConverter, "-12.34e-12.3");

        // Hex as String
        assertNuberFormatExeption(doubleConverter, "1af");
        assertNuberFormatExeption(doubleConverter, "AF");
        assertNuberFormatExeption(doubleConverter, "+1AF");
        assertNuberFormatExeption(doubleConverter, "+1af");
        assertNuberFormatExeption(doubleConverter, "+af");
        assertNuberFormatExeption(doubleConverter, "+AF");
        assertNuberFormatExeption(doubleConverter, "-1af");
        assertNuberFormatExeption(doubleConverter, "-AF");
        assertEquals(431, doubleConverter.convert("0x1AF"), 0.0001);
        assertEquals(175, doubleConverter.convert("0xaf"), 0.0001);
        assertEquals(175, doubleConverter.convert("+0xaf"), 0.0001);
        assertEquals(18, doubleConverter.convert("0x12"), 0.0001);
        assertEquals(18, doubleConverter.convert("+0x12"), 0.0001);
        assertEquals(-18, doubleConverter.convert("-0x12"), 0.0001);
        assertEquals(-431, doubleConverter.convert("-0X1AF"), 0.0001);
        assertEquals(-175, doubleConverter.convert("-0xaf"), 0.0001);

    }

    private void assertNuberFormatExeption(IValueTypeConverter converter, Object object) {
        try {
            converter.convert(object);
            fail();
        } catch (NumberFormatException e) {
        }
    }

    @Test
    public void testLongConverter() throws Exception {
        LongConverter longConverter = new LongConverter();
        // Integer
        int i = 0;
        assertEquals(i, longConverter.convert(i), 0.0001);
        i = 12345;
        assertEquals(i, longConverter.convert(i), 0.0001);
        i = -12345;
        assertEquals(i, longConverter.convert(i), 0.0001);
        // Long
        long l = 0;
        assertEquals(l, longConverter.convert(l), 0.0001);
        l = 12345;
        assertEquals(l, longConverter.convert(l), 0.0001);
        l = -12345;
        assertEquals(l, longConverter.convert(l), 0.0001);
        // Double
        double d = 0;
        assertEquals(d, longConverter.convert(d), 0.0001);
        d = 12345;
        assertEquals(d, longConverter.convert(d), 0.0001);
        d = -12345;
        assertEquals(d, longConverter.convert(d), 0.0001);
        d = 123.45;
        assertEquals(d, longConverter.convert(d), 0.0001);
        d = -123.45;
        assertEquals(d, longConverter.convert(d), 0.0001);
        // Wrong Object
        assertNuberFormatExeption(longConverter, new Object());
        // String
        assertNuberFormatExeption(longConverter, "");
        assertNuberFormatExeption(longConverter, "1qayXSW,.-+#");
        assertNuberFormatExeption(longConverter, "qayXSW,.-+#2454");
        // Int as String
        assertEquals(0, longConverter.convert("0"), 0.0001);
        assertEquals(1234567890, longConverter.convert("1234567890"), 0.0001);
        assertEquals(-1234567890, longConverter.convert("-1234567890"), 0.0001);

        // Float as String
        assertNuberFormatExeption(longConverter, "12345.67890");
        assertNuberFormatExeption(longConverter, "-12345.67890");
        assertNuberFormatExeption(longConverter, "-12345.-67890");

        // Exp as Stirng

        /*
        assertEquals(123400, longConverter.convert("1234e2"));
        assertEquals(-123.4, longConverter.convert("-1234e-1"));
        assertEquals(123400, longConverter.convert("+1234e+2"));
        assertEquals(1234, longConverter.convert("+12.34e+2"));
        assertNuberFormatExeption(longConverter, "+12.34e+12.3");
        assertEquals(-.01234, longConverter.convert("-12.34e-3"));
        assertNuberFormatExeption(longConverter, "-12.34e-12.3");
        */
        // Hex as String
        assertNuberFormatExeption(longConverter, "1af");
        assertNuberFormatExeption(longConverter, "AF");
        assertNuberFormatExeption(longConverter, "+1AF");
        assertNuberFormatExeption(longConverter, "+1af");
        assertNuberFormatExeption(longConverter, "+af");
        assertNuberFormatExeption(longConverter, "+AF");
        assertNuberFormatExeption(longConverter, "-1af");
        assertNuberFormatExeption(longConverter, "-AF");
        assertEquals(431, longConverter.convert("0x1AF"), 0.0001);
        assertEquals(175, longConverter.convert("0xaf"), 0.0001);
        assertEquals(175, longConverter.convert("+0xaf"), 0.0001);
        assertEquals(18, longConverter.convert("0x12"), 0.0001);
        assertEquals(18, longConverter.convert("+0x12"), 0.0001);
        assertEquals(-18, longConverter.convert("-0x12"), 0.0001);
        assertEquals(-431, longConverter.convert("-0X1AF"), 0.0001);
        assertEquals(-175, longConverter.convert("-0xaf"), 0.0001);
    }
}
