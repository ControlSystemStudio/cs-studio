/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.beast.AlarmTreeItem;
import org.csstudio.alarm.beast.AlarmTreePath;
import org.csstudio.alarm.beast.AlarmTreeRoot;
import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.WorkQueue;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmUpdateInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

/** Model for a 'global' alarm client.
 *  Initially reads alarms from RDB, then tracks changes via JMS.
 *
 *  TODO Check for latency from GUI clearing alarm to global alarm clearing up
 *  @author Kay Kasemir
 */
public class GlobalAlarmModel
{
    final private GlobalAlarmModelListener listener;

    /** Communicator that receives alarm updates from JMS */
    final private GlobalAlarmCommunicator communicator;

    /** Queue for received alarm updates when non-<code>null</code>.
     *  Set to <code>null</code> when updates can be executed right away.
     *
     *  Synchronize on <code>this</code> for access.
     */
    private WorkQueue update_queue = null;

    /** Currently active global alarms, i.e. configurations with partial
     *  sub-tree of global alarms.
     *
     *  Synchronize on <code>configurations</code> for access.
     */
    final private List<AlarmTreeRoot> configurations = new ArrayList<AlarmTreeRoot>();

    /** Initialize
     *  @param listener Listener
     */
    public GlobalAlarmModel(final GlobalAlarmModelListener listener)
    {
        this.listener = listener;
        communicator = new GlobalAlarmCommunicator(Preferences.getJMS_URL())
        {
            @Override
            void handleAlarmUpdate(final AlarmUpdateInfo info)
            {
                synchronized (GlobalAlarmModel.this)
                {
                    if (update_queue != null)
                    {   // Queue update to be executed once RDB info was read
                        update_queue.execute(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                GlobalAlarmModel.this.handleAlarmUpdate(info);
                            }
                        });
                        return;
                    }
                }
                // Execute update right away
                GlobalAlarmModel.this.handleAlarmUpdate(info);
            }
        };
    }

    /** @return Currently active global alarms */
    public AlarmTreeRoot[] getAlarms()
    {
        synchronized (configurations)
        {
            return configurations.toArray(new AlarmTreeRoot[configurations.size()]);
        }
    }

    /** Read 'global' alarms from RDB
     *  @param monitor Progress monitor
     */
    public void readConfiguration(final IProgressMonitor monitor)
    {
        monitor.beginTask(Messages.AlarmClientModel_ReadingConfiguration, IProgressMonitor.UNKNOWN);
        // Arrange for JMS updates to be queued
        synchronized (this)
        {
            update_queue = new WorkQueue();
        }
        // Wait for communicator to be connected
        communicator.start();
        int wait = 0;
        while (! communicator.isConnected())
        {
            monitor.subTask(NLS.bind(Messages.AlarmClientModel_WaitingForJMSFmt, ++wait));
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            if (monitor.isCanceled())
            {
                monitor.done();
                return;
            }
        }

        // TODO Read global alarms from RDB
        synchronized (configurations)
        {
            configurations.clear();
        }

        // Apply queued updates
        final WorkQueue queued_updates;
        synchronized (this)
        {
            queued_updates = update_queue;
            update_queue = null;
        }
        queued_updates.perform_queued_commands();

        // From now on, updates will be executed right away
        monitor.done();
        fireAlarmUpdate();
    }

    /** Update global alarms in response to JMS info (queued or 'live') */
    private void handleAlarmUpdate(final AlarmUpdateInfo info)
    {
        // Update currently active alarms
        if (info.getSeverity() == SeverityLevel.OK)
        {   // Remove currently active alarm
            if (! removeAlarm(info.getNameOrPath()))
                    return;
        }
        else
        {
            // Add/update alarm
            final GlobalAlarm alarm;
            synchronized (configurations)
            {
                alarm = GlobalAlarm.fromPath(configurations, info.getNameOrPath(),
                        info.getSeverity(), info.getMessage(), info.getTimestamp());
            }
            // Complete GUI detail in background
            final ReadInfoJob read_job = new ReadInfoJob(Preferences.getRDB_Url(),Preferences.getRDB_User(),
                    Preferences.getRDB_Password(), alarm, null);
            // Wait a little to give fireAlarmUpdate a head-start
            read_job.schedule(100);
        }
        fireAlarmUpdate();
    }

    /** Remove alarm because it cleared
     *  @param full_path Alarm path
     *  @return <code>true</code> when removed, <code>false</code> when not found
     */
    private boolean removeAlarm(final String full_path)
    {
        final String path[] = AlarmTreePath.splitPath(full_path);
        if (path.length <= 1)
            return false;
        synchronized (configurations)
        {
            // Locate alarm: Root....
            AlarmTreeItem item = null;
            for (AlarmTreeRoot root : configurations)
                if (root.getName().equals(path[0]))
                {
                    item = root;
                    break;
                }
            if (item == null)
                return false;
            // .. descend tree..
            for (int i=1; item != null &&  i<path.length; ++i)
                item = item.getClientChild(path[i]);
            // Found?
            if (item == null || !(item instanceof GlobalAlarm))
                return false;

            // Up to the root, delete all 'empty' nodes
            final AlarmTreeRoot root = item.getClientRoot();
            while (item != null  &&  item.getChildCount() <= 0)
            {
                final AlarmTreeItem tmp = item;
                item = item.getClientParent();
                tmp.detachFromParent();
            }
            // If root is now unused, delete it from configurations
            if (root.getChildCount() <= 0)
                configurations.remove(root);
        }
        return true;
    }

    /** Inform listener of changes */
    private void fireAlarmUpdate()
    {
        listener.globalAlarmsChanged(this);
    }
}
