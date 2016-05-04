/*******************************************************************************
 * Copyright (c) 2016 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.java.time;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

/** JUnit test of TimestampFormats
 *  @author Kay Kasemir
 */
public class TimestampFormatsTest
{
    @Test
    public void testFormatter()
    {
        final Instant time = Instant.from(TimestampFormats.SECONDS_FORMAT.parse("2015/02/25 08:42:00"));
        final String text = TimestampFormats.SECONDS_FORMAT.format(time);
        System.out.println(time);
        System.out.println(text);
        assertThat(text, equalTo("2015/02/25 08:42:00"));
        assertThat(TimestampFormats.FULL_FORMAT.format(time), equalTo("2015/02/25 08:42:00.000000000"));
    }

    @Test
    public void testCompactFormatter()
    {
        Instant time = Instant.now();
        assertThat(TimestampFormats.formatCompactDateTime(null), equalTo("?"));

        // Timestamp on same day should be shown as HH:MM:SS
        // Note that this test could fail if run just around midnight
        // where 'now' turns into a new day.
        String text = TimestampFormats.formatCompactDateTime(time);
        System.out.println("Time, today: " + text);
        assertThat(text.length(), equalTo(8));

        time = time.minus(1, ChronoUnit.DAYS);
        text = TimestampFormats.formatCompactDateTime(time);
        System.out.println("Some time yesterday: " + text);
        assertThat(text.length(), equalTo(11));

        time = time.minus(365, ChronoUnit.DAYS);
        text = TimestampFormats.formatCompactDateTime(time);
        System.out.println("A day a year ago: " + text);
        assertThat(text.length(), equalTo(10));
    }
}
