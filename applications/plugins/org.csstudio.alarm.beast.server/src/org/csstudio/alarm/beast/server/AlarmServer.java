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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import org.csstudio.alarm.beast.AlarmTreePath;
import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.SQL;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.WorkQueue;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.logging.JMSLogMessage;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.TimeWarp;

/** Alarm Server
 *  
 *  Obtains configuration for all PVs from storage, then updates the PVs'
 *  status/severity from the control system.
 *  <p>
 *  Ignores the hierarchy which (some) of the clients may use to
 *  display the alarm state of PVs.
 *  
 *  @author Kay Kasemir, Xihui Chen
 */
@SuppressWarnings("nls")
public class AlarmServer
{
    /** Name of alarm tree root element */
    final String root_name = Preferences.getAlarmTreeRoot();

    /** Work queue in main thread of application */
    final private WorkQueue work_queue;

    /** Connection to storage for configuration/state */
    final private RDBUtil rdb;
    
    /** RDB SQL statements */
    final private SQL sql;

    /** RDB connection. Used to check if the RDB reconnected */
    private Connection connection;

    /** Map of severities and severity IDs in RDB */
    final private SeverityMapping severity_mapping;

    /** Map of message strings and IDs in RDB */
    final private MessageMapping message_mapping;

    /** Talker which can annunciate messages */
    final private Talker talker;

    /** Messenger to communicate with clients */
    final private ServerCommunicator messenger;

    /** All the PVs in the model, sorted by name
     *  <B>NOTE: Access to PV list and map must synchronize on 'this'</B>
     */
    private AlarmPV pv_list[] = new AlarmPV[0];
    
    /** All the PVs in the model, mapping PV name (not path name!) to AlarmPV
     *  <B>NOTE: Access to PV list and map must synchronize on 'this'</B>
     *  
     *  Use ConcurrentHashMap ?
     */
    private HashMap<String, AlarmPV> pv_map = new HashMap<String, AlarmPV>();

    /** Lazily (re-)created statement for updating the alarm state of a PV */
    private PreparedStatement updateStateStatement;

    /** Initialize
     *  @param talker Talker that'll be used to annunciate
     *  @param work_queue Work queue of the 'main' thread
     *  @throws Exception on error
     */
    public AlarmServer(final Talker talker, final WorkQueue work_queue) throws Exception
    {
        this.work_queue = work_queue;
        rdb = RDBUtil.connect(Preferences.getRDB_Url(), 
        		Preferences.getRDB_User(), Preferences.getRDB_Password(), true);
        sql = new SQL(rdb);
        connection = rdb.getConnection();
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
        this.talker = talker;
        this.messenger = new ServerCommunicator(this, work_queue);
        readConfiguration();
    }
    
    /** @return Name of configuration root element */
    public String getRootName()
    {
        return root_name;
    }
    
    /** Set maintenance mode.
     *  @param maintenance_mode
     *  @see AlarmLogic#getMaintenanceMode()
     */
    public void setMaintenanceMode(final boolean maintenance_mode)
    {
        // Any change?
        if (maintenance_mode == AlarmLogic.getMaintenanceMode())
            return;
        // Configure alarm logic
        AlarmLogic.setMaintenanceMode(maintenance_mode);
        // Send update to clients
        messenger.sendIdleMessage();
        // Entering maintenance mode: Ack' all INVALID alarms
        if (maintenance_mode)
        {
            synchronized (this)
            {
                for (AlarmLogic pv : pv_list)
                    if (pv.getAlarmState().getSeverity() == SeverityLevel.INVALID)
                        pv.acknowledge(true);
            }
        }
    }

    /** Dump all PVs to stdout */
    public void dump()
    {
        System.out.println("== Alarm Server PV Snapshot ==");
        synchronized (this)
        {
            for (AlarmLogic pv : pv_list)
                System.out.println(pv);
        }

        // Log memory usage
        final double MB = 1024.0*1024.0;
        final double free = Runtime.getRuntime().freeMemory() / MB;
        final double total = Runtime.getRuntime().totalMemory() / MB;
        final double max = Runtime.getRuntime().maxMemory() / MB;
        
        final DateFormat format = new SimpleDateFormat(JMSLogMessage.DATE_FORMAT);
        System.out.format("%s == Alarm Server Memory: Max %.2f MB, Free %.2f MB (%.1f %%), total %.2f MB (%.1f %%)\n",
                format.format(new Date()), max, free, 100.0*free/max, total, 100.0*total/max);
    }

