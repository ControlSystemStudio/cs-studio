/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.csstudio.logbook.sns.elog.ELog;
import org.csstudio.logbook.sns.elog.ELogEntry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** JUnit test for {@link ELog}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ELogUnitTest
{
    final private static String LOGBOOK = "Scratch Pad";
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
        assertTrue(logbooks.size() > 0);
    }
    
    @Test
    public void testTextEntry() throws Exception
    {
        entry_id = elog.createEntry(LOGBOOK, "Text test", "this is a test");
        
        final ELogEntry entry = elog.getEntry(entry_id);
        System.out.println(entry);
        assertThat(entry.getTitle(), equalTo("Text test"));
    }

    @Test
    public void testTextEntryForMultipleLogbooks() throws Exception
    {
        entry_id = elog.createEntry(LOGBOOK, "Text test", "this is a test");
        elog.addLogbook(entry_id, "Automated Entries");
        
        final ELogEntry entry = elog.getEntry(entry_id);
        System.out.println(entry);
        assertThat(entry.getTitle(), equalTo("Text test"));
        assertThat(entry.getLogbooks().size(), equalTo(2));
    }
    
    @Test
    public void testLongEntry() throws Exception
    {
        // Happen to know that max text length is 4000...
        final StringBuilder buf = new StringBuilder();
        buf.append("Long Text Test\n");
        for (int i=0; i<=4000/28; ++i)
            buf.append("This is a long test text... ");
        assertTrue(buf.length() > 4000);
        
        entry_id = elog.createEntry(LOGBOOK, "Long text test", buf.toString());

        final ELogEntry entry = elog.getEntry(entry_id);
        System.out.println(entry);
        assertThat(entry.getTitle(), equalTo("Long text test"));
        assertThat(entry.getAttachments().size(), equalTo(1));
        final String attached_text = new String(entry.getAttachments().get(0).getData());
        assertThat(attached_text, equalTo(buf.toString()));
    }

    @Test //(timeout=10000)
    public void testReadEntry() throws Exception
    {
        ELogEntry entry = elog.getEntry(entry_id);
        System.out.println(entry);
   }
}
