/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import static org.csstudio.utility.test.HamcrestMatchers.greaterThan;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.csstudio.logbook.sns.elog.ELog;
import org.csstudio.logbook.sns.elog.ELogEntry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** JUnit test for reading from the {@link ELog}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ELogReadUnitTest
{
    final DateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
    private ELog elog;
    private long entry_id = 395389;

    @Before
    public void connect() throws Exception
    {
        elog = new ELog(Preferences.getURL(), Preferences.getLogListUser(), Preferences.getLogListPassword());
    }

    @After
    public void disconnect()
    {
        elog.close();
    }

    @Test
    public void testLogbooks() throws Exception
    {
        final List<String> logbooks = elog.getLogbooks();
        System.out.println(logbooks);
        assertThat(logbooks.size(), greaterThan(0));
    }
    
    @Test //(timeout=10000)
    public void testReadEntry() throws Exception
    {
        ELogEntry entry = elog.getEntry(entry_id);
        System.out.println(entry);
        assertThat(entry, not(nullValue()));
   }

    @Test //(timeout=10000)
    public void testReadEntries() throws Exception
    {
        final Date now = new Date();
        final Date start = new Date(now.getTime() - 1000L*60*60*24);
        final List<ELogEntry> entries = elog.getEntries(start, now);
        for (ELogEntry entry : entries)
            System.out.format("%s (%s) - %-20s - %s: %s\n",
                    format.format(entry.getDate()),
                    entry.getPriority().getName(),
                    entry.getUser(),
                    entry.getTitle(),
                    shorten(entry.getText().replaceAll("[\r\n]", " ")));
        assertThat(entries.size(), greaterThan(0));
   }

    private static String shorten(final String text)
    {
        if (text.length() < 80)
            return text;
        return text.substring(0, 80) + " ...";
    }
}
