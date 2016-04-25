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
}
