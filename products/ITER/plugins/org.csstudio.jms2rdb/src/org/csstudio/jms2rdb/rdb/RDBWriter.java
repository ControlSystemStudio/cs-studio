/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.jms2rdb.rdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.MapMessage;

import org.csstudio.jms2rdb.Activator;
import org.csstudio.logging.JMSLogMessage;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;

/** Class that writes JMSLogMessages to the RDB
 *  @author Kay Kasemir
 *  @author Lana Abadie - PostgreSQL additions. Disable autocommit as needed.
 *  reviewed by Katia Danilova 08/20/08
 */
@SuppressWarnings("nls")
public class RDBWriter
{
	private static final int MAX_VALUE_LENGTH = 100;

    private static final int MAX_NAME_LENGTH = 80;

    /** Enable Oracle statistics? */
	private static final boolean enable_trace = false;

    /** RDB Utility */
    final private RDBUtil rdb_util;

    /** SQL statements */
    final private SQL sql;

    /** Map of Property IDs, mapping property name to numeric ID */
    final private HashMap<String, Integer> properties =
    	new HashMap<String, Integer>();

    /** Lazily initialized statement */
    private PreparedStatement next_message_id_statement;

    /** Lazily initialized statement */
    private PreparedStatement insert_message_statement;

    /** Lazily initialized statement */
    private PreparedStatement insert_property_statement;

