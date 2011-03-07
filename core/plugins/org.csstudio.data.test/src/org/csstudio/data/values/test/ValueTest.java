/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.junit.Test;

/** Some very basic tests of the Sample implementation.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ValueTest
{
    @Test
    public void testEquality() throws Exception
    {
        ISeverity ok = ValueFactory.createOKSeverity();
        ISeverity bad = ValueFactory.createInvalidSeverity();
        ITimestamp now = TimestampFactory.now();

        INumericMetaData meta =
            ValueFactory.createNumericMetaData(0, 10, 0, 0, 0, 0, 3, "socks");
        System.out.println(meta.toString());

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
        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(b, a);
        assertFalse(a.equals(c));
        assertFalse(b.equals(c));

        // Check twice to assert the format cache is functional
        assertEquals("3.140", a.format());
        assertEquals("3.140", a.format());
        assertEquals("42.000", c.format());
        assertEquals("42.000", c.format());

        assertEquals("3.1400", a.format(IValue.Format.Decimal, 4));
        assertEquals("3.1400", a.format(IValue.Format.Decimal, 4));
        assertEquals("3", a.format(IValue.Format.Decimal, 0));
        assertEquals("3", a.format(IValue.Format.Decimal, 0));
        assertEquals("3.14E0", a.format(IValue.Format.Exponential, 2));
        assertEquals("3.14E0", a.format(IValue.Format.Exponential, 2));
        assertEquals("3.140E0", a.format(IValue.Format.Exponential, 3));
        assertEquals("3.140E0", a.format(IValue.Format.Exponential, 3));

        IEnumeratedMetaData enum_meta = ValueFactory.createEnumeratedMetaData(
            new String[] { "One", "Two" } );
        System.out.println(enum_meta.toString());
        IValue en = ValueFactory.createEnumeratedValue(now, ok, "OK", enum_meta,
                                                 quality, new int[] { 1 });
        assertEquals("Two", en.format());
    }
}
