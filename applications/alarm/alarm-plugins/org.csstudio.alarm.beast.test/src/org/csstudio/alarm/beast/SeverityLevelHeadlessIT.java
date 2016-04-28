/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit Plug-in test [headless] of alarm severities
 *  (must be plug-in to obtain colors for severities)
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SeverityLevelHeadlessIT
{
    @Test
    public void testPreferences()
    {
        final SeverityLevel sevr = SeverityLevel.MAJOR;
        // Could change with preference settings,
        // but assume MAJOR is somewhat red in color, and called major
        assertTrue(sevr.getRed() > 200);
        assertTrue(sevr.getDisplayName().equalsIgnoreCase("Major"));
    }

    @Test
    public void testOrdering()
    {
        final SeverityLevel minor = SeverityLevel.MINOR;
        final SeverityLevel major = SeverityLevel.MAJOR;
        final SeverityLevel major_ack = SeverityLevel.MAJOR_ACK;
        // major more severe than minor:
        assertTrue(major.ordinal() > minor.ordinal());
        // major more severe than ack'ed major:
        assertTrue(major.ordinal() > major_ack.ordinal());
        // .. but when updating severities, an ack'ed major
        // means further major and minor alarms of the same PV don't matter
        assertTrue(major_ack.getAlarmUpdatePriority() > major.getAlarmUpdatePriority());
        assertTrue(major_ack.getAlarmUpdatePriority() > minor.getAlarmUpdatePriority());
        // Only an invalid alarm would be higher
        assertTrue(SeverityLevel.INVALID.getAlarmUpdatePriority() > major.getAlarmUpdatePriority());
    }
}
