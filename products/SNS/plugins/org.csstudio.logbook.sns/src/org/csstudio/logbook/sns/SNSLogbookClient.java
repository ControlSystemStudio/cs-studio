/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.csstudio.logbook.Attachment;
import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.Property;
import org.csstudio.logbook.Tag;
import org.csstudio.logbook.sns.elog.ELog;

/** {@link LogbookClient} for SNS 'ELog'
 *  @author ky9
 */
@SuppressWarnings("nls")
public class SNSLogbookClient implements LogbookClient
{
    final private String url;
    final private String user;
    final private String password;

    /** Initialize
     *  @param url RDB URL
     *  @param user User
     *  @param password Password
     *  @throws Exception on error
     */
    public SNSLogbookClient(final String url, final String user, final String password) throws Exception
    {
        // Connect to the SNS RDB
        if (user.length() <= 0)
            throw new Exception("Empty user name");
        if (password.length() <= 0)
            throw new Exception("Empty password");
        if (url == null)
            throw new Exception("Missing logbook URL");
        this.url = url;
        this.user = user;
        this.password = password;
    }
    
    /** {@inheritDoc} */
    @Override
    public Collection<Logbook> listLogbooks() throws Exception
    {
        final ELog elog = new ELog(url, user, password);
        try
        {
            return Converter.convertLogbooks(elog.getLogbooks());
        }
        finally
        {
            elog.close();
        }
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Tag> listTags() throws Exception
    {
        final ELog elog = new ELog(url, user, password);
        try
        {
            return Converter.convertCategories(elog.getCategories());
        }
        finally
        {
            elog.close();
        }
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Property> listProperties() throws Exception
    {
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Attachment> listAttachments(final Object logId)
            throws Exception
    {
        final long entry_id = getEntryID(logId);
        final ELog elog = new ELog(url, user, password);
        try
        {
            return Converter.convertAttachments(elog.getImageAttachments(entry_id),
                    elog.getOtherAttachments(entry_id));
        }
        finally
        {
            elog.close();
        }
    }

    /** {@inheritDoc} */
    @Override
    public InputStream getAttachment(Object logId, String attachmentFileName)
            throws Exception
    {
        // TODO Why this in addition to listAttachments?
        // What file name when it's all based on streams?
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public LogEntry findLogEntry(final Object logId) throws Exception
    {
        final long entry_id = getEntryID(logId);
        final ELog elog = new ELog(url, user, password);
        try
        {
            return new SNSLogEntry(entry_id, elog.getEntry(entry_id));
        }
        finally
        {
            elog.close();
        }
    }

    /** {@inheritDoc} */
    @Override
    public Collection<LogEntry> findLogEntries(String search) throws Exception
    {
        // TODO Support locating entries based on time range, ...
        // once the API is clearer
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public LogEntry createLogEntry(final LogEntry entry) throws Exception
    {
        final String[] title_body = getTitleAndBody(entry.getText());
        if (title_body[0].isEmpty())
            throw new Exception("Empty title");
        final String title = title_body[0];
        final String text = title_body[1];

        if (entry.getLogbooks().isEmpty())
            throw new Exception("No logbook specified");
        
        final Iterator<Logbook> logbooks = entry.getLogbooks().iterator();
        String logbook = logbooks.next().getName();
        
        final ELog elog = new ELog(url, user, password);
        final long id;
        try
        {
            id = elog.createEntry(logbook, title, text);
        
            // Attach to multiple logbooks?
            while (logbooks.hasNext())
            {
                logbook = logbooks.next().getName();
                elog.addLogbook(id, logbook);
            }
            
            // Add optional tags
            for (Tag tag : entry.getTags())
                elog.addCategory(id, tag.getName());
            
            // Add optional attachments
            for (Attachment attachment : entry.getAttachment())
            {
                final String name = attachment.getFileName();
                final InputStream stream = attachment.getInputStream();
                if (stream != null)
                    elog.addAttachment(id, name, name, stream);
            }
            
            // API requires returning the entry as actually written...
            return findLogEntry(id);
        }
        finally
        {
            elog.close();
        }
    }

    /** Split complete logbook text into title and content
     *  @param text
     *  @return Title and content
     */
    static String[] getTitleAndBody(String text)
    {
        // Use one type of newline
        // Some entries use the Windows-type \r\n, others just \r
        text = text.replace("\r\n", "\n");
        text = text.replace('\r', '\n');

        // Replace more than 2 newlines with just 2 to avoid too many empty lines
        text = text.replaceAll("\n{2,}", "\n\n");
        
        final int nl = text.indexOf("\n");
        if (nl < 0)
            // No title. Use the text for both title and body
            return new String[] { text, text };
        
        // Extract title
        String title = text.substring(0, nl).trim();
        String body = text.substring(nl+1).trim();
        return new String[] { title, body };
    }

    /** SNS Logbook cannot 'update'. Create new entry
     *  @param entry New entry
     *  @return New entry
     *  @throws Exception on error
     */
    @Override
    public LogEntry updateLogEntry(final LogEntry entry) throws Exception
    {
        return createLogEntry(entry);
    }

    // Why this in addition to updateLogEntry?
    @Override
    public void updateLogEntries(final Collection<LogEntry> entires)
            throws Exception
    {
        for (LogEntry entry : entires)
            updateLogEntry(entry);
    }

    // Why this in addition to createLogEntry which already handles attachments?
    @Override
    public Attachment addAttachment(final Object logId, final InputStream stream, final String name)
            throws Exception
    {
        final long entry_id = getEntryID(logId);
        
        final ELog elog = new ELog(url, user, password);
        try
        {
            return Converter.convertAttachment(elog.addAttachment(entry_id, name, name, stream));
        }
        finally
        {
            elog.close();
        }
    }

    /** @param logId Log ID as used by {@link LogbookClient} API
     *  @return Log entry ID of SNS logbook
     *  @throws Exception on error
     */
    private long getEntryID(final Object logId) throws Exception
    {
        if (! (logId instanceof Long))
            throw new Exception("Expecting Integer log entry ID");
        return ((Long) logId).intValue();
    }
}
