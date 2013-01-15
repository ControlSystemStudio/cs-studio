/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;

import javax.imageio.ImageIO;

import org.csstudio.logbook.AttachmentBuilder;
import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.LogbookBuilder;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.Tag;
import org.junit.Test;

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
    public void testListTags() throws Exception
    {
        final LogbookClient client = new SNSLogbookClientFactory().getClient();
        final Collection<Tag> tags = client.listTags();
        assertThat(tags, is(not(nullValue())));
        assertThat(tags.size(), is(not(0)));
        for (Tag tag : tags)
            System.out.println(tag);
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

    
    @Test //(timeout=10000)
    public void testCreateEntry() throws Exception
    {
        final LogbookClient client = new SNSLogbookClientFactory().getClient();
        
        LogEntry entry = LogEntryBuilder.withText("Test\nThis is a test")
            .addLogbook(LogbookBuilder.logbook(Preferences.getDefaultLogbook()))
            // Tags 'work', but the default user cannot add tags
            //.addTag(TagBuilder.tag("Controls"))
            .build();
        entry = client.createLogEntry(entry);
        assertThat(entry.getId(), instanceOf(Long.class));
        assertThat(entry.getText(), equalTo("Test\nThis is a test"));
    }

    /** Text attachment */
    @Test //(timeout=10000)
    public void testAddTextAttachment() throws Exception
    {
        final LogbookClient client = new SNSLogbookClientFactory().getClient();
        
        LogEntry entry = LogEntryBuilder.withText("Text attachment Test\nThis is a test")
            .addLogbook(LogbookBuilder.logbook(Preferences.getDefaultLogbook()))
            .build();
        entry = client.createLogEntry(entry);
        assertThat(entry.getId(), instanceOf(Long.class));
        assertThat(entry.getText(), equalTo("Text attachment Test\nThis is a test"));
        
        final InputStream stream = new ByteArrayInputStream("This is the attachment".getBytes());
        client.addAttachment(entry.getId(), stream, "demo.txt");
    }

    /** Long text, automatically turned into text attachment */
    @Test //(timeout=10000)
    public void testLongEntry() throws Exception
    {
        final LogbookClient client = new SNSLogbookClientFactory().getClient();
        
        // Happen to know that max text length is 4000...
        final StringBuilder buf = new StringBuilder();
        buf.append("Long Text Test\n");
        for (int i=0; i<=4000/28; ++i)
            buf.append("This is a long test text... ");
        assertTrue(buf.length() > 4000);
        LogEntry entry = LogEntryBuilder.withText(buf.toString())
            .addLogbook(LogbookBuilder.logbook(Preferences.getDefaultLogbook()))
            .build();
        entry = client.createLogEntry(entry);
        assertThat(entry.getId(), instanceOf(Long.class));
    }
    
    /** @return Stream for a PNG image
     *  @throws Exception on error
     */
    private static InputStream createImage() throws Exception
    {
        int WIDTH = 400;
        int height = 300;
        final BufferedImage image = new BufferedImage(WIDTH , height , BufferedImage.TYPE_INT_RGB);
        final Graphics gc = image.getGraphics();
        gc.setColor(new Color(100,  100, 255));
        gc.fillRect(0, 0, WIDTH, height);
        gc.setColor(new Color(100, 0, 0));
        final String text = "Test";
        final int wid = gc.getFontMetrics().stringWidth(text);
        gc.drawString(text, (WIDTH - wid)/2, height/2);
        
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ImageIO.write(image, "png", buffer);
        buffer.close();
        
        return new ByteArrayInputStream(buffer.toByteArray());
    }

    /** Image attachment */
    @Test //(timeout=10000)
    public void testAddImageAttachment() throws Exception
    {
        final LogbookClient client = new SNSLogbookClientFactory().getClient();
        
        LogEntry entry = LogEntryBuilder.withText("Image Test\nThis is a test")
            .addLogbook(LogbookBuilder.logbook(Preferences.getDefaultLogbook()))
            .build();
        entry = client.createLogEntry(entry);
        assertThat(entry.getId(), instanceOf(Long.class));
        assertThat(entry.getText(), equalTo("Image Test\nThis is a test"));
        
        client.addAttachment(entry.getId(), createImage(), "demo.png");
    }

    /** Entry with immediate Image attachment */
    @Test //(timeout=20000)
    public void testEntryWithImage() throws Exception
    {
        final LogbookClient client = new SNSLogbookClientFactory().getClient();
        
        LogEntry entry = LogEntryBuilder.withText("Entry with Image\nThis is a test")
            .addLogbook(LogbookBuilder.logbook(Preferences.getDefaultLogbook()))
            .attach(AttachmentBuilder.attachment("demo.png").inputStream(createImage()))
            .build();
        entry = client.createLogEntry(entry);
        assertThat(entry.getId(), instanceOf(Long.class));
        assertThat(entry.getText(), equalTo("Entry with Image\nThis is a test"));
    }

    /** Read some specific attachments */
    @Test
    public void testReadAttachments() throws Exception
    {
        final LogbookClient client = new SNSLogbookClientFactory().getClient();
        
        LogEntry entry = client.findLogEntry(Long.valueOf(395343));
        System.out.println(entry.getAttachment());

        entry = client.findLogEntry(Long.valueOf(395216));
        System.out.println(entry.getAttachment());
    }
}
