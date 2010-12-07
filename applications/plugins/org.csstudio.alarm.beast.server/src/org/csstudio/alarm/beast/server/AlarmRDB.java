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
import java.util.List;

import org.csstudio.alarm.beast.SQL;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.TimeWarp;

/** Alarm RDB Handler
 *  @author Kay Kasemir
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

	public AlarmRDB(final AlarmServer server, final String url,
			final String user, final String password, final String root_name) throws Exception
    {
		this.server = server;
		rdb = RDBUtil.connect(url, user, password, true);
		sql = new SQL(rdb);
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
    public AlarmHierarchy readConfiguration() throws Exception
    {
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.sel_configuration_by_name);
        // Disabling the auto-reconnect is about 15% faster, and we don't
        // expect a timeout while we read the configuration.
        rdb.setAutoReconnect(false);
        try
        {
            statement.setString(1, root_name);
            final ResultSet result = statement.executeQuery();
            if (!result.next())
                throw new Exception("Unknown alarm tree root " + root_name);
            final int id = result.getInt(1);
            result.close();
            final AlarmHierarchy root = new AlarmHierarchy(null, root_name, id);
            root.setChildren(getAlarmTreeChildren(root));
            root.check();
            return root;
        }
        finally
        {
            statement.close();
            rdb.setAutoReconnect(true);
        }
    }

    /** Read alarm tree hierarchy, set all child elements and their child elements.
     *  @param parent Parent entry
     *  @throws Exception on error
     */
	private AlarmHierarchy[] getAlarmTreeChildren(final AlarmHierarchy parent) throws Exception
    {
		// Get PVs under this parent
		final List<AlarmHierarchy> children = getAlarmTreePVs(parent);

		// Fetch non-PV children
        final PreparedStatement sel_items_by_parent =
            rdb.getConnection().prepareStatement(sql.sel_items_by_parent);
        try
        {
            sel_items_by_parent.setInt(1, parent.getID());
            final ResultSet result = sel_items_by_parent.executeQuery();
            while (result.next())
            {
                final int child_id = result.getInt(1);
                final String child_name = result.getString(17);
                // Do not recurse if this child has already been added as a PV
                boolean is_pv = false;
                for (AlarmHierarchy child : children)
                {
	                if (child.getID() == child_id)
	                {
	                	is_pv = true;
	                	break;
	                }
                }
                if (is_pv)
                	continue;
                final AlarmHierarchy child = new AlarmHierarchy(parent, child_name, child_id);
				children.add(child);
				child.setChildren(getAlarmTreeChildren(child));
            }
            result.close();
        }
        finally
        {
            sel_items_by_parent.close();
        }

        // Recurse to children
        final AlarmHierarchy[] child_array =
        	children.toArray(new AlarmHierarchy[children.size()]);
        return child_array;
    }

    /** Read PVs below a parent
     *  @param parent Parent node
     *  @throws Exception on error
     *  @return PVs
     */
    private List<AlarmHierarchy> getAlarmTreePVs(final AlarmHierarchy parent) throws Exception
    {
		final List<AlarmHierarchy> pvs = new ArrayList<AlarmHierarchy>();
		// When trying to re-use this statement note the recursive access!
		final PreparedStatement sel_pv_statement =
            rdb.getConnection().prepareStatement(sql.sel_pvs_by_parent);
        try
        {
            sel_pv_statement.setInt(1, parent.getID());
            final ResultSet result = sel_pv_statement.executeQuery();
            while (result.next())
            {   // Easy results
                final int id = result.getInt(1);
                if (result.wasNull())
                    throw new Exception("NULL PV ID");
                final String name = result.getString(2);
                if (result.wasNull())
                    throw new Exception("NULL PV Name");
                String description = result.getString(3);
                // Description should not be empty
                if (result.wasNull() || description == null || description.length() <= 0)
                    description = name;
                // Default to most features turned 'on'
                boolean enabled = result.getBoolean(4);
                if (result.wasNull())
                    enabled = true;
                boolean annunciate = result.getBoolean(5);
                if (result.wasNull())
                    annunciate = true;
                boolean latch = result.getBoolean(6);
                if (result.wasNull())
                    latch = true;
                // 0/null/empty disables these features
                final int min_alarm_delay = result.getInt(7);
                final int count = result.getInt(8);
                final String filter = result.getString(9);

                // Decode current severity/status IDs, handling NULL as "Ok"
                int severity_id = result.getInt(10);
                final SeverityLevel current_severity = result.wasNull()
                    ? SeverityLevel.OK
                    : severity_mapping.getSeverityLevel(severity_id);

                int status_id = result.getInt(11);
                final String current_status = result.wasNull()
                    ? ""
                    : message_mapping.findMessageById(status_id);

                // Alarm severity/status
                severity_id = result.getInt(12);
                final SeverityLevel severity = result.wasNull()
                    ? SeverityLevel.OK
                    : severity_mapping.getSeverityLevel(severity_id);

                status_id = result.getInt(13);
                final String status = result.wasNull()
                    ? ""
                    : message_mapping.findMessageById(status_id);

                // Alarm value, time
                final String value = result.getString(14);

                final Timestamp time = result.getTimestamp(15);
                final ITimestamp timestamp = result.wasNull()
                    ? TimestampFactory.now()
                    : TimeWarp.getCSSTimestamp(time);

                // Ignoring config. time from result.getTimestamp(16)

                // TODO Get global_delay from
                final int global_delay = AlarmServerPreferences.getGlobalAlarmDelay();

                final AlarmPV pv = new AlarmPV(server, parent, id, name, description,
                        enabled, latch, annunciate, min_alarm_delay, count, global_delay, filter,
                        current_severity, current_status, severity, status, value, timestamp);
                pvs.add(pv);
            }
            result.close();
        }
        finally
        {
            sel_pv_statement.close();
        }
        return pvs;
    }


    /** Read configuration for PV, update it from RDB
     *  @param pv AlarmPV to update
     * 	@throws Exception on error
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
            final String current_message, final SeverityLevel severity, final String message,
            final String value, final ITimestamp timestamp) throws Exception
    {
        // According to JProfiler, this is the part of the code
        // that uses most of the CPU:
        // Compared to receiving updates from PVs and sending them
        // to JMS clients, the (Oracle) RDB update dominates
        // the combined time spent in CPU usage and network I/O.

        // These are usually quick accesses to local caches
        final int current_severity_id = severity_mapping.getSeverityID(current_severity);
        final int severity_id = severity_mapping.getSeverityID(severity);
        final int current_message_id = message_mapping.findOrAddMessage(current_message);
        final int message_id = message_mapping.findOrAddMessage(message);

        // The isConnected() check in here is expensive, but what's
        // the alternative if we want convenient auto-reconnect?
        final Connection actual_connection = rdb.getConnection();
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
        updateStateStatement.setTimestamp(6, TimeWarp.getSQLTimestamp(timestamp));
        updateStateStatement.setInt(7, pv.getID());
        updateStateStatement.execute();
        connection.commit();
    }

	/** Write updated PV enablement to RDB
     *  @param pv Alarm PV
     *  @param enabled Enabled or not?
	 *  @throws Exception on error
     */
	public void writeEnablementUpdate(final AlarmPV pv, final boolean enabled) throws Exception
    {
        final Connection connection = rdb.getConnection();
        final PreparedStatement update_enablement_statement =
            connection.prepareStatement(sql.update_pv_enablement);
        try
        {
            update_enablement_statement.setBoolean(1, enabled);
            update_enablement_statement.setInt(2, pv.getID());
            update_enablement_statement.execute();
            connection.commit();
        }
        finally
        {
            update_enablement_statement.close();
        }
    }

	/** Must be called to release resources */
    public void close()
    {
    	rdb.close();
    }
}
