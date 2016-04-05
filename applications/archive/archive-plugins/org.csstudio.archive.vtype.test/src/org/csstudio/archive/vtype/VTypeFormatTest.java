/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.csstudio.utility.test.HamcrestMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.diirt.util.text.NumberFormats;
import org.diirt.util.time.Timestamp;
import org.junit.Test;

/** JUnit test of {@link VTypeFormat}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class VTypeFormatTest
{
    @Test
    public void testFormats() throws Exception
    {
        final Timestamp now = Timestamp.now();
        final Display display = ValueFactory.newDisplay(0.0, 1.0, 2.0, "a.u.", NumberFormats.format(3), 8.0, 9.0, 10.0, 0.0, 10.0);

        final VTypeFormat devault = new DefaultVTypeFormat();
        final VTypeFormat decimal = new DecimalVTypeFormat(2);
        final VTypeFormat exponen = new ExponentialVTypeFormat(1);
        final VTypeFormat strings = new StringVTypeFormat();

        assertThat(devault.toString(), equalTo("Default"));
        assertThat(decimal.toString(), equalTo("Decimal (2 digits)"));
        assertThat(exponen.toString(), equalTo("Exponential (1 digits)"));
        assertThat(strings.toString(), equalTo("String"));

        VType value = new ArchiveVNumber(now, AlarmSeverity.NONE, "", display, 31.4);
        assertThat(devault.format(value), equalTo("31.400"));
        assertThat(decimal.format(value), equalTo("31.40"));
        assertThat(exponen.format(value), equalTo("3.1E1"));
        assertThat(strings.format(value), equalTo("31.400"));

        final List<String> labels = Arrays.asList("zero", "one", "two", "three");
        value = new ArchiveVEnum(now, AlarmSeverity.NONE, "", labels, 3);
        assertThat(devault.format(value), equalTo("three (3)"));
        assertThat(decimal.format(value), equalTo("3.00"));
        assertThat(exponen.format(value), equalTo("3.0E0"));
        assertThat(strings.format(value), equalTo("three"));

        value = new ArchiveVNumberArray(now, AlarmSeverity.NONE, "", display, 1.0, 2.0, 3.0);
        assertThat(devault.format(value), equalTo("1.000, 2.000, 3.000"));
        assertThat(decimal.format(value), equalTo("1.00, 2.00, 3.00"));
        assertThat(exponen.format(value), equalTo("1.0E0, 2.0E0, 3.0E0"));
        assertThat(strings.format(value), equalTo("\\u0001\\u0002\\u0003"));

        value = new ArchiveVNumberArray(now, AlarmSeverity.NONE, "", display, 72, 101, 108, 108, 111, 32, 33, 0);
        assertThat(decimal.format(value), equalTo("72.00, 101.00, 108.00, 108.00, 111.00, 32.00, 33.00, 0.00"));
        assertThat(strings.format(value), equalTo("Hello !"));

        assertThat(VTypeFormat.MAX_ARRAY_ELEMENTS, equalTo(10));
        final double[] data = new double[20];
        for (int i=0; i<data.length; ++i)
            data[i] = i;
        value = new ArchiveVNumberArray(now, AlarmSeverity.NONE, "", display, data);
        String text = devault.format(value);
        System.out.println(text);
        assertThat(text, containsString("0.000, 1.000, 2.000, 3.000, 4.000"));
        assertThat(text, containsString("total 20 elements"));
        assertThat(text, containsString("15.000, 16.000, 17.000, 18.000, 19.000"));

        // Initial '0' results in empty string
        text = strings.format(value);
        assertThat(text.length(), equalTo(0));

        text = decimal.format(value);
        System.out.println(text);
        assertThat(text, containsString("0.00, 1.00, 2.00, 3.00, 4.00"));
        assertThat(text, containsString("total 20 elements"));
        assertThat(text, containsString("15.00, 16.00, 17.00, 18.00, 19.00"));

        text = exponen.format(value);
        System.out.println(text);
        assertThat(text, containsString("0.0E0, 1.0E0, 2.0E0, 3.0E0, 4.0E0"));
        assertThat(text, containsString("total 20 elements"));
        assertThat(text, containsString("1.5E1, 1.6E1, 1.7E1, 1.8E1, 1.9E1"));
    }
}
