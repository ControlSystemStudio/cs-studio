/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.alarm.beast.SQL;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.TimestampHelper;
import org.csstudio.alarm.beast.TreeItem;
import org.csstudio.alarm.beast.server.AlarmServer.Update;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** Alarm RDB Handler
 *  @author Kay Kasemir
 *  @author Lana Abadie - Disable autocommit as needed.
 *  @author Jaka Bobnar - RDB batching
 */
@SuppressWarnings("nls")
public class AlarmRDB
{
    /** Alarm Server */
    final private AlarmServer server;

    /** Connection to storage for configuration/state */
    final private RDBUtil rdb;

    /** RDB SQL statements */
    final private SQL sql;

    /** RDB connection. Used to check if the RDB reconnected */
    private Connection connection;

    final private String root_name;

    /** Map of severities and severity IDs in RDB */
    final private SeverityMapping severity_mapping;

    /** Map of message strings and IDs in RDB */
    final private MessageMapping message_mapping;

    /** Lazily (re-)created statement for updating the alarm state of a PV */
    private PreparedStatement updateStateStatement;

    /** Lazily (re-)created statement for updating the global alarm state of a PV */
    private PreparedStatement updateGlobalStatement;

    public AlarmRDB(final AlarmServer server, final String url,
            final String user, final String password,
            final String schema, final String root_name) throws Exception
    {
        this.server = server;
        rdb = RDBUtil.connect(url, user, password, true);
        sql = new SQL(rdb, schema);
        connection = rdb.getConnection();
        this.root_name = root_name;
        // Disable auto-reconnect: Slightly faster, and we just connected OK.
        rdb.setAutoReconnect(false);
        try
        {
            severity_mapping = new SeverityMapping(rdb, sql);
            message_mapping = new MessageMapping(rdb, sql);
        }
        finally
        {
            rdb.setAutoReconnect(true);
        }
    }

    /** Read alarm configuration
     *  @return Root element of the alarm tree hierarchy
     *  @throws Exception on error
     */
    public TreeItem readConfiguration() throws Exception
    {
        final Connection conn = rdb.getConnection();
        // Disabling the auto-reconnect is about 15% faster, and we don't
        // expect a timeout while we read the configuration.
        rdb.setAutoReconnect(false);

        final PreparedStatement statement =
            conn.prepareStatement(sql.sel_configuration_by_name);

        // Get root element
        final TreeItem root;
        try
        {
            statement.setString(1, root_name);
            final ResultSet result = statement.executeQuery();
            if (!result.next())
                throw new Exception("Unknown alarm tree root " + root_name);
            final int id = result.getInt(1);
            result.close();
            root = new TreeItem(null, root_name, id);
        }
        finally
        {
            statement.close();
        }

        // Fetch children
        final PreparedStatement sel_items_by_parent =
            conn.prepareStatement(sql.sel_items_by_parent);
        try
        {
            readChildren(root, sel_items_by_parent);
        }
        finally
        {
            sel_items_by_parent.close();
        }

        // In transactional mode (Connection.setAutoCommit(false)),
        // even SELECTs needed a commit() to end the transaction.
        // Otherwise the next 'SELECT' would get the same information.
        // Usually, there will be some alarm state updates, each committed,
        // to assert that a later readConfiguration() call will get the latest
        // config, but in the rare case that there were no alarm state changes
        // after reading the original configuration,
        // a newly added PV will not be found in the next readConfiguration() call
        // because we would still be in the previous transaction.
        if (! conn.getAutoCommit())
            conn.commit();

        // Re-enable auto-reconnect
        rdb.setAutoReconnect(true);

        root.check();

        return root;
    }

