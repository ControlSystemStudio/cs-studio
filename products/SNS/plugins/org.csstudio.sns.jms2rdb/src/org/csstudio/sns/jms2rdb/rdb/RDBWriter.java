package org.csstudio.sns.jms2rdb.rdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;

import javax.jms.MapMessage;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.logging.JMSLogMessage;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** Class that writes JMSLogMessages to the RDB
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBWriter
{
    private static final String DEFAULT_MESSAGE_TYPE = "log";

	private static final String TYPE_PROPERTY = "TYPE";

	private static final int MAX_VALUE_LENGTH = 300;

    /** RDB Utility */
    final private RDBUtil rdb_util;
    
    /** SQL statements */
    final private SQL sql;
    
    /** Cache of Message Type/ID mappings */
	final private HashMap<String, Integer> message_type
		= new HashMap<String, Integer>();

    /** Map of Property IDs, mapping property name to numeric ID */
    final private HashMap<String, Integer> property_id = 
    	new HashMap<String, Integer>();
    
    /** Lazily initialized statement */
    private PreparedStatement next_message_id_statement;

    /** Lazily initialized statement */
    private PreparedStatement insert_message_statement;

    /** Lazily initialized statement */
    private PreparedStatement next_property_id;

    /** Lazily initialized statement */
    private PreparedStatement insert_property_statement;

    /** Constructor
     *  @param url RDB URL
     *  @param schema Schema name or ""
     *  @throws Exception on error
     */
    public RDBWriter(final String url, final String schema) throws Exception
    {
        rdb_util = RDBUtil.connect(url);
        sql = new SQL(rdb_util, schema);
        
        getPropertyType(JMSLogMessage.TYPE);
        getPropertyType(JMSLogMessage.TEXT);
        getPropertyType(JMSLogMessage.CREATETIME);
        getPropertyType(JMSLogMessage.EVENTTIME);
        getPropertyType(JMSLogMessage.CLASS);
        getPropertyType(JMSLogMessage.NAME);
        getPropertyType(JMSLogMessage.FILENAME);
        getPropertyType(JMSLogMessage.APPLICATION_ID);
        getPropertyType(JMSLogMessage.HOST);
        getPropertyType(JMSLogMessage.USER);
        
        final Connection connection = rdb_util.getConnection();
        next_message_id_statement =
            connection.prepareStatement(sql.select_next_message_id);
        insert_message_statement =
            connection.prepareStatement(sql.insert_message_type_datum);
        next_property_id =
            connection.prepareStatement(sql.select_next_content_id);
        insert_property_statement =
            connection.prepareStatement(sql.insert_message_property_value);
    }
    
    /** Query RDB for the numeric ID of message.
     *  @param type Message type name
     *  @return Numeric ID
     *  @throws Exception on error
     */
    private int getMessageTypeID(final String type) throws Exception
    {	// Cache lookup
    	final Integer int_id = message_type.get(type);
    	if (int_id != null)
    		return int_id.intValue();
        final PreparedStatement statement =
        	rdb_util.getConnection().prepareStatement(sql.select_message_type);
        try
        {
        	statement.setString(1, type);
            final ResultSet result = statement.executeQuery();
            if (result.next())
            {	// Add to cache
                final int id = result.getInt(1);
                message_type.put(type, id);
				return id;
            }
        }
        finally
        {
            statement.close();
        }
        throw new Exception("Cannot locate ID for 'log' messages");
    }

    /** Get numeric ID of a property, using either the local cache
     *  or querying the RDB.
     *  @param property_name
     *  @return Numeric property ID
     *  @throws Exception on error
     */
    private int getPropertyType(final String property_name) throws Exception
    {
    	// First try cache
    	final Integer int_id = property_id.get(property_name);
    	if (int_id != null)
    		return int_id.intValue();
    	// Perform RDB query
        PreparedStatement statement =
            rdb_util.getConnection().prepareStatement(sql.select_property_id_by_name);
        statement.setString(1, property_name);
        try
        {
            final ResultSet result = statement.executeQuery();
            if (result.next())
            {	// Add to cache
            	final int id = result.getInt(1);
                property_id.put(property_name, new Integer(id));
				return id;
            }
        }
        finally
        {
            statement.close();
        }
        // Insert unknown message property: Get next ID
        statement =
            rdb_util.getConnection().prepareStatement(sql.select_next_property_id);
        int next_id;
        try
        {
            final ResultSet result = statement.executeQuery();
            if (result.next())
            	next_id = result.getInt(1);
            else
            	throw new Exception("Cannot get new ID for " + property_name);
        }
        finally
        {
            statement.close();
        }
        statement =
            rdb_util.getConnection().prepareStatement(sql.insert_property_id);
        statement.setInt(1, next_id);
        statement.setString(2, property_name);
        try
        {
        	statement.executeUpdate();
        }
        finally
        {
            statement.close();
        }
        CentralLogger.getInstance().getLogger(this).warn(
    		"Inserted unkown Message Property " + property_name + " as ID "
    		+ next_id);
        // Add to cache
    	property_id.put(property_name, new Integer(next_id));
		return next_id;    
	}

    /** Close the RDB connection */
    public void close()
    {
        if (next_message_id_statement != null)
        {
            try
            {
                next_message_id_statement.close();
            }
            catch (Exception ex)
            { /* Ignore */ }
        }
        if (insert_message_statement != null)
        {
            try
            {
                insert_message_statement.close();
            }
            catch (Exception ex)
            { /* Ignore */ }
        }
        if (insert_property_statement != null)
        {
            try
            {
                insert_property_statement.close();
            }
            catch (Exception ex)
            { /* Ignore */ }
        }
        rdb_util.close();
    }

    /** Write log message to RDB
     *  @param message JMSLogMessage to write
     *  @throws Exception on error
     */
    public void write(final JMSLogMessage message) throws Exception
    {
        final int message_id = getNextMessageID();
        insertMessage(message_id, DEFAULT_MESSAGE_TYPE);
        // Since batched inserts are only performed at the end,
        // a 'select' for the next content ID won't see them, yet.
        // So we have to count them up in here
        int content_id = getNextContentID();
        if (batchProperty(message_id, content_id,
        		getPropertyType(JMSLogMessage.TYPE), JMSLogMessage.TYPE_LOG))
            ++content_id;
        if (batchProperty(message_id, content_id, 
        		getPropertyType(JMSLogMessage.TEXT), message.getText()))
            ++content_id;
        if (batchProperty(message_id, content_id, 
        		getPropertyType(JMSLogMessage.CREATETIME),
            JMSLogMessage.date_format.format(message.getCreateTime().getTime())))
            ++content_id;
        if (batchProperty(message_id, content_id, 
        		getPropertyType(JMSLogMessage.EVENTTIME),
            JMSLogMessage.date_format.format(message.getEventTime().getTime())))
            ++content_id;
        if (batchProperty(message_id, content_id, 
        		getPropertyType(JMSLogMessage.CLASS), message.getClassName()))
            ++content_id;
        if (batchProperty(message_id, content_id, 
        		getPropertyType(JMSLogMessage.NAME), message.getMethodName()))
            ++content_id;
        if (batchProperty(message_id, content_id, 
        		getPropertyType(JMSLogMessage.FILENAME), message.getFileName()))
            ++content_id;
        if (batchProperty(message_id, content_id, 
        		getPropertyType(JMSLogMessage.APPLICATION_ID), message.getApplicationID()))
            ++content_id;
        if (batchProperty(message_id, content_id, 
        		getPropertyType(JMSLogMessage.HOST), message.getHost()))
            ++content_id;
        batchProperty(message_id, content_id, 
        		getPropertyType(JMSLogMessage.USER), message.getUser());
        insert_property_statement.executeBatch();
        rdb_util.getConnection().commit();
    }

    /** Write log message to RDB
     *  @param message JMSLogMessage to write
     *  @throws Exception on error
     */
    @SuppressWarnings("unchecked")
	public void write(final MapMessage map) throws Exception
    {
    	String type = map.getString(TYPE_PROPERTY);
    	if (type == null)
    		type = DEFAULT_MESSAGE_TYPE;
        final int message_id = getNextMessageID();
        insertMessage(message_id, type);
        // Since batched inserts are only performed at the end,
        // a 'select' for the next content ID won't see them, yet.
        // So we have to count them up in here
        int content_id = getNextContentID();
        final Enumeration<String> props = map.getMapNames();
        while (props.hasMoreElements())
        {
        	final String prop = props.nextElement();
        	final int prop_id = getPropertyType(prop);
            if (batchProperty(message_id, content_id,
            		prop_id, map.getString(prop)))
                ++content_id;
        }
        insert_property_statement.executeBatch();
        rdb_util.getConnection().commit();
    }

    /** @return Next available message ID
     *  @throws Exception on error
     */
    private int getNextMessageID() throws Exception
    {
        final ResultSet result = next_message_id_statement.executeQuery();
        if (result.next())
            return result.getInt(1);
        throw new Exception("Cannot obtain next message ID");
    }

    /** Insert a new message
     *  @param message_id ID for the new message
     *  @param type  Message type
     *  @throws Exception on error
     */
    private void insertMessage(final int message_id, final String type) throws Exception
    {
        // Insert the main message
        insert_message_statement.setInt(1, message_id);
        insert_message_statement.setInt(2, getMessageTypeID(type));
        final Calendar now = Calendar.getInstance();
        insert_message_statement.setTimestamp(3, new Timestamp(now.getTimeInMillis()));
        final int rows = insert_message_statement.executeUpdate();
        if (rows != 1)
            throw new Exception("Inserted " + rows + " instead of 1 Message");
    }

    /** Insert a property, add content to a message
     *  @param message_id ID of message to which this property belongs
     *  @param content_id ID of message content entry for this prop/value
     *  @param property_id ID of the property type
     *  @param value Value of the property
     *  @throws Exception on error
     */
    private boolean batchProperty(final int message_id, final int content_id,
            final int property_id, String value) throws Exception
    {
        // Don't bother to insert empty properties
        if (value.length() <= 0)
            return false;
        insert_property_statement.setInt(1, content_id);
        insert_property_statement.setInt(2, message_id);
        insert_property_statement.setInt(3, property_id);
        // Overcome Oracle limitations
        if (value.length() > MAX_VALUE_LENGTH)
            value = value.substring(0, MAX_VALUE_LENGTH);
        insert_property_statement.setString(4, value);
        insert_property_statement.addBatch();
        return true;
    }
    
    /** @return Next available message ID
     *  @throws Exception on error
     */
    private int getNextContentID() throws Exception
    {
        final ResultSet result = next_property_id.executeQuery();
        if (result.next())
            return result.getInt(1);
        throw new Exception("Cannot obtain next content ID");
    }
}