    /** Constructor
     *  @param url RDB URL
     *  @param schema Schema name or ""
     *  @throws Exception on error
     */
    public RDBWriter(final String url, final String schema) throws Exception
    {
        try
        {
            rdb_util = RDBUtil.connect(url, false);
        }
        catch (Exception ex)
        {
            throw new Exception("Error connecting to '" + url + "': " + ex.getMessage());
        }

        if (enable_trace)
        {
            final Statement statement = rdb_util.getConnection().createStatement();
            statement.execute("alter session set tracefile_identifier='KayTest'");
            statement.execute("ALTER SESSION SET events " +
                    "'10046 trace name context forever, level 12'");
        }

        sql = new SQL(rdb_util, schema);
        final Connection connection = rdb_util.getConnection();
        
        if (sql.select_next_message_id != null)
            next_message_id_statement =
                connection.prepareStatement(sql.select_next_message_id);

        if (rdb_util.getDialect() == Dialect.PostgreSQL)
			insert_message_statement =
        				connection.prepareStatement(sql.insert_message_id_datum_type_name_severity);
        else // MySQL, other RDB that supports RETURN_GENERATED_KEYS
        	insert_message_statement =
        			connection.prepareStatement(sql.insert_message_id_datum_type_name_severity,
        					Statement.RETURN_GENERATED_KEYS);
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
    	final Integer int_id = properties.get(property_name);
    	if (int_id != null)
    		return int_id.intValue();
    	// Perform RDB query
        final Connection connection = rdb_util.getConnection();
        PreparedStatement statement =
            connection.prepareStatement(sql.select_property_id_by_name);
        statement.setString(1, property_name);
        try
        {
            final ResultSet result = statement.executeQuery();
            if (result.next())
            {	// Add to cache
            	final int id = result.getInt(1);
                properties.put(property_name, Integer.valueOf(id));
				return id;
            }
        }
        finally
        {
            statement.close();
        }
        // Insert unknown message property: Get next ID
        // This does not use a sequence!
        // Fundamentally, there is a small chance that multiple instances
        // of this program will try to create duplicate property entries.
        // In reality, it probably doesn't matter.
        // Since we wrap the whole one-message-write into a transaction,
        // the worst case would be one lost message because of a property ID clash.
        statement = connection.prepareStatement(sql.select_next_property_id);
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

        rdb_util.getConnection().setAutoCommit(false);
        statement = connection.prepareStatement(sql.insert_property_id);
        statement.setInt(1, next_id);
        statement.setString(2, property_name);
        try
        {
        	statement.executeUpdate();
        	rdb_util.getConnection().commit();
        }
        catch(Exception e)
        {
        	rdb_util.getConnection().rollback();
        	throw e;
        }
        finally
        {
            statement.close();
            rdb_util.getConnection().setAutoCommit(true);
        }
        Activator.getLogger().log(Level.WARNING,
    		"Inserted previously unused Message Property {0} as ID {1}",
    		new Object[] { property_name, next_id } );
        // Add to cache
    	properties.put(property_name, Integer.valueOf(next_id));
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
     *  @param message Text to write
     *  @throws Exception on error
     */
    public void write(final String message) throws Exception
    {
        final Connection connection = rdb_util.getConnection();
        connection.setAutoCommit(false);
        try
        {
            final long message_id = insertMessage(JMSLogMessage.TYPE, null, "INFO");
            batchProperty(message_id, JMSLogMessage.TEXT, message);
            insert_property_statement.executeBatch();
            connection.commit();
        }
        catch (Exception ex)
        {
            connection.rollback();
            throw ex;
        }
        finally
        {
        	connection.setAutoCommit(true);
        }
    }

    /** Write log message to RDB
     *  @param message MapMessage to write
     *  @throws Exception on error
     */
    @SuppressWarnings("unchecked")
	public void write(final MapMessage map) throws Exception
    {
		final String type = map.getString(JMSLogMessage.TYPE);
		final String name = map.getString(JMSLogMessage.NAME);
		final String severity = map.getString(JMSLogMessage.SEVERITY);

        final Connection connection = rdb_util.getConnection();
        connection.setAutoCommit(false);
        try
        {
    		final long message_id = insertMessage(type, name, severity);

            final Enumeration<String> props = map.getMapNames();
            while (props.hasMoreElements())
            {
            	final String prop = props.nextElement();
            	// Skip properties which are already in message table columns
            	if (JMSLogMessage.TYPE.equals(prop) ||
            	    JMSLogMessage.NAME.equals(prop) ||
            	    JMSLogMessage.SEVERITY.equals(prop))
            		continue;
            	batchProperty(message_id, prop, map.getString(prop));
            }
            insert_property_statement.executeBatch();
            connection.commit();
        }
        catch (Exception ex)
        {
            connection.rollback();
            throw ex;
        }
        finally
        {
        	connection.setAutoCommit(true);
        }
    }

    /** Insert a new message
     *  @param type  Message type
     *  @param name Primary name (PV name, ...) to which the msg refers. May be <code>null</code>
     *  @param severity Message severity
     *  @return ID of the new message row
     *  @throws Exception on error
     */
    private long insertMessage(
    		final String type, String name,
    		final String severity) throws Exception
    {
        long message_id = -1;
        if (rdb_util.getDialect() == Dialect.Oracle)
        {
           // Read next unique message ID from sequence
            final ResultSet result = next_message_id_statement.executeQuery();
            if (result.next())
                message_id = result.getInt(1);
            else
            {
            	result.close();
                throw new Exception("Cannot obtain next message ID");
            }
            result.close();
            insert_message_statement.setLong(5, message_id);
        }
        // else: Depend on AUTO_INCREMENT resp. SERIAL for new ID, then read it after insert

        // Insert the main message
        final Date now = new Date();
        insert_message_statement.setTimestamp(1, new Timestamp(now.getTime()));
        insert_message_statement.setString(2, type);
        // Overcome RDB limitations
        if (name == null)
            name = "";
        else if (name.length() > MAX_NAME_LENGTH)
        {
            Activator.getLogger().log(Level.WARNING,
                "Limiting NAME = {0} to {1} characters",
                new Object[] { name, MAX_NAME_LENGTH });
            name = name.substring(0, MAX_NAME_LENGTH);
        }
        insert_message_statement.setString(3, name);
        insert_message_statement.setString(4, severity);

        // PostgreSQL, MySQL: Read auto-assigned unique message ID
        if (rdb_util.getDialect() == Dialect.PostgreSQL)
        {
            final ResultSet result = insert_message_statement.executeQuery();
    	    if (result.next())
        	{
        	      message_id = result.getInt(1);
        	      result.close();
        	}
        	else
        	{
        		 result.close();
                throw new Exception("Cannot obtain next message ID");
        	}
        }
        else if (rdb_util.getDialect() == Dialect.MySQL)
        {
            final int rows = insert_message_statement.executeUpdate();
            if (rows != 1)
                throw new Exception("Inserted " + rows + " instead of 1 Message");

            final ResultSet result = insert_message_statement.getGeneratedKeys();
            if (result.next())
                message_id = result.getInt(1);
            else
            {
                result.close();
                throw new Exception("Cannot obtain next message ID");
            }
            result.close();
        }
        else // Oracle
        {
            final int rows = insert_message_statement.executeUpdate();
            if (rows != 1)
                throw new Exception("Inserted " + rows + " instead of 1 Message");
        }

        final Logger logger = Activator.getLogger();
        if (logger.isLoggable(Level.FINE))
        {
            logger.fine("Message " + message_id + ":");
            logger.fine("  TYPE          : " + type);
            logger.fine("  DATUM         : " + now);
            logger.fine("  NAME          : " + name);
            logger.fine("  SEVERITY      : " + severity);
        }
        return message_id;
    }

    /** Insert a property, add content to a message
     *  @param message_id ID of message to which this property belongs
     *  @param property_id ID of the property type
     *  @param value Value of the property
     *  @throws Exception on error
     */
    private boolean batchProperty(final long message_id,
            final String property, String value) throws Exception
    {
        // Don't bother to insert empty properties
        if (value == null  ||  value.isEmpty())
            return false;

        final int property_id = getPropertyType(property);

        insert_property_statement.setLong(1, message_id);
        insert_property_statement.setInt(2, property_id);
        // Overcome RDB limitations
        if (value.length() > MAX_VALUE_LENGTH)
        {
            Activator.getLogger().log(Level.WARNING,
                    "Limiting {0} = {1} to {2} characters",
                    new Object[] { property, value, MAX_NAME_LENGTH });
            value = value.substring(0, MAX_VALUE_LENGTH);
        }
        insert_property_statement.setString(3, value);
        insert_property_statement.addBatch();

        Activator.getLogger().fine(String.format("  %-14s: %s", property, value));
        return true;
    }
}