    /** Read alarm tree hierarchy
     *  @param parent Parent entry
     *  @param sel_items_by_parent Prepared statement for fetching child elements
     *  @throws Exception on error
     */
    private void readChildren(final TreeItem parent, final PreparedStatement sel_items_by_parent) throws Exception
    {
        final List<TreeItem> recurse_items = new ArrayList<TreeItem>();

        sel_items_by_parent.setInt(1, parent.getID());
        final ResultSet result = sel_items_by_parent.executeQuery();
        try
        {
            while (result.next())
            {
                final int id = result.getInt(1);
                if (result.wasNull())
                    throw new Exception("NULL component ID");
                final String name = result.getString(17);
                if (result.wasNull())
                    throw new Exception("NULL component Name");
                // Ignoring config. time from result.getTimestamp(2)

                // Check PV's ID. If null, this is a component, not PV
                final int pv_id = result.getInt(3);
                if (result.wasNull())
                {
                    final TreeItem child = new TreeItem(parent, name, id);
                    recurse_items.add(child);
                }
                else
                {   // Handle PV
                    if (id != pv_id)
                        throw new Exception("Internal RDB error: Item '" + name + "' as ID " + id + " but also PV ID " + pv_id);
                    // Easy results
                    String description = result.getString(4);
                    // Description should not be empty
                    if (result.wasNull() || description == null || description.length() <= 0)
                        description = name;
                    // Default to most features turned 'on'
                    boolean enabled = result.getBoolean(5);
                    if (result.wasNull())
                        enabled = true;
                    boolean annunciate = result.getBoolean(6);
                    if (result.wasNull())
                        annunciate = true;
                    boolean latch = result.getBoolean(7);
                    if (result.wasNull())
                        latch = true;
                    // 0/null/empty disables these features
                    final int min_alarm_delay = result.getInt(8);
                    final int count = result.getInt(9);
                    final String filter = result.getString(10);

                    // Decode current severity/status IDs, handling NULL as "Ok"
                    int severity_id = result.getInt(11);
                    final SeverityLevel current_severity = result.wasNull()
                        ? SeverityLevel.OK
                        : severity_mapping.getSeverityLevel(severity_id);

                    int status_id = result.getInt(12);
                    final String current_status = result.wasNull()
                        ? ""
                        : message_mapping.findMessageById(status_id);

                    // Alarm severity/status
                    severity_id = result.getInt(13);
                    final SeverityLevel severity = result.wasNull()
                        ? SeverityLevel.OK
                        : severity_mapping.getSeverityLevel(severity_id);

                    status_id = result.getInt(14);
                    final String status = result.wasNull()
                        ? ""
                        : message_mapping.findMessageById(status_id);

                    // Alarm value, time
                    final String value = result.getString(15);

                    final Timestamp time = result.getTimestamp(16);
                    final Instant timestamp = result.wasNull()
                        ? Instant.now()
                        : TimestampHelper.toEPICSTime(time);

                    final int global_delay = AlarmServerPreferences.getGlobalAlarmDelay();

                    new AlarmPV(server, parent, id, name, description,
                            enabled, latch, annunciate, min_alarm_delay, count, global_delay, filter,
                            current_severity, current_status, severity, status, value, timestamp);
                }
            }
        }
        finally
        {
            result.close();
        }

        // Recurse to children
        // Cannot do that inside the above while() because that would reuse
        // the statement of the current ResultSet
        for (TreeItem child : recurse_items)
            readChildren(child, sel_items_by_parent);
    }

