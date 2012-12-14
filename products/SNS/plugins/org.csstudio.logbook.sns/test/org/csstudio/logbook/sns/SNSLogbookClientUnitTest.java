/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import java.util.Collection;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.LogbookBuilder;
import org.csstudio.logbook.LogbookClient;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/** JUnit test for {@link SNSLogbookClient}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSLogbookClientUnitTest
{
    @Test
    public void testListLogbooks() throws Exception
    {
        final LogbookClient client = new SNSLogbookClientFactory().getClient();
        final Collection<Logbook> logbooks = client.listLogbooks();
        assertThat(logbooks, is(not(nullValue())));
        assertThat(logbooks.size(), is(not(0)));
        for (Logbook logbook : logbooks)
            System.out.println(logbook);
    }

    
    @Test
    public void testDetermineTitle() throws Exception
    {
        String[] title_body = SNSLogbookClient.getTitleAndBody(" Test \r\n\n\r\nThis is\n\n\na test");
        assertThat(title_body.length, equalTo(2));
        assertThat(title_body[0], equalTo("Test"));
        assertThat(title_body[1], equalTo("This is\n\na test"));

    
        title_body = SNSLogbookClient.getTitleAndBody("A one liner...");
        assertThat(title_body.length, equalTo(2));
        assertThat(title_body[0], equalTo("A one liner..."));
        assertThat(title_body[1], equalTo("A one liner..."));

    }


    @Test
    public void testEntryErrors() throws Exception
    {
        final LogbookClient client = new SNSLogbookClientFactory().getClient();
        
        try
        {
            client.createLogEntry(
                    LogEntryBuilder.withText("").build());
            fail("Empty entry?");
        }
        catch (Exception ex)
        {
            assertThat(ex.getMessage(), equalTo("Empty title"));
        }
    }

    
    @Test
    public void testCreateEntry() throws Exception
    {
        final LogbookClient client = new SNSLogbookClientFactory().getClient();
        
        LogEntry entry = LogEntryBuilder.withText("Test\nThis is a test")
            .addLogbook(LogbookBuilder.logbook(Preferences.getDefaultLogbook()))
            .build();
        entry = client.createLogEntry(entry);
        assertThat(entry.getId(), instanceOf(Integer.class));
        assertThat(entry.getText(), equalTo("Test\nThis is a test"));
    }
}
