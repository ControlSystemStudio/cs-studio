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
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.WorkQueue;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmUpdateInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

/** Model for a 'global' alarm client.
 *  Initially reads alarms from RDB, then tracks changes via JMS.
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

    // TODO Track list of configurations which then contain the active alarms,
    //      not just list of alarms
    /** Currently active global alarms.
     *
     *  Synchronize on <code>alarms</code> for access.
     */
    final private List<GlobalAlarm> alarms = new ArrayList<GlobalAlarm>();

    /** Initialize
     *  @param jms_url JMS URL
     */
    public GlobalAlarmModel(final String jms_url,
            final GlobalAlarmModelListener listener)
    {
        this.listener = listener;
        communicator = new GlobalAlarmCommunicator(jms_url)
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
    public GlobalAlarm[] getAlarms()
    {
        synchronized (alarms)
        {
            return alarms.toArray(new GlobalAlarm[alarms.size()]);
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
        synchronized (alarms)
        {
            alarms.clear();
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
            // TODO check for existing alarm
            // Add alarm
            createNewAlarm(info);
        }
        fireAlarmUpdate();
    }

    /** Remove alarm because it cleared
     *  @param path Alarm path
     *  @return <code>true</code> when removed, <code>false</code> when not found
     */
    private boolean removeAlarm(final String path)
    {
        synchronized (alarms)
        {
            for (int i=0;  i<alarms.size();  ++i)
                if (alarms.get(i).getPathName().equals(path))
                {
                    alarms.remove(i);
                    return true;
                }
        }
        return false;
    }

    /** Create a temporary alarm: IDs -1, no guidance etc.
     *  @param info Alarm info
     */
    private void createNewAlarm(final AlarmUpdateInfo info)
    {
        final String path[] = AlarmTreePath.splitPath(info.getNameOrPath());
        AlarmTreeItem parent = null;
        for (int i=0; i<path.length-1; ++i)
            parent = new AlarmTreeItem(parent, path[i], -1);
        final GlobalAlarm alarm = new GlobalAlarm(parent, path[path.length-1], -1,
                info.getSeverity(),
                info.getMessage(),
                info.getTimestamp());
        synchronized (alarms)
        {
            alarms.add(alarm);
        }
        // TODO schedule background task for fetching GUI detail
    }

    /** Inform listener of changes */
    private void fireAlarmUpdate()
    {
        listener.globalAlarmsChanged(this);
    }
}