    /** Read configuration for PV, update it from RDB
     *  @param pv AlarmPV to update
     *  @throws Exception on error
     */
    public void readConfigurationUpdate(final AlarmPV pv) throws Exception
    {
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.sel_pv_by_id);
        try
        {
            statement.setInt(1, pv.getID());
            final ResultSet result = statement.executeQuery();
            if (! result.next())
                throw new Exception("PV " + pv.getName() + " not found");
            final boolean enabled = result.getBoolean(2);
            final String filter = result.getString(7);
            pv.setDescription(result.getString(1));
            pv.getAlarmLogic().setAnnunciate(result.getBoolean(3));
            pv.getAlarmLogic().setLatching(result.getBoolean(4));
            pv.getAlarmLogic().setDelay(result.getInt(5));
            pv.getAlarmLogic().setCount(result.getInt(6));
            pv.setEnablement(enabled, filter);
        }
        finally
        {
            statement.close();
        }
    }

    /** Write updated PV state to RDB
     *  @param pv
     *  @param current_severity
     *  @param current_message
     *  @param severity
     *  @param message
     *  @param value
     *  @param timestamp
     *  @throws Exception on error
     */
    private void writeStateUpdate(final AlarmPV pv, final SeverityLevel current_severity,
            String current_message, final SeverityLevel severity, String message,
            final String value, final Instant timestamp) throws Exception
    {
        // Message should not be empty because Oracle treats empty strings like null
        if (message == null  ||  message.isEmpty())
            message = SeverityLevel.OK.getDisplayName();
        if (current_message == null  ||  current_message.isEmpty())
            current_message = SeverityLevel.OK.getDisplayName();

        // These are usually quick accesses to local caches,
        // but could fail when trying to add new values to RDB, so give detailed error
        final int current_severity_id;
        final int severity_id;
        final int current_message_id;
        final int message_id;
        try
        {
            current_severity_id = severity_mapping.getSeverityID(current_severity);
        }
        catch (Exception ex)
        {
            throw new Exception("Failed to map current severity " + current_severity + ": " + ex.getMessage(), ex);
        }
        try
        {
            severity_id = severity_mapping.getSeverityID(severity);
        }
        catch (Exception ex)
        {
            throw new Exception("Failed to map alarm severity " + severity + ": " + ex.getMessage(), ex);
        }
        try
        {
            current_message_id = message_mapping.findOrAddMessage(current_message);
        }
        catch (Exception ex)
        {
            throw new Exception("Failed to map current message " + current_message + ": " + ex.getMessage(), ex);
        }
        try
        {
            message_id = message_mapping.findOrAddMessage(message);
        }
        catch (Exception ex)
        {
            throw new Exception("Failed to map alarm message " + message + ": " + ex.getMessage(), ex);
        }

        updateStateStatement.setInt(1, current_severity_id);
        updateStateStatement.setInt(2, current_message_id);
        updateStateStatement.setInt(3, severity_id);
        updateStateStatement.setInt(4, message_id);
        //Truncate the value to avoid Truncation Exception thrown by some SQL Servers
        String newValue = value;
        if (newValue != null && newValue.length() > 100)
        {
            newValue = newValue.substring(0,99);
            Activator.getLogger().log(Level.WARNING,
                "Value truncated. Too many characters: " + pv.getName() + "; " + Date.from(timestamp) + " " + value);
        }
        updateStateStatement.setString(5, newValue);
        Timestamp sql_time = TimestampHelper.toSQLTime(timestamp);
        if (sql_time.getTime() == 0)
        {    // MySQL will throw Data Truncation exception on 0 time stamps
            sql_time = new Timestamp(new Date().getTime());
            Activator.getLogger().log(Level.INFO,
                    "State update for {0} corrects time stamp {1} to now",
                    new Object[] { pv.getPathName(), timestamp });
        }
        updateStateStatement.setTimestamp(6, sql_time);
        updateStateStatement.setInt(7, pv.getID());
        updateStateStatement.addBatch();
    }

    /** Persists all the updates into DB.
     *  @param updates the updates to persist
     *  @param batchSize maximum batch size
     *
     *  @throws Exception
     */
    public void persistAllStates(final Update[] updates, final int batchSize) throws Exception
    {
        final Connection actual_connection = rdb.getConnection();
        actual_connection.setAutoCommit(false);

        // New or changed connection?
        if (actual_connection != connection  ||  updateStateStatement == null)
        {
            connection = actual_connection;
            updateStateStatement = null;
            updateStateStatement = actual_connection.prepareStatement(sql.update_pv_state);
        }
        try
        {
            int count = 0;
            for (Update u : updates)
            {
                try
                {
                    writeStateUpdate(u.pv, u.currentSeverity, u.currentMessage, u.alarmSeverity,
                            u.alarmMessage, u.value, u.timestamp);
                    count++;
                }
                catch (Exception ex)
                {
                    //this is about 4-times faster than StringBuilder
                    String s = "Error updating state: current severity=" + u.currentSeverity +
                                "; current message=" + u.currentMessage + "; severity=" + u.alarmSeverity +
                                "; message=" + u.alarmMessage + "; value=" + u.value + "; timestamp=" + u.timestamp +
                                "; pv=" + u.pv.getName() + '(' + u.pv.getID() + "). Message skipped.";
                    Activator.getLogger().log(Level.SEVERE, s, ex);
                }
                if (count == batchSize)
                {    // Periodically submit as batch
                    updateStateStatement.executeBatch();
                    actual_connection.commit();
                    count = 0;
                }
            }
            // Submit remaining statements
            if (count > 0)
            {
                updateStateStatement.executeBatch();
                actual_connection.commit();
            }
        }
        catch (Exception e)
        {
            rollbackBatchUpdate(actual_connection,updateStateStatement);
            throw e;
        }
        finally
        {
            actual_connection.setAutoCommit(true);
        }
    }

    /** Persists all the global updates in batches of the given size.
     *
     *  @param updates the updates to persist
     *  @param batchSize maximum batch size
     *
     *  @throws Exception
     */
    public void persistGlobalUpdates(final Update[] updates, final int batchSize) throws Exception
    {
        final Connection actual_connection = rdb.getConnection();
        actual_connection.setAutoCommit(false);

        try
        {
            int count = 0;
            if (actual_connection != connection  ||  updateGlobalStatement == null)
            {   // (Re-)create statement on new connection
                connection = actual_connection;
                updateGlobalStatement = null;
                updateGlobalStatement = connection.prepareStatement(sql.update_global_state);
            }

            for (Update u : updates)
            {
                try
                {
                    updateGlobalStatement.setBoolean(1, u.currentSeverity.isActive());
                    updateGlobalStatement.setInt(2, u.pv.getID());
                    updateGlobalStatement.addBatch();
                    count++;
                }
                catch (Exception ex)
                {
                    //this is about 4-times faster than StringBuilder
                    String s = "Error updating global state: current severity=" + u.currentSeverity +
                                "; current message=" + u.currentMessage + "; severity=" + u.alarmSeverity +
                                "; message=" + u.alarmMessage + "; value=" + u.value + "; timestamp=" + u.timestamp +
                                "; pv=" + u.pv.getName() + '(' + u.pv.getID() + "). Message skipped.";
                    Activator.getLogger().log(Level.SEVERE, s, ex);
                }
                if (count == batchSize)
                {
                    updateGlobalStatement.executeBatch();
                    actual_connection.commit();
                    count = 0;
                }
            }
            // Submit remaining batch
            if (count > 0)
            {
                updateGlobalStatement.executeBatch();
                actual_connection.commit();
            }
        }
        catch (Exception e)
        {
            rollbackBatchUpdate(actual_connection, updateGlobalStatement);
            throw e;
        }
        finally
        {
            actual_connection.setAutoCommit(true);
        }
    }

    /** Write updated PV enablement to RDB
     *  @param pv Alarm PV
     *  @param enabled Enabled or not?
     *  @throws Exception on error
     */
    public void writeEnablementUpdate(final AlarmPV pv, final boolean enabled) throws Exception
    {
        final Connection actual_connection = rdb.getConnection();

        final PreparedStatement update_enablement_statement=
                actual_connection.prepareStatement(sql.update_pv_enablement);
        try
        {
            actual_connection.setAutoCommit(false);

            update_enablement_statement.setBoolean(1, enabled);
            update_enablement_statement.setInt(2, pv.getID());
            update_enablement_statement.execute();
            actual_connection.commit();
        }
        catch(Exception e)
        {
            actual_connection.rollback();
            throw e;
        }
        finally
        {
            update_enablement_statement.close();
            actual_connection.setAutoCommit(true);
        }
    }

    private void rollbackBatchUpdate(final Connection actualConnection, final PreparedStatement statement)
    {
        try
        {
            statement.clearBatch();
            actualConnection.rollback();
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.SEVERE, "State update rollback error.", ex);
        }
    }

    /** Must be called to release resources */
    public void close()
    {
        // Does not specifically close all prepared statements,
        // leaves that to overall rdb.close()
        rdb.close();
    }
}
