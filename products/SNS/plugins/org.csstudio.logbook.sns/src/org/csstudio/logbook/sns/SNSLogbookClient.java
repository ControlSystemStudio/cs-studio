/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.csstudio.logbook.Attachment;
import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.Property;
import org.csstudio.logbook.Tag;
import org.csstudio.platform.utility.rdb.RDBUtil;

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
        final RDBUtil rdb = RDBUtil.connect(url, user, password, false);
        final List<Logbook> logbooks = new ArrayList<Logbook>();
        final Statement statement = rdb.getConnection().createStatement();
        try
        {
            final ResultSet result = statement.executeQuery(
                    "SELECT oper_grp_nm FROM oper.oper_grp " +
                    "WHERE elog_ind='Y' ORDER BY oper_grp_nm");
            while (result.next())
                logbooks.add(new SNSLogbook(result.getString(1)));
        }
        finally
        {
            statement.close();
            rdb.close();
        }
        return logbooks;
    }

    @Override
    public Collection<Tag> listTags() throws Exception
    {
        return Collections.emptyList();
    }

    @Override
    public Collection<Property> listProperties() throws Exception
    {
        return Collections.emptyList();
    }

    @Override
    public Collection<Attachment> listAttachments(Object logId)
            throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getAttachment(Object logId, String attachmentFileName)
            throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LogEntry findLogEntry(Object logId) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<LogEntry> findLogEntries(
            Map<String, String> findAttributeMap) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

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
        
        final String logbook = entry.getLogbooks().iterator().next().getName();
        
        final SNSLogbookSupport support = new SNSLogbookSupport(url, user, password);
        final long id;
        try
        {
            id = support.createEntry(logbook, title, text);
            // Add optional attachments
            for (Attachment attachment : entry.getAttachment())
            {
                final String name = attachment.getFileName();
                final InputStream stream = attachment.getInputStream();
                if (stream != null)
                    support.addAttachment(id, name, name, stream);
            }
        }
        finally
        {
            support.close();
        }
        return new SNSLogEntry(id, entry);
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

    @Override
    public LogEntry updateLogEntry(final LogEntry entry) throws Exception
    {
        return createLogEntry(entry);
    }

    @Override
    public void updateLogEntries(final Collection<LogEntry> entires)
            throws Exception
    {
        throw new Exception("Not supported by the SNS logbook. Create new entry");
    }

    @Override
    public Attachment addAttachment(final Object logId, final InputStream stream, final String name)
            throws Exception
    {
        final long entry_id = getEntryID(logId);
        
        final SNSLogbookSupport support = new SNSLogbookSupport(url, user, password);
        try
        {
            return support.addAttachment(entry_id, name, name, stream);
        }
        finally
        {
            support.close();
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