    /** Release all resources */
    public void close()
    {
        talker.close();
        messenger.close();
        rdb.close();
    }
    
    /** Start all the PVs, connect to control system. */
    public void start()
    {
        final long delay = Preferences.getPVStartDelay();
        // Must not sync while calling PV, because Channel Access updates
        // might arrive while we're trying to start/stop channels,
        // and those updates will try to lock the Alarm Server,
        // which then results in a deadlock:
        // a) We lock AlarmServer, then try to call CA,
        //     which takes internal JNI locks
        // b) CA takes internal locks, calls PV update callback, which
        //    then tries to lock the AlarmServer when locating the PV by name
        final AlarmPV pvs[];
        synchronized (this)
        {
            pvs = pv_list.clone();
        }
        for (AlarmPV pv : pvs)
        {
            try
            {
                pv.start();
                if (delay > 0)
                    Thread.sleep(delay);
            }
            catch (Exception ex)
            {
                CentralLogger.getInstance().getLogger(this)
                    .error("Error starting PV " + pv.getName(), ex);
            }
        }
    }

    /** Stop all the PVs, disconnect from control system. */
    public void stop()
    {
        // See deadlock comment in start()
        final AlarmPV pvs[];
        synchronized (this)
        {
            pvs = pv_list.clone();
        }
        for (AlarmPV pv : pvs)
            pv.stop();
    }
    
    /** @return Talker */
    public Talker getTalker()
    {
        return talker;
    }

