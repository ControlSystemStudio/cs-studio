/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.time.Instant;

import org.csstudio.display.pvtable.model.TimestampHelper;
import org.junit.Test;

/** JUnit test of {@link TimestampHelper}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TimestampHelperTest
{
    @Test
    public void testTimestampHelper() throws Exception
    {
        final String example = "2016-10-21 14:28:55.023605801";
        Instant time = TimestampHelper.parse(example);
        assertThat(time, not(nullValue()));

        final String text = TimestampHelper.format(time);
        assertThat(text, equalTo(example));

        time = TimestampHelper.parse("2016-10-21 14:28:55");
        assertThat(time, not(nullValue()));
        System.out.println(TimestampHelper.format(time));
    }
}
