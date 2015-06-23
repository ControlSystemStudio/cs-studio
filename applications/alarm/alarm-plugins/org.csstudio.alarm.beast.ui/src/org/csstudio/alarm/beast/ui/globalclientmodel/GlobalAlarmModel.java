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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.csstudio.alarm.beast.AlarmTreePath;
import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.WorkQueue;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreeLeaf;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.alarm.beast.ui.Activator;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmUpdateInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

/** Model for a 'global' alarm client.
 *  Initially reads alarms from RDB, then tracks changes via JMS.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GlobalAlarmModel
{
    /** Singleton instance */
    private static volatile GlobalAlarmModel instance = null;

    /** Reference count for instance */
    private AtomicInteger references = new AtomicInteger();

    /** Listeners who registered for notifications */
    final private CopyOnWriteArrayList<GlobalAlarmModelListener> listeners =
        new CopyOnWriteArrayList<GlobalAlarmModelListener>();

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

    /** Initialize */
    private GlobalAlarmModel()
    {
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

        new ReadConfigJob(this).schedule();
    }

    /** Obtain the shared instance.
     *  <p>
     *  Increments the reference count.
     *  @see #release()
     *  @return Global alarm model instance
     */
    public static GlobalAlarmModel reference()
    {
        synchronized (GlobalAlarmModel.class)
        {
            if (instance == null)
                instance = new GlobalAlarmModel();
        }
        instance.references.incrementAndGet();
        return instance;
    }

    /** Release the 'instance' */
    private static void releaseInstance()
    {
        synchronized (GlobalAlarmModel.class)
        {
            instance = null;
        }
    }

    /** Must be called to release model when no longer used.
     *  <p>
     *  Based on reference count, model is closed when last
     *  user releases it.
     */
    public void release()
    {
        if (references.decrementAndGet() > 0)
            return;
        communicator.stop();
        releaseInstance();
    }

    /** @param listener Listener to add */
    public void addListener(final GlobalAlarmModelListener listener)
    {
        listeners.add(listener);
        // Send initial update
        listener.globalAlarmsChanged(this);
    }

    /** @param listener Listener to remove
     *  @throws IllegalArgumentException when listener not known
     */
    public void removeListener(final GlobalAlarmModelListener listener)
    {
        if (! listeners.remove(listener))
            throw new IllegalArgumentException("Unknown listener"); //$NON-NLS-1$
    }

    /** @return Roots of currently active global alarms */
    public AlarmTreeRoot[] getAlarmRoots()
    {
        synchronized (configurations)
        {
            return configurations.toArray(new AlarmTreeRoot[configurations.size()]);
        }
    }

    /** @return Currently active global alarms */
    public AlarmTreeLeaf[] getAlarms()
    {
        final List<AlarmTreeLeaf> alarms = new ArrayList<AlarmTreeLeaf>();
        synchronized (configurations)
        {
            for (AlarmTreeRoot root : configurations)
            {
                for (int i=root.getAlarmChildCount()-1; i>=0; --i)
                    root.addLeavesToList(alarms);
            }
        }
        return alarms.toArray(new AlarmTreeLeaf[alarms.size()]);
    }

    /** Read 'global' alarms from RDB
     *  Invoked by {@link ReadConfigJob}
     *  @param monitor Progress monitor
     */
    void readConfiguration(final IProgressMonitor monitor)
    {
        monitor.beginTask(org.csstudio.alarm.beast.Messages.AlarmClientModel_ReadingConfiguration, IProgressMonitor.UNKNOWN);
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
            monitor.subTask(NLS.bind(org.csstudio.alarm.beast.Messages.AlarmClientModel_WaitingForJMSFmt, ++wait));
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

        // Read global alarms from RDB
        List<AlarmTreeRoot> alarms = null;
        GlobalAlarmReader reader = null;
        try
        {
            reader = new GlobalAlarmReader();
            alarms = reader.readGlobalAlarms();
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.SEVERE,
                    "GlobalAlarmModel cannot read existing alarms", ex);
        }
        finally
        {
            if (reader != null)
                reader.close();
        }

        synchronized (configurations)
        {
            configurations.clear();
            if (alarms != null)
                configurations.addAll(alarms);
        }

        // Apply queued updates
        final WorkQueue queued_updates;
        synchronized (this)
        {
            queued_updates = update_queue;
            update_queue = null;
        }
        queued_updates.performQueuedCommands();

        // From now on, updates will be executed right away
        monitor.done();
        // Send initial alarm update after we read configuration
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
                    Preferences.getRDB_Password(), Preferences.getRDB_Schema(), alarm, null);
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
                item = item.getChild(path[i]);
            // Found?
            if (item == null || !(item instanceof GlobalAlarm))
                return false;

            // Up to the root, delete all 'empty' nodes
            final AlarmTreeRoot root = item.getRoot();
            while (item != null  &&  item.getChildCount() <= 0)
            {
                final AlarmTreeItem tmp = item;
                item = item.getParent();
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
        for (GlobalAlarmModelListener listener : listeners)
            listener.globalAlarmsChanged(this);
    }
}
