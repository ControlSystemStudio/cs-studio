/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.csstudio.alarm.beast.TimestampHelper;
import org.csstudio.alarm.beast.notifier.history.AlarmNotifierHistory;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;
import org.csstudio.alarm.beast.notifier.util.OverflowManager;
import org.epics.util.time.Timestamp;

/**
 * Automated actions work queue. Each action is scheduled in a timer and then
 * executed in a stand-alone thread. A scheduled task is executed only if its
 * status is OK.
 * 
 * @author Fred Arnaud (Sopra Group)
 * 
 */
public class WorkQueue {

	private class ScheduledActionTask extends TimerTask {

		private final AlarmHandler alarmHandler;

		public ScheduledActionTask(final AlarmHandler alarmHandler) {
			this.alarmHandler = alarmHandler;
		}

		@Override
		public void run() {
			if (alarmHandler.getStatus().equals(EActionStatus.PENDING)
					|| alarmHandler.getStatus().equals(EActionStatus.FORCED)) {
				if (alarmHandler.getStatus().equals(EActionStatus.PENDING))
					alarmHandler.setStatus(EActionStatus.EXECUTED);
				execute(alarmHandler);
			} else {
				if (debug)
					AlarmNotifierHistory.getInstance().addAction(alarmHandler);
				String time = TimestampHelper.format(Timestamp.now());
				Activator.getLogger().log(Level.INFO,
						time + " CANCEL " + alarmHandler.getInfos() + " because " + alarmHandler.getReason());
			}
			remove(this.alarmHandler);
		}

		@Override
		public boolean cancel() {
			String time = TimestampHelper.format(Timestamp.now());
			String reason = alarmHandler.getReason().isEmpty() ? "the timer has been canceled"
					: alarmHandler.getReason();
			Activator.getLogger().log(Level.INFO,
					time + " CANCEL " + alarmHandler.getInfos() + " because " + reason);
			alarmHandler.setStatus(EActionStatus.CANCELED);
			return super.cancel();
		}

		public AlarmHandler getAlarmHandler() {
			return alarmHandler;
		}

	}

	private class ExecuteActionThread extends Thread {

		private final String infos;
		private final List<PVSnapshot> snapshots;
		private final IAutomatedAction action;

		public ExecuteActionThread(final IAutomatedAction action,
				final String info, final List<PVSnapshot> snapshots) {
			super("ExecuteActionThread");
			this.infos = info;
			this.snapshots = snapshots;
			this.action = action;
		}

		@Override
		public void run() {
			incrementRunningThreads();
			try {
				String time = TimestampHelper.format(Timestamp.now());
				Activator.getLogger().log(Level.INFO, time + " EXECUTION " + infos);
				action.execute(snapshots);
			} catch (Exception e) {
				Activator.getLogger().log(Level.SEVERE,
						"ERROR executing " + infos + ": " + e.getMessage());
			}
			decrementRunningThreads();
		}
	}

	private boolean debug = false;

	/** Overflow timer_threshold */
	private final int timer_threshold;
	private final int time_interval;

	/** Number of running actions */
	private int count_pending = 0;
	private int count_thread = 0;

	/** Minimum allowed action priority if overflow occurs */
	private final EActionPriority overflow_level = EActionPriority.MAJOR;

	/** Automated actions scheduler */
	private Timer timer;
	private Map<ActionID, ScheduledActionTask> scheduledActions;

	private Map<Class<?>, OverflowManager> overflowManagers;
	private Map<Class<?>, ReentrantLock> flushLocks;
	private Map<Class<?>, Boolean> classCleaned;

	public WorkQueue(final int timer_threshold, final int time_interval) {
		timer = new Timer();
		this.timer_threshold = timer_threshold;
		this.time_interval = time_interval;
		classCleaned = new ConcurrentHashMap<Class<?>, Boolean>();
		flushLocks = new ConcurrentHashMap<Class<?>, ReentrantLock>();
		overflowManagers = new ConcurrentHashMap<Class<?>, OverflowManager>();
		scheduledActions = new ConcurrentHashMap<ActionID, ScheduledActionTask>();
	}

	// Remove an automated action from the list
	private void remove(final AlarmHandler alarmHandler) {
		if (scheduledActions.remove(alarmHandler.getID()) != null)
			decrementPendingActions();
	}

	// If overflow => schedule only Systems actions or PV with a severity
	// level higher or equal to the one defined as preference.
	private boolean isAllowed(final AlarmHandler alarmHandler) {
		if (!alarmHandler.getItem().isPV()
				|| (alarmHandler.getItem().isPV() && alarmHandler.getPriority()
						.compareTo(overflow_level) >= 0))
			return true;
		return false;
	}

	private ReentrantLock getLock(final Class<?> actionClass) {
		if (flushLocks.get(actionClass) == null)
			flushLocks.put(actionClass, new ReentrantLock());
		return flushLocks.get(actionClass);
	}

	private OverflowManager getOverflowManager(final Class<?> actionClass) {
		if (overflowManagers.get(actionClass) == null)
			overflowManagers.put(actionClass, new OverflowManager(
					this.timer_threshold, this.time_interval));
		return overflowManagers.get(actionClass);
	}

	private Boolean getCleaned(final Class<?> actionClass) {
		if (classCleaned.get(actionClass) == null)
			classCleaned.put(actionClass, false);
		return classCleaned.get(actionClass);
	}

	private void setCleaned(final Class<?> actionClass, Boolean cleaned) {
		classCleaned.put(actionClass, cleaned);
	}

