/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.alarm.beast.AlarmTreePath;
import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.ReplacableRunnable;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.TreeItem;
import org.csstudio.alarm.beast.WorkQueue;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.logging.JMSLogMessage;
import org.eclipse.osgi.util.NLS;

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
    final String root_name;

    /** Work queue in main thread of application */
    final private WorkQueue work_queue;

    /** RDB for configuration/state */
    final private AlarmRDB rdb;

    /** Messenger to communicate with clients */
    final private ServerCommunicator messenger;

    /** {@link NagTimer} or <code>null</code> if not used */
    private volatile NagTimer nag_timer;

    /** Hierarchical alarm configuration
     *  <p><b>NOTE: Access to tree, PV list and map must synchronize on 'this'</b>
     */
	private TreeItem alarm_tree;

    /** All the PVs in the alarm_tree, sorted by name
     *  <p><b>NOTE: Access to tree, PV list and map must synchronize on 'this'</b>
     */
    private AlarmPV pv_list[] = new AlarmPV[0];

    /** All the PVs in the model, mapping PV name (not path name!) to AlarmPV
     *  <p><b>NOTE: Access to tree, PV list and map must synchronize on 'this'</b>
     */
    private Map<String, AlarmPV> pv_map = new HashMap<String, AlarmPV>();

    /** Indicator for communication errors */
    private volatile boolean had_RDB_error = false;


    /** Initialize
     *  @param talker Talker that'll be used to annunciate
     *  @param work_queue Work queue of the 'main' thread
     *  @param root_name
     *  @throws Exception on error
     */
    public AlarmServer(final WorkQueue work_queue, final String root_name) throws Exception
    {
        this.root_name = root_name;
        this.work_queue = work_queue;
        rdb = new AlarmRDB(this, Preferences.getRDB_Url(),
        		Preferences.getRDB_User(),
        		Preferences.getRDB_Password(),
        		Preferences.getRDB_Schema(),
        		root_name);
        messenger = new ServerCommunicator(this, work_queue, root_name);
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
                for (AlarmPV pv : pv_list)
                {
                	final AlarmLogic logic = pv.getAlarmLogic();
                    if (logic.getAlarmState().getSeverity() == SeverityLevel.INVALID)
                    	logic.acknowledge(true);
                }
            }
        }
    }

    /** Dump all PVs to stdout */
    public void dump()
    {
        System.out.println("== Alarm Server PV Snapshot ==");
        synchronized (this)
        {
        	alarm_tree.dump(System.out);
        }

        System.out.println("Work queue size: " + work_queue.size());

        // Log memory usage in MB
        final double free = Runtime.getRuntime().freeMemory() / (1024.0*1024.0);
        final double total = Runtime.getRuntime().totalMemory() / (1024.0*1024.0);
        final double max = Runtime.getRuntime().maxMemory() / (1024.0*1024.0);

        final DateFormat format = new SimpleDateFormat(JMSLogMessage.DATE_FORMAT);
        System.out.format("%s == Alarm Server Memory: Max %.2f MB, Free %.2f MB (%.1f %%), total %.2f MB (%.1f %%)\n",
                format.format(new Date()), max, free, 100.0*free/max, total, 100.0*total/max);
    }

    /** Release all resources */
    public void close()
    {
        messenger.stop();
        rdb.close();
    }

    /** Start all the PVs, connect to JMS */
    public void start()
    {
        messenger.start();
        messenger.sendAnnunciation(Messages.StartupMessage);
        startPVs();

        // Conditionally enable nagging
        double nag_period;
        try
        {
            nag_period = AlarmServerPreferences.getNagPeriod();
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.WARNING,
                    "Invalid '" + AlarmServerPreferences.NAG_PERIOD + "', repeated annunciations disabled", ex);
            nag_period = 0.0;
        }
        if (nag_period > 0)
        {
            nag_timer = new NagTimer(Math.round(nag_period * 1000), new NagTimerHandler()
            {
                @Override
                public int getActiveAlarmCount()
                {
                    int active = 0;
                    // Sync on access to pv_list
                    synchronized (AlarmServer.this)
                    {
                        for (AlarmPV pv : pv_list)
                            if (pv.getAlarmLogic().getAlarmState().getSeverity().isActive())
                                ++active;
                    }
                    return active;
                }

                @Override
                public void nagAboutActiveAlarms(final int active)
                {
                    final String message;
                    if (active == 1)
                        message = "There is 1 active alarm";
                    else
                        message = NLS.bind("There are {0} active alarms", active);
                    messenger.sendAnnunciation(message);
                }
            });
            nag_timer.start();
        }
    }

    /** Start PVs */
    private void startPVs()
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
                Activator.getLogger().log(Level.SEVERE,
                    "Error starting PV " + pv.getName(), ex);
            }
        }
    }

    /** Stop all the PVs, disconnect from JMS */
    public void stop()
    {
        if (nag_timer != null)
        {
            nag_timer.cancel();
            nag_timer = null;
        }
        messenger.sendAnnunciation("Alarm server exiting");
        stopPVs();
        messenger.stop();
    }

    /** Stop PVs */
    private void stopPVs()
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

    /** Read the initial alarm configuration
     *  @throws Exception on error
     */
    private void readConfiguration() throws Exception
    {
    	// Read alarm hierarchy
        final BenchmarkTimer timer = new BenchmarkTimer();
        final int pv_count;
        synchronized (this)
        {
        	alarm_tree = rdb.readConfiguration();

        	// Determine PVs
            final ArrayList<AlarmPV> tmp_pv_array = new ArrayList<AlarmPV>();
            findPVs(alarm_tree, tmp_pv_array);
            // Turn into plain array
            pv_list = tmp_pv_array.toArray(new AlarmPV[tmp_pv_array.size()]);
            tmp_pv_array.clear();
            // Sort PVs by name
            Arrays.sort(pv_list, new Comparator<AlarmPV>()
            {
                @Override
                public int compare(final AlarmPV pv1, final AlarmPV pv2)
                {
                    return pv1.getName().compareTo(pv2.getName());
                }
            });
            // Create hash
            pv_map = new HashMap<String, AlarmPV>();
            for (AlarmPV pv : pv_list)
                pv_map.put(pv.getName(), pv);
            pv_count = pv_list.length;
        }
        timer.stop();
        // LDAP results: Read 12614 PVs in 2.69 seconds, 4689.0 PVs/sec
        System.out.format("Read %d PVs in %.2f seconds: %.1f PVs/sec\n",
        		pv_count, timer.getSeconds(), pv_count/timer.getSeconds());
    }

    /** Recursively locate AlarmPVs in alarm hierarchy
     *  @param node Start node
     *  @param pvs Array to which located AlarmPVs are added
     */
    private void findPVs(final TreeItem node, final List<AlarmPV> pvs)
    {
    	if (node instanceof AlarmPV)
    	{
    		pvs.add((AlarmPV) node);
    		return;
    	}
    	for (int i=0; i<node.getChildCount(); ++i)
    		findPVs(node.getChild(i), pvs);
    }

    /** Reset the {@link NagTimer} - if we're using one
     *
     *  <p>To be called in response to any user action
     *  (ack, config) or when performing other annunciation,
     *  so that the nag will only happen if there are active
     *  alarms while nobody does anything about them.
     */
    private void resetNagTimer()
    {
        final NagTimer safe_copy = nag_timer;
        if (safe_copy != null)
            safe_copy.reset();
    }

    /** Read updated configuration for PV from RDB
     *  @param path_name PV name or <code>null</code> to reload complete configuration
     */
    void updateConfig(final String path_name) throws Exception
    {
        resetNagTimer();
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
            stopPVs();
            readConfiguration();
            startPVs();
            return;
        }
        // Known PV
        pv.stop();
        rdb.readConfigurationUpdate(pv);
        pv.start();
    }

    /** (Un-)acknowledge alarm.
     *  @param pv_name PV to acknowledge
     *  @param acknowledge Acknowledge, or un-acknowledge?
     */
    public void acknowledge(final String pv_name, final boolean acknowledge)
    {
        resetNagTimer();
        final AlarmPV pv = findPV(pv_name);
        if (pv != null)
            pv.getAlarmLogic().acknowledge(acknowledge);
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
        // so that it won't delay the alarm server from updating.

    	// ReplacableRunnable:
    	// If there is already an update request for this PV on the queue,
    	// replace it with the new one because it's out of date and no
    	// longer needs to be writting to the RDB anyway.
        work_queue.executeReplacable(new ReplacableRunnable<AlarmPV>(pv)
        {
            @Override
            public void run()
            {
                try
                {
                	rdb.writeStateUpdate(pv, current_severity, current_message,
                			severity, message, value, timestamp);
                    recoverFromRDBErrors();
                }
                catch (Exception ex)
                {
                    // Remember that there was an error
                    had_RDB_error = true;
                    Activator.getLogger().log(Level.SEVERE, "Exception during alarm state update", ex);
                }
            }
        });
    }

    /** Update 'global' JMS clients and RDB
     *  @param pv Alarm PV
     *  @param severity Alarm severity (highest, latched)
     *  @param message Alarm message
     *  @param value Value that triggered
     *  @param timestamp Time of last alarm update
     */
    public void sendGlobalUpdate(final AlarmPV pv,
            final SeverityLevel severity,
            final String message,
            final String value, final ITimestamp timestamp)
    {
        // Send to JMS
        messenger.sendGlobalUpdate(pv, severity, message, value, timestamp);
        // Persist global alarm state change in separate queue & thread
        // so that it won't delay the alarm server from updating
        work_queue.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    rdb.writeGlobalUpdate(pv, severity.isActive());
                    recoverFromRDBErrors();
                }
                catch (Exception ex)
                {
                    // Remember that there was an error
                    had_RDB_error = true;
                    Activator.getLogger().log(Level.SEVERE, "Exception during global alarm update", ex);
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
        work_queue.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                	rdb.writeEnablementUpdate(pv, enabled);
                    recoverFromRDBErrors();
                }
                catch (Exception ex)
                {
                    // Remember that there was an error
                    had_RDB_error = true;
                    Activator.getLogger().log(Level.SEVERE, "Exception during enablement update", ex);
                }
            }
        });
	}

    /** Perform annunciation
     *  @param level Alarm severity
     *  @param message Text message to send to annunciator
     */
    public void sendAnnunciation(final SeverityLevel level, final String message)
    {
        resetNagTimer();
        messenger.sendAnnunciation(level, message);
    }

    /** If this is the first successful RDB update after errors,
     *  tell everybody to re-load the configuration because otherwise
     *  they get out of sync.
     *  @throws Exception on error
     */
    protected void recoverFromRDBErrors() throws Exception
    {
        if (! had_RDB_error)
            return;

        // We should be on the work queue thread
        work_queue.assertOnThread();
        Activator.getLogger().info("RDB connection recovered, re-loading configuration");
        updateConfig(null);

        // If that worked out, reset error and inform clients
        had_RDB_error = false;
        messenger.sendReloadMessage();
    }
}
