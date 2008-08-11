package org.csstudio.sns.jms2rdb.rdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
	private static final int MAX_VALUE_LENGTH = 100;
	
    /** Enable statistics for Jeff Patton ? */
	private static final boolean enable_trace = false;

    /** RDB Utility */
    final private RDBUtil rdb_util;
    
    /** SQL statements */
    final private SQL sql;
    
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
        
        if (enable_trace)
        {
            final Statement statement = rdb_util.getConnection().createStatement();
            statement.execute("alter session set tracefile_identifier='KayTest'");
            statement.execute("ALTER SESSION SET events " +
                    "'10046 trace name context forever, level 12'");
        }
        
        sql = new SQL(rdb_util, schema);

        final Connection connection = rdb_util.getConnection();
        next_message_id_statement =
            connection.prepareStatement(sql.select_next_message_id);
        insert_message_statement =
            connection.prepareStatement(sql.insert_message_id_datum_type_name_severity);
        next_property_id =
            connection.prepareStatement(sql.select_next_content_id);
        insert_property_statement =
            connection.prepareStatement(sql.insert_message_property_value);
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
        
        if (enable_trace)
        {
            try
            {
                final Statement statement = rdb_util.getConnection().createStatement();
                statement.execute("ALTER SESSION SET events '10046 trace name context off'");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
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
        insertMessage(message_id, JMSLogMessage.TYPE_LOG, message.getMethodName(),
        		message.getSeverity());
        // Since batched inserts are only performed at the end,
        // a 'select' for the next content ID won't see them, yet.
        // So we have to count them up in here
        int content_id = getNextContentID();
        if (batchProperty(message_id, content_id, 
        		getPropertyType(JMSLogMessage.TEXT), message.getText()))
            ++content_id;
        if (batchProperty(message_id, content_id, 
        		getPropertyType(JMSLogMessage.CREATETIME),
            JMSLogMessage.date_format.format(message.getCreateTime().getTime())))
            ++content_id;
        if (batchProperty(message_id, content_id, 
        		getPropertyType(JMSLogMessage.CLASS), message.getClassName()))
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
        final int message_id = getNextMessageID();
		String type = map.getString(JMSLogMessage.TYPE);
		String name = map.getString(JMSLogMessage.NAME);
        String severity = map.getString(JMSLogMessage.SEVERITY);
		insertMessage(message_id, type, name, severity);
        // Since batched inserts are only performed at the end,
        // a 'select' for the next content ID won't see them, yet.
        // So we have to count them up in here
        int content_id = getNextContentID();
        final Enumeration<String> props = map.getMapNames();
        while (props.hasMoreElements())
        {
        	final String prop = props.nextElement();
        	// Skip properties which are already in message table colums
        	if (JMSLogMessage.TYPE.equals(prop) ||
        	    JMSLogMessage.NAME.equals(prop) ||
        	    JMSLogMessage.SEVERITY.equals(prop))
        		continue;
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
    private void insertMessage(final int message_id,
    		final String type, final String name,
    		final String severity) throws Exception
    {
        // Insert the main message
        insert_message_statement.setInt(1, message_id);
        final Calendar now = Calendar.getInstance();
        insert_message_statement.setTimestamp(2, new Timestamp(now.getTimeInMillis()));
        insert_message_statement.setString(3, type);
        insert_message_statement.setString(4, name);
        insert_message_statement.setString(5, severity);
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
