/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values.test;

import static org.junit.Assert.*;

import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.data.values.internal.DoubleValue;
import org.csstudio.data.values.internal.StringValue;
import org.junit.Test;

@SuppressWarnings("nls")
public class MetaDataUnitTest
{
    @Test
    public void compareEnum()
    {
        final String s1[] = new String[] { "one", "two", "three" };
        final String s2[] = new String[] { "one", "two", "three" };
        final String s3[] = new String[] { "Eins", "Zwei", "Drei" };

        final IEnumeratedMetaData m1 = ValueFactory.createEnumeratedMetaData(s1);
        final IEnumeratedMetaData m2 = ValueFactory.createEnumeratedMetaData(s2);
        final IEnumeratedMetaData m3 = ValueFactory.createEnumeratedMetaData(s3);

        assertEquals("two", m2.getState(1));

        assertEquals(m1, m2);
        assertEquals(m2, m1);
        assertNotSame(m1, m2);
        assertTrue(! m1.equals(m3));
    }

    @Test
    public void compareNumeric()
    {
        final INumericMetaData m1 = ValueFactory.createNumericMetaData(-10.0, 10.0, 1.0, 8.0, 0.0, 9.0, 2, "socks");
        final INumericMetaData m2 = ValueFactory.createNumericMetaData(-10.0, 10.0, 1.0, 8.0, 0.0, 9.0, 2, "socks");
        final INumericMetaData m3 = ValueFactory.createNumericMetaData(-10.0, 10.0, 1.0, 8.0, 0.0, 9.0, 20, "socks");

        assertEquals("socks", m2.getUnits());

        assertEquals(m1, m2);
        assertEquals(m2, m1);
        assertNotSame(m1, m2);
        assertTrue(! m1.equals(m3));
    }

    /** Check the metadata comparison inside the value comparison. */
    @Test
    public void compareInValues()
    {
        final ITimestamp stamp = TimestampFactory.now();
        final ISeverity severity = ValueFactory.createMajorSeverity();
        final String status = "Test";
        final INumericMetaData meta_a = ValueFactory.createNumericMetaData(0, 1, 0, 1, 0, 1, 2, "stuff");
        IValue a = new DoubleValue(stamp, severity, status, meta_a,
                IValue.Quality.Original, new double [] { 1.0, 2.0 });

        final INumericMetaData meta_b = ValueFactory.createNumericMetaData(0, 1, 0, 1, 0, 1, 2, "stuff");
        IValue b = new DoubleValue(stamp, severity, status, meta_b,
                IValue.Quality.Original, new double [] { 1.0, 2.0 });
        assertEquals(a, b);

        a = new StringValue(stamp, severity, status,
                IValue.Quality.Original, new String [] { "Fred" });

        assertTrue(!a.equals(b));
        b = new StringValue(stamp, severity, status,
                IValue.Quality.Original, new String [] { "Fred" });
        assertEquals(a, b);
    }
}
