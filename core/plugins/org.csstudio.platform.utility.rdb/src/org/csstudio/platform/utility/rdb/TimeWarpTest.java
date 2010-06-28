/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.platform.utility.rdb;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.junit.Ignore;
import org.junit.Test;

/** JUnit test of the TimeWarp class
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TimeWarpTest
{
    /** Time stamp conversion tests */
    @Test
    public void testGetSQLTimestamp()
    {
        // Get some time stamp with nanoseconds != 0
        final ITimestamp now = TimestampFactory.createTimestamp(
                System.currentTimeMillis()/1000,
                123456789);

        // Convert CSS time to SQL Time
        final Timestamp sql = TimeWarp.getSQLTimestamp(now);
        
        final Calendar css_cal = now.toCalendar();
        final Calendar sql_cal = Calendar.getInstance();
        sql_cal.setTime(new Date(sql.getTime()));
        
        System.out.println("CSS: " + css_cal);
        System.out.println("SQL: " + sql_cal);
        assertEquals(css_cal, sql_cal);

        // Convert SQL time to CSS Time
        final ITimestamp css = TimeWarp.getCSSTimestamp(sql);
        
        System.out.println("Orig           : " + now);
        System.out.println("To SQL and back: "  + css);
        assertEquals(now, css);
    }

    /** Long-term test meant to be run in JProfiler to compare CPU usage */
    @Test
    @Ignore
    public void compareRuntime() throws Exception
    {
        // Convert CSS time to SQL Time
        final ITimestamp css = TimestampFactory.now();
        while (true)
        {
            final Timestamp sql1 = TimeWarp.getSQLTimestamp(css);
            final Timestamp sql2 = TimeWarp.oldGetSQLTimestamp(css);
            assertEquals(sql1, sql2);
            Thread.sleep(2);
        }
    }
}
