/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.AlarmTreePosition;
import org.csstudio.alarm.beast.notifier.actions.AutomatedActionFactory;
import org.csstudio.alarm.beast.notifier.history.AlarmNotifierHistory;
import org.csstudio.alarm.beast.notifier.history.PVHistoryEntry;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;
import org.csstudio.alarm.beast.notifier.rdb.IAlarmRDBHandler;
import org.csstudio.alarm.beast.notifier.util.NotifierUtils;
import org.csstudio.logging.JMSLogMessage;

/**
 * Main thread for automated actions.
 *
 * @author Fred Arnaud (Sopra Group)
 * @author Kay Kasemir - One loop in handleAlarmUpdate for all actions up the tree
 */
@SuppressWarnings("nls")
public class AlarmNotifier {

    private boolean debug = false;

    /** Name of alarm tree root element */
    final String rootName = Preferences.getAlarmTreeRoot();

    /** Alarm model handler */
    final private IAlarmRDBHandler rdb;

    /** Automated actions factory */
    final private AutomatedActionFactory factory;

    /** Queue which handles pending actions */
    final private WorkQueue workQueue;

    private boolean maintenanceMode = false;

    public AlarmNotifier(final String root_name,
            final IAlarmRDBHandler rdbHandler,
            final AutomatedActionFactory factory, final int timer_threshold)
            throws Exception {
        this.rdb = rdbHandler;
        this.factory = factory;
        this.workQueue = new WorkQueue(timer_threshold, 60000); // 60s
    }

    /** @return Name of configuration root element */
    public String getRootName() {
        return rootName;
    }

    /** Connect to JMS */
    public void start() {
        Activator.getLogger().log(Level.INFO, "Alarm Notifier started");
    }

    /** Release all resources */
    public void stop() {
        rdb.close();
        workQueue.flush();
        Activator.getLogger().log(Level.INFO, "Alarm Notifier stopped");
    }

    /**
     * Read info about {@link AlarmTreeItem} from model.
     *
     * @param path, path of the item
     * @return ItemInfo
     */
    public ItemInfo getItemInfo(String path) {
        AlarmTreeItem item = rdb.findItem(path);
        return ItemInfo.fromItem(item);
    }

    /**
     * Start automated action for the given PV and its parents.
     *
     * @param pvItem
     */
    public synchronized void handleAlarmUpdate(final AlarmTreePV pvItem) {
        final PVSnapshot snapshot = PVSnapshot.fromPVItem(pvItem);
        final AlarmNotifierHistory history = AlarmNotifierHistory.getInstance();
        if (!pvItem.isEnabled()) {
            // Ignore PV, it's disabled
            history.clear(snapshot);
            return;
        }
        // Avoid to send 'fake' alarms when the PV is re-enabled for example
        if (snapshot.getValue() != null && snapshot.getValue().isEmpty())
            return;
        // Configuration update results in an alarm update with no changes
        final PVHistoryEntry pv_history = history.getPV(snapshot.getPath());
        if (pv_history == null
                && snapshot.getSeverity().equals(SeverityLevel.OK))
            return;
        if (pv_history != null
                && snapshot.getSeverity().equals(pv_history.getSeverity())
                && snapshot.getCurrentSeverity().equals(pv_history.getCurrentSeverity()))
            return;
        // Process automated actions of PV, recursing up to root
        for (AlarmTreeItem item = pvItem;
             item != null  &&  !item.getPosition().equals(AlarmTreePosition.Root);
             item = item.getParent()) {
            for (AADataStructure aa : item.getAutomatedActions())
                handleAutomatedAction(snapshot, item, aa);
        }

        // When only notifying on escalation, and the current
        // severity is OK, forget that this happened.
        // Otherwise remember so that we can notify with NO_DELAY
        // as the PV re-enters an alarm
        if (PVAlarmHandler.notify_escalating_alarms_only == false  ||
            snapshot.getCurrentSeverity() != SeverityLevel.OK)
            history.addSnapshot(snapshot);
    }

