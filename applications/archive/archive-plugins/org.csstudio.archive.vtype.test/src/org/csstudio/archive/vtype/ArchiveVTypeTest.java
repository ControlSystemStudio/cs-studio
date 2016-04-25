/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.diirt.util.text.NumberFormats;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.junit.Test;

/** JUnit test of Archive {@link VType}s
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ArchiveVTypeTest
{
    @Test
    public void testEqual() throws Exception
    {
        final Instant now = Instant.now();
        final Display display = ValueFactory.newDisplay(0.0, 1.0, 2.0, "a.u.", NumberFormats.format(3), 8.0, 9.0, 10.0, 0.0, 10.0);
        final VType dbl314 = new ArchiveVNumber(now, AlarmSeverity.MINOR, "Troubling", display, 3.14);
        final VType dbl314b = new ArchiveVNumber(now, AlarmSeverity.MINOR, "Troubling", display, 3.14);
        assertThat(dbl314, equalTo(dbl314b));

        final VType int3 = new ArchiveVNumber(now, AlarmSeverity.MINOR, "Troubling", display, Integer.valueOf(3));
        assertThat(int3, not(equalTo(dbl314)));

        final VType dbl3 = new ArchiveVNumber(now, AlarmSeverity.MINOR, "Troubling", display, Double.valueOf(3));
        assertThat(int3, equalTo(dbl3));

        final List<String> labels = Arrays.asList("zero", "one", "two", "three");
        final VType enum3 = new ArchiveVEnum(now, AlarmSeverity.MINOR, "Troubling", labels, 3);
        assertThat(enum3, not(equalTo(dbl314)));
        assertThat(enum3, equalTo(int3));
        assertThat(enum3, equalTo(dbl3));
        assertThat(dbl3, equalTo(enum3));

        final VType str3 = new ArchiveVString(now, AlarmSeverity.MINOR, "Troubling", "three");
        assertThat(str3, not(equalTo(int3)));
        assertThat(enum3, equalTo(str3));
        assertThat(str3, equalTo(enum3));

        final VType arr1 = new ArchiveVNumberArray(now, AlarmSeverity.MINOR, "Troubling", display, 1.0, 2.0, 3.0);
        final VType arr1b = new ArchiveVNumberArray(now, AlarmSeverity.MINOR, "Troubling", display, 1.0, 2.0, 3.0);
        final VType arr2 = new ArchiveVNumberArray(now, AlarmSeverity.MINOR, "Troubling", display, 1.0, 2.0, 3.1);
        System.out.println(arr1);
        assertThat(arr1, not(equalTo(int3)));
        assertThat(arr1, not(equalTo(arr2)));
        assertThat(arr1, equalTo(arr1b));

        final StatisticsAccumulator stats = new StatisticsAccumulator(1.0, 2.0, 3.0, 4.0);
        final VType stat1 = new ArchiveVStatistics(now, AlarmSeverity.MINOR, "Troubling", display, stats);
        final VType stat1b = new ArchiveVStatistics(now, AlarmSeverity.MINOR, "Troubling", display, stats);
        stats.add(2.5);
        final VType stat2 = new ArchiveVStatistics(now, AlarmSeverity.MINOR, "Troubling", display, stats);
        System.out.println(stat1);
        assertThat(stat1, not(equalTo(int3)));
        assertThat(stat1, equalTo(stat1b));
        assertThat(stat1, not(equalTo(stat2)));
    }
}