    /** Read the initial alarm configuration
     *  @throws Exception on error
     */
    private void readConfiguration() throws Exception
    {
        final ArrayList<AlarmPV> tmp_pv_array = new ArrayList<AlarmPV>();
        final BenchmarkTimer timer = new BenchmarkTimer();
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.sel_item_by_name);
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
            final Object parent = result.getObject(2);
            if (parent != null)
                throw new Exception("Root element " + root_name + " has parent");
            result.close();
            getAlarmTreeChildren(id, tmp_pv_array);
        }
        finally
        {
            statement.close();
            rdb.setAutoReconnect(true);
        }
        synchronized (this)
        {
            // Turn into plain array
            pv_list = tmp_pv_array.toArray(new AlarmPV[tmp_pv_array.size()]);
            // Sort PVs by name
            Arrays.sort(pv_list, new Comparator<AlarmPV>()
            {
                public int compare(final AlarmPV pv1, final AlarmPV pv2)
                {
                    return pv1.getName().compareTo(pv2.getName());
                }
            });
            // Create hash
            pv_map = new HashMap<String, AlarmPV>();
            for (AlarmPV pv : pv_list)
                pv_map.put(pv.getName(), pv);
        }
        timer.stop();
        // LDAP results: Read 12614 PVs in 2.69 seconds, 4689.0 PVs/sec
        System.out.format("Read %d PVs in %.2f seconds, %.1f PVs/sec\n",
                pv_list.length, timer.getSeconds(), pv_list.length/timer.getSeconds());
    }

    /** Read configuration for child elements
     *  @param parent Parent node ID.
     *  @param tmp_pv_array Array into which to read the PVs
     *  @throws Exception on error
     */
    private void getAlarmTreeChildren(final int parent, final ArrayList<AlarmPV> tmp_pv_array) throws Exception
    {
        // When trying to re-use this statement note the recursive access!
        final PreparedStatement sel_items_by_parent =
            rdb.getConnection().prepareStatement(sql.sel_items_by_parent);
        try
        {
            sel_items_by_parent.setInt(1, parent);
            final ResultSet result = sel_items_by_parent.executeQuery();
            while (result.next())
            {
                // Recurse to children of child entry
                final int id = result.getInt(1);
                getAlarmTreeChildren(id, tmp_pv_array);
            }
            result.close();
        }
        finally
        {
            sel_items_by_parent.close();
        }
        getAlarmTreePVs(parent, tmp_pv_array);
    }
    
    /** Read configuration of PVs
     *  @param parent Parent node ID.
     *  @param tmp_pv_array Array into which to read the PVs
     *  @throws Exception on error
     */
    private void getAlarmTreePVs(final int parent, final ArrayList<AlarmPV> tmp_pv_array) throws Exception
    {
        final PreparedStatement sel_pv_statement =
            rdb.getConnection().prepareStatement(sql.sel_pvs_by_parent);   
        try
        {
            sel_pv_statement.setInt(1, parent);
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
                    
                final AlarmPV pv = new AlarmPV(this, id, name, description,
                        enabled, latch, annunciate, min_alarm_delay, count, filter,
                        current_severity, current_status, severity, status, value, timestamp);
                tmp_pv_array.add(pv);
            }
            result.close();
        }
        finally
        {
            sel_pv_statement.close();
        }
    }

    /** Read updated configuration for PV from RDB
     *  @param path_name PV name
     */
    void updateConfig(final String path_name) throws Exception
    {
        AlarmPV pv = null;
        if (path_name != null)
        {
            final String[] path = AlarmTreePath.splitPath(path_name);
            // Is this a PV under a different alarm tree root?
            if (! root_name.equals(path[0]))
                return;
            // Locate PV, assuming last path element is PV
            pv = findPV(path[path.length-1]);
        }
        if (pv == null)
        {   // Unknown PV, so this must be a new PV. Read whole config again
            stop();
            readConfiguration();
            start();
            return;
        }
        // Known PV
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.sel_pv_by_id);
        try
        {
            statement.setInt(1, pv.getID());
            final ResultSet result = statement.executeQuery();
            if (! result.next())
                throw new Exception("PV " + path_name + " not found");
            pv.stop();
            final boolean enabled = result.getBoolean(2);
            final String filter = result.getString(7);
            pv.setDescription(result.getString(1));
            pv.setAnnunciate(result.getBoolean(3));
            pv.setLatching(result.getBoolean(4));
            pv.setDelay(result.getInt(5));
            pv.setCount(result.getInt(6));
            pv.setEnablement(enabled, filter);
            pv.start();
        }
        finally
        {
            statement.close();
        }
    }

    /** (Un-)acknowledge alarm.
     *  @param pv_name PV to acknowledge
     *  @param acknowledge Acknowledge, or un-acknowledge?
     */
    public void acknowledge(final String pv_name, final boolean acknowledge)
    {
        final AlarmLogic pv = findPV(pv_name);
        if (pv != null)
            pv.acknowledge(acknowledge);
    }

    /** Locate alarm PV by name
     *  @param pv_name PV name
     *  @return AlarmPV or <code>null</code> when not found
     */
    private AlarmPV findPV(final String pv_name)
    {
        synchronized (this)
        {
            return pv_map.get(pv_name);
        }
    }

    /** Update JMS clients and RDB
     *  @param pv Alarm PV
     *  @param current_severity Current channel severity
     *  @param current_message Current message
     *  @param severity Alarm severity (highest, latched)
     *  @param message Alarm message
     *  @param value Value that triggered
     *  @param timestamp Time of last alarm update
     */
    public void sendStateUpdate(final AlarmPV pv,
            final SeverityLevel current_severity,
            final String current_message,
            final SeverityLevel severity,
            final String message,
            final String value, final ITimestamp timestamp)
    {
    	messenger.sendStateUpdate(pv, current_severity, current_message,
    	        severity, message, value, timestamp);
        // Move the persistence of states into separate queue & thread
        // so that it won't delay the alarm server from updating
        work_queue.add(new Runnable()
        {
            public void run()
            {
                try
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
                catch (Exception ex)
                {
                    CentralLogger.getInstance().getLogger(this).error("Exception during alarm state update", ex);
                }
            }
        });
    }

    /** Update JMS clients and RDB about 'enabled' state of PV
     *  @param pv Alarm PV
     *  @param enabled Enabled or not?
     */
	public void sendEnablementUpdate(final AlarmPV pv, final boolean enabled)
	{
		messenger.sendEnablementUpdate(pv, enabled);
        // Handle in separate queue & thread
        work_queue.add(new Runnable()
        {
            public void run()
            {
                try
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
                catch (Exception ex)
                {
                    CentralLogger.getInstance().getLogger(this).error("Exception", ex);
                }
            }
        });
	}
}