    private void handleAutomatedAction(PVSnapshot snapshot,
            AlarmTreeItem aaItem, AADataStructure aa) {
        final ActionID naID = NotifierUtils.getActionID(aaItem, aa);
        AlarmHandler actionTask = workQueue.find(naID);
        if (actionTask != null) {
            // Update only if action is scheduled and not yet executed
            actionTask.updateAlarms(snapshot);
            if (actionTask.getStatus().equals(EActionStatus.NO_DELAY)) {
                workQueue.interrupt(actionTask);
                if (!maintenanceMode
                        || (maintenanceMode && actionTask.getPriority().equals(
                                EActionPriority.IMPORTANT))) {
                    actionTask.setStatus(EActionStatus.FORCED);
                    workQueue.schedule(actionTask, true);
                }
            } else if (actionTask.getStatus().equals(
                    EActionStatus.CANCELED_NO_DELAY)) {
                workQueue.interrupt(actionTask);
                if (debug) AlarmNotifierHistory.getInstance().addAction(actionTask);
            }
            // Pending action updated => no need to create a new one
            return;
        }
        final ItemInfo info = ItemInfo.fromItem(aaItem);
        // No automated action if PV disabled
        if (info.isPV() && !info.isEnabled())
            return;
        final IAutomatedAction newAction = factory.getNotificationAction(aaItem, aa);
        if (newAction == null)
            return;
        final AlarmHandler newTask = new AlarmHandler(naID, info, newAction, aa.getDelay());
        newTask.updateAlarms(snapshot);
        if (newTask.getStatus().equals(EActionStatus.CANCELED)
                || newTask.getStatus().equals(EActionStatus.CANCELED_NO_DELAY)) {
            // Do not schedule canceled actions
            if (debug) AlarmNotifierHistory.getInstance().addAction(newTask);
            return;
        }
        if (!maintenanceMode
                || (maintenanceMode && newTask.getPriority().equals(
                        EActionPriority.IMPORTANT))) {
            if (newTask.getStatus().equals(EActionStatus.NO_DELAY)) {
                newTask.setStatus(EActionStatus.FORCED);
                workQueue.schedule(newTask, true);
            } else {
                workQueue.schedule(newTask, false);
            }
        }
    }

    /**
     * Cancel all current running automated actions when a new configuration is
     * set.
     */
    public void handleNewAlarmConfiguration() {
        workQueue.interruptAll();
        Activator.getLogger().config("New alarm configuration loaded, pending actions interrupted");
    }

    /**
     * Cancel all current running automated actions when maintenance mode is set
     * to <code>true</code>
     */
    public void handleModeUpdate(boolean maintenance_mode) {
        this.maintenanceMode = maintenance_mode;
        AlarmNotifierHistory.getInstance().clearAll();
        if (maintenance_mode)
            workQueue.interruptAll();
        Activator.getLogger().config("Maintenance mode "
                        + (maintenance_mode ? "activated, pending actions interrupted"
                                : "deactivated") + ", history cleaned");
    }

    /** Dump to stdout */
    public void dump() {
        System.out.println("== Alarm Notifier Snapshot ==");

        // Log memory usage in MB
        final double free = Runtime.getRuntime().freeMemory() / (1024.0 * 1024.0);
        final double total = Runtime.getRuntime().totalMemory() / (1024.0 * 1024.0);
        final double max = Runtime.getRuntime().maxMemory() / (1024.0 * 1024.0);
        final DateFormat format = new SimpleDateFormat(JMSLogMessage.DATE_FORMAT);
        System.out.format("%s == Alarm Notifier Memory: Max %.2f MB, Free %.2f MB (%.1f %%), total %.2f MB (%.1f %%)\n",
                        format.format(new Date()), max, free, 100.0 * free / max, total, 100.0 * total / max);

        workQueue.dump();
    }

    public WorkQueue getWorkQueue() {
        return workQueue;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

}