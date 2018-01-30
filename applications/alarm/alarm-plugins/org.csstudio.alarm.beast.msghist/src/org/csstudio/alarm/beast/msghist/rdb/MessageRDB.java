/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.rdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.alarm.beast.msghist.Activator;
import org.csstudio.alarm.beast.msghist.Messages;
import org.csstudio.alarm.beast.msghist.model.Message;
import org.csstudio.alarm.beast.msghist.model.MessagePropertyFilter;
import org.csstudio.alarm.beast.msghist.model.PVMessage;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

/** Helper for accessing the CSS message RDB.
 *  @author Kay Kasemir
 *  @author Borut Terpinc
 */
@SuppressWarnings("nls")
public class MessageRDB
{
    /** Util. for connection to RDB */
    final private RDBUtil rdb_util;

    /** SQL statements */
    final private SQL sql;

    /** Connect to RDB
     *  @param url Database URL
     *  @param user
     *  @param password
     *  @param schema Database schema ending in "." or "" if not used
     *  @throws Exception on error
     *  @see RDBUtil#connect(String)
     *  @see #close()
     */
    public MessageRDB(final String url, final String user,
            final String password, final String schema ) throws Exception
    {
        rdb_util = RDBUtil.connect(url, user, password, true);
        sql = new SQL(rdb_util, schema);
    }

    /** Close RDB, release resources */
    public void close()
    {
        rdb_util.close();
    }

    /** Read messages from start to end time, maybe including filters.
     *  <p>
     *  @param monitor Used to display progress, also checked for cancellation
     *  @param start Start time
     *  @param end End time
     *  @param filters Filters to use (not <code>null</code>).
     *  @param max_messages Limit on the number of messages retrieved.
     *  @return Array of Messages or <code>null</code>
     */
    public Message[] getMessages(
            final IProgressMonitor monitor,
            final Calendar start, final Calendar end,
            final MessagePropertyFilter filters[],
            final int max_messages, final DateTimeFormatter date_format) throws Exception
    {
        monitor.beginTask("Reading Messages", IProgressMonitor.UNKNOWN);
        final ArrayList<Message> messages = new ArrayList<Message>();
        // Create new select statement
        final String sql_txt = sql.createSelect(rdb_util, filters);
        final Connection connection = rdb_util.getConnection();
        connection.setReadOnly(true);
        final PreparedStatement statement =
                connection.prepareStatement(sql_txt);
        try
        {
            StringBuffer spLog= new StringBuffer();

            int parm = 1;
            // Set start/end
            statement.setTimestamp(parm++, new Timestamp(start.getTimeInMillis()));
            spLog.append(System.lineSeparator()).append(parm).append(" - ").append(new Timestamp(start.getTimeInMillis()));
            statement.setTimestamp(parm++, new Timestamp(end.getTimeInMillis()));
            spLog.append(System.lineSeparator()).append(parm).append(" - ").append(new Timestamp(end.getTimeInMillis()));

            // Set filter parameters
            for (MessagePropertyFilter filter : filters){
                statement.setString(parm++, filter.getPattern());
                spLog.append(System.lineSeparator()).append(parm).append(" - ").append(filter.getPattern());
            }
            // Set query limit a bit higher than max_messages.
            // This still limits the number of messages on the RDB side,
            // but allows the following code to detect exhausting the limit.
            statement.setInt(parm++, max_messages+1);
            spLog.append(System.lineSeparator()).append(parm).append(" - ").append(max_messages+1);

            // One benchmark example:
            // Query took <<1 second, but reading all the messages took ~30.
            // Same result when only calling 'next',
            // i.e. commenting the 'next' loop body.
            // So the local lookup of properties and packing
            // into a HashMap adds almost nothing to the overall time.

            Activator.getLogger().log(Level.FINEST, () -> "Message history sql parameters:" + spLog.toString());
            Activator.getLogger().log(Level.FINEST, () -> "Message history sql query:" + sql_txt.toString());

            final ResultSet result = statement.executeQuery();
            int sequence = 0;
            // Initialize id and datum as "no current message"
            int id = -1;
            Date datum = null;
            Date last_datum = null;
            Message last_message = null;
            Map<String, String> props = null;
            int msg_count = 0;
            while (!monitor.isCanceled()  &&  result.next())
            {
                // Fixed ID and DATUM
                final int next_id = result.getInt(1);
                final Date next_datum = result.getTimestamp(2);
                // New message?
                if (next_id != id)
                {   // Does this conclude a previous message?
                    if (props != null)
                    {
                        final Message message = createMessage(++sequence, id, props);
                        messages.add(message);
                        // Maybe set the 'delta' of previous message
                        if (last_message != null  &&  last_datum != null)
                            last_message.setDelta(last_datum, datum);
                        last_datum = datum;
                        last_message = message;
                        // Update monitor every 50 messages
                        final int count = messages.size();
                        if (count % 50 == 0)
                            monitor.subTask(count + " messages...");
                        ++msg_count;
                    }
                    // Construct new message and values
                    props = new HashMap<String, String>();
                    id = next_id;
                    datum = next_datum;
                    props.put(Message.DATUM, date_format.format(datum.toInstant()));
                }
                // Get Prop/Value from MESSAGE table
                int res_idx = 3;
                for (int i=0; i<sql.messagePropertyCount(); ++i)
                    props.put(sql.getMessageProperty(i), result.getString(res_idx++));
                // Prop/Value from MESSAGE_CONTENT table
                final String prop = sql.getPropertyNameById(result.getInt(res_idx++));
                final String value = result.getString(res_idx);
                props.put(prop, value);
            }
            // No more results.
            // Was another (partial) message assembled?
            // This message may miss some properties because the RDB retrieval
            // is limited by property max_properties...
            if (props != null  &&  props.isEmpty() == false)
            {
                messages.add(createMessage(++sequence, id, props));
                if (last_message != null  &&  last_datum != null)
                    last_message.setDelta(last_datum, datum);
            }

            // Was readout stopped because we reached max. number of messages?
            if (msg_count >= max_messages)
            {
                props = new HashMap<String, String>();
                props.put(Message.TYPE, "internal");
                props.put(Message.SEVERITY, "FATAL");
                props.put("TEXT",
                        NLS.bind(Messages.ReachedMaxMessagesFmt, max_messages));
                // Add this message both as the first and last messages,
                // so user is more likely to see it.
                // A dialog box is even harder to miss,
                // but auto-refresh mode would result in either
                // blocked updates or a profusion of message boxes.
                messages.add(0, createMessage(0, -1, props));
                messages.add(createMessage(++sequence, -1, props));
            }
        }
        finally
        {
            statement.close();
            monitor.done();
        }

        // Convert to plain array
        final Message[] ret_val = new Message[messages.size()];
        return messages.toArray(ret_val);
    }

    /** Create Message or PVMessage
     *  @param sequence Sequence number
     *  @param id RDB ID
     *  @param props Remaining properties
     *  @return Message or PVMessage
     */
    private Message createMessage(final int sequence, final int id,
                                  final Map<String, String> props)
    {
        // Is there a better way to determine which messages
        // have PVs and which don't??
        if ("alarm".equalsIgnoreCase(props.get(Message.TYPE)))
            return new PVMessage(sequence, id, props);
        return new Message(sequence, id, props);
    }
}
