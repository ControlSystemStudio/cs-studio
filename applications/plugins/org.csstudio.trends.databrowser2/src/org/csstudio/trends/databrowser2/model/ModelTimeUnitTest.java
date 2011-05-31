/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import static org.junit.Assert.assertEquals;

import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.junit.Test;

/** JUnit Test of model's start/end time handling
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ModelTimeUnitTest
{
    @Test
    public void testTimes() throws InterruptedException
    {
        final double hour = 60.0*60;
        final Model model = new Model();

        // Check scroll mode
        model.setTimespan(hour);
        assertEquals(true, model.isScrollEnabled());
        assertEquals(hour, model.getTimespan(), 1.0);

        // 1 hour ago ... now?
        ITimestamp now = TimestampFactory.now();
        ITimestamp start = model.getStartTime();
        ITimestamp end = model.getEndTime();
        assertEquals(now.toDouble(), end.toDouble(), 5.0);
        assertEquals(now.toDouble() - hour, start.toDouble(), 5.0);

        System.out.println("Scroll starts OK, waiting 5 seconds...");
        Thread.sleep(5*1000);

        // Still 1 hour ago ... now, but 'now' has changed?
        final double change = model.getEndTime().toDouble() - now.toDouble();
        assertEquals(5.0, change, 1.0);

        now = TimestampFactory.now();
        start = model.getStartTime();
        end = model.getEndTime();
        assertEquals(now.toDouble(), end.toDouble(), 5.0);
        assertEquals(now.toDouble() - hour, start.toDouble(), 5.0);

        System.out.println("Scroll updated OK, waiting 5 seconds...");
        Thread.sleep(5*1000);

        // Turn scrolling off
        model.enableScrolling(false);
        assertEquals(false, model.isScrollEnabled());

        // Start/end should no longer change
        assertEquals(start.toDouble(), model.getStartTime().toDouble(), 1.0);
        assertEquals(end.toDouble(),   model.getEndTime().toDouble(), 1.0);
        System.out.println("Start/end stayed constant in no-scroll mode");
    }
}
