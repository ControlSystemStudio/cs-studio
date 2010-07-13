/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.alarm.beast.SQL;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.StringIDHelper;

/** Mapper between alarm status messages and RDB IDs
 *  @author Kay Kasemir
 */
public class MessageMapping
{
    /** Helper for Message-to-ID mapping in RDB */
    final private StringIDHelper helper;
    
    /** Cash of Messages by ID to limit RDB lookups */
    final private Map<Integer, String> cache_by_id = new HashMap<Integer, String>();

    /** Cash of Messages by ID to limit RDB lookups */
    final private Map<String, Integer> cache_by_message = new HashMap<String, Integer>();
    
    /** Initialize
     *  @param rdb RDBUtil
     *  @param sql SQL statements
     */
    public MessageMapping(final RDBUtil rdb, final SQL sql)
    {
        helper = new StringIDHelper(rdb, sql.schema_prefix + sql.message_table,
                                    sql.message_id_col, sql.message_name_col);
    }

    /** @param id RDB ID of a message
     *  @return Message for that ID
     *  @throws Exception on error
     */
    public String findMessageById(final int id) throws Exception
    {
        String message = cache_by_id.get(id);
        if (message == null)
        {   // Not cached, get from RDB and remember the result
            message = helper.find(id).getName();
            cache_by_id.put(id, message);
            cache_by_message.put(message, id);
        }
        return message;
    }

    /** If message is already in RDB, get its ID.
     *  Otherwise add message to RDB, getting its new ID.
     *  @param message Message to add
     *  @return RDB ID
     *  @throws Exception on error
     */
    public int findOrAddMessage(final String message) throws Exception
    {
        // First check local cache
        Integer id = cache_by_message.get(message);
        if (id != null)
            return id.intValue();
        // Add to RDB, remember in cache
        id = helper.add(message).getId();
        cache_by_id.put(id, message);
        cache_by_message.put(message, id);
        return id;
    }
}