	/**
	 * Returns currently scheduled automated action if exists, <code>null</code>
	 * otherwise.
	 */
	public AlarmHandler find(final ActionID actionId) {
		if (scheduledActions.get(actionId) == null)
			return null;
		return scheduledActions.get(actionId).getAlarmHandler();
	}

	/** Add an automated action to the work queue and schedule it. */
	public void schedule(final AlarmHandler alarmHandler, boolean noDelay) {
		final Class<?> actionClass = alarmHandler.getScheduledAction().getClass();
		final OverflowManager overflowManager = getOverflowManager(actionClass);
		overflowManager.refreshOverflow();
		if (overflowManager.isOverflowed()) {
			boolean lockAcquired = false;
			try {
				if (getCleaned(actionClass) == false
						&& getLock(actionClass).tryLock()) {
					lockAcquired = true;
					Activator.getLogger().log(Level.WARNING,
							"Work queue OVERFLOWED, start cleaning: " + actionClass.getSimpleName() + " !");
					flushClass(alarmHandler.getScheduledAction().getClass());
					Activator.getLogger().log(Level.WARNING,
							"Work queue CLEANED: " + actionClass.getSimpleName() + " !");
					setCleaned(actionClass, true);
				}
			} finally {
				if (lockAcquired)
					getLock(actionClass).unlock();
			}
		} else {
			setCleaned(actionClass, false);
		}
		if ((overflowManager.isOverflowed() && isAllowed(alarmHandler))
				|| !overflowManager.isOverflowed()) {
			ActionID actionId = alarmHandler.getID();
			ScheduledActionTask newTask = new ScheduledActionTask(alarmHandler);
			// TODO: action already scheduled ? => remove
			if (scheduledActions.get(actionId) != null) { // replace
				ScheduledActionTask oldTask = scheduledActions.get(actionId);
				oldTask.cancel();
				scheduledActions.put(actionId, newTask);
			} else {
				scheduledActions.put(actionId, newTask);
				incrementPendingActions();
			}
			int delay = noDelay ? 0 : (alarmHandler.getDelay() * 1000);
			String time = TimestampHelper.format(Timestamp.now());
			timer.schedule(newTask, delay);
			Activator.getLogger().log(Level.INFO,
							time + " SUBMISSION " + alarmHandler.getInfos()
									+ " scheduled in " + (delay / 1000)
									+ " seconds on " + alarmHandler.getItem().getName());
		}
	}

	/** Execute an automated action. */
	public void execute(final AlarmHandler alarmHandler) {
		new ExecuteActionThread(alarmHandler.getScheduledAction(),
				alarmHandler.getInfos(), alarmHandler.getCurrentSnapshots()).start();
		if (debug)
			AlarmNotifierHistory.getInstance().addAction(alarmHandler);
	}

	/** Interrupt an automated action. */
	public void interrupt(final AlarmHandler alarmHandler) {
		ScheduledActionTask task = scheduledActions.get(alarmHandler.getID());
		if (task != null) {
			task.cancel();
			remove(task.getAlarmHandler());
		}
	}

	/** Interrupt all automated actions. */
	public void interruptAll() {
		synchronized (scheduledActions) {
			for (ScheduledActionTask task : scheduledActions.values())
				task.cancel();
			scheduledActions.clear();
			this.count_pending = 0;
		}
	}

	/** Flush the work queue. */
	public void flush() {
		for (Class<?> actionClass : overflowManagers.keySet())
			flushClass(actionClass);
	}

	private void flushClass(final Class<?> actionClass) {
		Map<ActionID, ScheduledActionTask> scheduledActionsToFlush = null;
		synchronized (scheduledActions) {
			scheduledActionsToFlush = new ConcurrentHashMap<ActionID, ScheduledActionTask>();
			Iterator<Entry<ActionID, ScheduledActionTask>> it = scheduledActions.entrySet().iterator();
			while (it.hasNext()) {
				final Entry<ActionID, ScheduledActionTask> entry = it.next();
				if (entry.getValue().getAlarmHandler().getScheduledAction()
						.getClass().equals(actionClass)) {
					scheduledActionsToFlush.put(entry.getKey(),
							entry.getValue());
					it.remove();
				}
			}
			this.count_pending -= scheduledActionsToFlush.size();
		}
		for (ScheduledActionTask task : scheduledActionsToFlush.values()) {
			task.cancel();
			AlarmHandler alarmHandler = task.getAlarmHandler();
			if (isAllowed(alarmHandler)) {
				alarmHandler.setStatus(EActionStatus.FORCED);
				execute(alarmHandler);
			}
		}
	}

	/** @return Number of currently queued actions on the work queue */
	public int countPendingActions() {
		return count_pending;
	}

	/** @return Number of currently running threads on the work queue */
	public int countRunningThreads() {
		return count_thread;
	}

	private synchronized void incrementPendingActions() {
		count_pending++;
	}

	private synchronized void incrementRunningThreads() {
		count_thread++;
	}

	private synchronized void decrementPendingActions() {
		count_pending--;
	}

	private synchronized void decrementRunningThreads() {
		count_thread--;
	}

	/** Dump to stdout */
	public void dump() {
		System.out.println("== Work Queue Snapshot ==");
		System.out.println("Work work_queue size:");
		System.out.println(">>>> Pending actions: " + count_pending);
		System.out.println(">>>> Running threads: " + count_thread);
		System.out.println("Pending actions list:");
		for (ScheduledActionTask task : scheduledActions.values()) {
			System.out.println(task.getAlarmHandler().getScheduledAction());
		}
		System.out.println("== Work Queue Snapshot ==");
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}
