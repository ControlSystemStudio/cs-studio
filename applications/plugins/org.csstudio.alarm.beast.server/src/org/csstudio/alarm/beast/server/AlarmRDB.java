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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.alarm.beast.SQL;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.TreeItem;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** Alarm RDB Handler
 *  @author Kay Kasemir
 *  @author Lana Abadie - Disable autocommit as needed.
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
                    final ITimestamp timestamp = result.wasNull()
                        ? TimestampFactory.now()
                        : TimestampFactory.fromSQLTimestamp(time);

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
    public void writeStateUpdate(final AlarmPV pv, final SeverityLevel current_severity,
            String current_message, final SeverityLevel severity, String message,
            final String value, final ITimestamp timestamp) throws Exception
    {
        // Message should not be empty because Oracle treats empty strings like null
        if (message == null  ||  message.isEmpty())
            message = SeverityLevel.OK.getDisplayName();
        if (current_message == null  ||  current_message.isEmpty())
            current_message = SeverityLevel.OK.getDisplayName();

        // According to JProfiler, this is the part of the code
        // that uses most of the CPU:
        // Compared to receiving updates from PVs and sending them
        // to JMS clients, the (Oracle) RDB update dominates
        // the combined time spent in CPU usage and network I/O.

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

        // The isConnected() check in here is expensive, but what's
        // the alternative if we want convenient auto-reconnect?
        final Connection actual_connection = rdb.getConnection();
        actual_connection.setAutoCommit(false);
        try
        {
            if (actual_connection != connection  ||  updateStateStatement == null)
            {   // (Re-)create statement on new connection
                connection = actual_connection;
                updateStateStatement = null;
                updateStateStatement = connection.prepareStatement(sql.update_pv_state);
            }
            // Bulk of the time is spent in execute() & commit
            updateStateStatement.setInt(1, current_severity_id);
            updateStateStatement.setInt(2, current_message_id);
            updateStateStatement.setInt(3, severity_id);
            updateStateStatement.setInt(4, message_id);
            updateStateStatement.setString(5, value);
            Timestamp sql_time = timestamp.toSQLTimestamp();
            if (sql_time.getTime() == 0)
            {    // MySQL will throw Data Truncation exception on 0 time stamps
                sql_time = new Timestamp(new Date().getTime());
                Activator.getLogger().log(Level.INFO,
                        "State update for {0} corrects time stamp {1} to now",
                        new Object[] { pv.getPathName(), timestamp });
            }
            updateStateStatement.setTimestamp(6, sql_time);
            updateStateStatement.setInt(7, pv.getID());
            updateStateStatement.execute();
            actual_connection.commit();
        }
        catch(Exception e)
        {
        	actual_connection.rollback();
            throw e;
        }
        finally
        {
            actual_connection.setAutoCommit(true);
        }
    }

    /** Update 'global' alarm indicator in RDB
     *  @param pv
     *  @param active Is there an active 'global' alarm on the PV?
     *  @throws Exception on error
     */
    public void writeGlobalUpdate(final AlarmPV pv, final boolean active) throws Exception
    {
        // The isConnected() check in here is expensive, but what's
        // the alternative if we want convenient auto-reconnect?
        final Connection actual_connection = rdb.getConnection();
        actual_connection.setAutoCommit(false);
        try
        {
            if (actual_connection != connection  ||  updateGlobalStatement == null)
            {   // (Re-)create statement on new connection
                connection = actual_connection;
                updateGlobalStatement = null;
                updateGlobalStatement = connection.prepareStatement(sql.update_global_state);
            }
            updateGlobalStatement.setBoolean(1, active);
            updateGlobalStatement.setInt(2, pv.getID());
            updateGlobalStatement.execute();
            actual_connection.commit();
        }
        catch(Exception e)
        {
        	actual_connection.rollback();
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

    /** Must be called to release resources */
    public void close()
    {
        // Does not specifically close all prepared statements,
        // leaves that to overall rdb.close()
        rdb.close();
    }
}
