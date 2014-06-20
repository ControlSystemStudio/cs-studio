/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import static org.csstudio.utility.test.HamcrestMatchers.closeTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.epics.util.time.Timestamp;
import org.junit.Test;

/** JUnit Test of model's start/end time handling
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ModelTimeUnitTest
{
    @Test
    public void testTimes() throws Exception
    {
        final double hour = 60.0*60;
        final Model model = new Model();

        // Check scroll mode
        model.setTimerange("-1 hour", "now");
        assertEquals(true, model.isScrollEnabled());
        assertEquals(hour, model.getTimespan(), 1.0);

        // 1 hour ago ... now?
        Timestamp now = Timestamp.now();
        Timestamp start = model.getStartTime();
        Timestamp end = model.getEndTime();
        assertThat(end.durationFrom(now).toSeconds(), closeTo(0, 5));
        assertThat(now.durationFrom(start).toSeconds(), closeTo(hour, 5.0));

        System.out.println("Scroll starts OK, waiting 5 seconds...");
        Thread.sleep(5*1000);

        // Still 1 hour ago ... now, but 'now' has changed?
        final double change =
                model.getEndTime().durationFrom(now).toSeconds();
        assertThat(change, closeTo(5.0, 1.0));

        now = Timestamp.now();
        start = model.getStartTime();
        end = model.getEndTime();
        assertThat(end.durationFrom(now).toSeconds(), closeTo(0, 5));
        assertThat(now.durationFrom(start).toSeconds(), closeTo(hour, 5.0));

        System.out.println("Scroll updated OK. Disabling scoll, waiting 5 seconds...");
        // Turn scrolling off
        model.enableScrolling(false);
        assertThat(model.isScrollEnabled(), equalTo(false));

        Thread.sleep(5*1000);

        // Start/end should no longer change
        assertThat(start.durationBetween(model.getStartTime()).toSeconds(), closeTo(0.0, 1.0));
        assertThat(end.durationBetween(model.getEndTime()).toSeconds(), closeTo(0.0, 1.0));
        System.out.println("Start/end stayed constant in no-scroll mode");
    }
}
