/*******************************************************************************
 * Copyright (c) 2010-2015 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.util;

import static org.junit.Assert.assertEquals;

import java.time.Instant;

import org.junit.Test;

/**
 * Unit test for {@link NotifierUtils}
 * *
 */
public class NotifierUtilsUnitTest {

    /**
     * Ensure that the NotifierUtil correctly parse time durations
     */
    @Test
    public void testStringFormatting() {
        Instant now = Instant.now();
        // Parse positive times in the format hh:mm:ss
        assertEquals("notifierUtil failed correctly parse posative time duration ", "0:00:05",
                NotifierUtils.getDurationString(now.minusSeconds(5)));

        // Ignore negative time durations
        assertEquals("notifierUtil failed correctly parse negative time duration ", "",
                NotifierUtils.getDurationString(now.plusSeconds(5)));

    }
}
