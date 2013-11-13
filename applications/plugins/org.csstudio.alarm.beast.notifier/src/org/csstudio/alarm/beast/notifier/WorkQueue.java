/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.csstudio.alarm.beast.notifier.history.AlarmNotifierHistory;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;

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

		private final AlarmHandler taskManager;

		public ScheduledActionTask(final AlarmHandler taskManager) {
			this.taskManager = taskManager;
		}

		@Override
		public void run() {
			if (taskManager.getStatus().equals(EActionStatus.PENDING)) {
				taskManager.setStatus(EActionStatus.EXECUTED);
				execute(taskManager);
			} else {
				if (debug)
					AlarmNotifierHistory.getInstance().addAction(taskManager);
				Activator.getLogger().log(Level.INFO,
						taskManager.getInfos() + " => TASK INTERRUPTED");
			}
			remove(this.taskManager);
		}

		@Override
		public boolean cancel() {
			Activator.getLogger().log(Level.INFO,
					taskManager.getInfos() + " => TASK CANCELED");
			taskManager.setStatus(EActionStatus.CANCELED);
			return super.cancel();
		}

		public AlarmHandler getTaskManager() {
			return taskManager;
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
				action.execute(snapshots);
				Activator.getLogger().log(Level.INFO, infos + " => EXECUTED");
			} catch (Exception e) {
				Activator.getLogger().log(Level.SEVERE,
						"ERROR executing " + infos + " => " + e.getMessage());
			}
			decrementRunningThreads();
		}
	}

	private boolean debug = false;

	/** Overflow timer_threshold */
	private final int timer_threshold;
	private final int thread_threshold;

	/** Number of running actions */
	private int count_pending = 0;
	private int count_thread = 0;

	/** Minimum allowed action priority if overflow occurs */
	private final EActionPriority overflow_level = EActionPriority.MAJOR;

	/** Automated actions scheduler */
	private Timer timer;
	private Map<ActionID, ScheduledActionTask> scheduledActions;

	public WorkQueue(final int timer_threshold, final int thread_threshold) {
		this.timer_threshold = timer_threshold;
		this.thread_threshold = thread_threshold;
		timer = new Timer();
		scheduledActions = new ConcurrentHashMap<ActionID, ScheduledActionTask>();
	}

	// Remove an automated action from the list
	private void remove(final AlarmHandler taskManager) {
		if (scheduledActions.remove(taskManager.getID()) != null)
			decrementPendingActions();
	}

	/**
	 * Returns currently scheduled automated action if exists, <code>null</code>
	 * otherwise.
	 */
	public AlarmHandler find(final ActionID actionId) {
		if (scheduledActions.get(actionId) == null)
			return null;
		return scheduledActions.get(actionId).getTaskManager();
	}

	/** Add an automated action to the work queue and schedule it. */
	public void schedule(final AlarmHandler taskManager) {
		if (isOverflooded()) {
			Activator.getLogger().log(Level.WARNING,
					"Work queue overflooded, start cleaning !");
			flush();
		}
		// If overflow => schedule only Systems actions with a severity level
		// higher or equal to the one defined as preference.
		if ((isOverflooded()
				&& taskManager.getPriority().compareTo(overflow_level) >= 0 && !taskManager
				.getItem().isPV()) || !isOverflooded()) {
			ActionID actionId = taskManager.getID();
			ScheduledActionTask newTask = new ScheduledActionTask(taskManager);
			// TODO: action already scheduled ? => remove
			if (scheduledActions.get(actionId) != null) { // replace
				ScheduledActionTask oldTask = scheduledActions.get(actionId);
				oldTask.cancel();
				scheduledActions.put(actionId, newTask);
			} else {
				scheduledActions.put(actionId, newTask);
				incrementPendingActions();
			}
			timer.schedule(newTask, taskManager.getDelay() * 1000);
			Activator.getLogger().log(Level.INFO,
					taskManager.getInfos() + " => SCHEDULED: " + taskManager.getDelay() + "s");
		}
	}

	/** Execute an automated action. */
	public void execute(final AlarmHandler taskManager) {
		new ExecuteActionThread(taskManager.getScheduledAction(),
				taskManager.getInfos(), taskManager.getCurrentSnapshots()).start();
		if (debug)
			AlarmNotifierHistory.getInstance().addAction(taskManager);
	}

	/** Interrupt an automated action. */
	public void interrupt(final AlarmHandler taskManager) {
		ScheduledActionTask task = scheduledActions.get(taskManager.getID());
		if (task != null) {
			task.cancel();
			remove(task.getTaskManager());
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
		synchronized (scheduledActions) {
			for (ScheduledActionTask task : scheduledActions.values()) {
				task.cancel();
				AlarmHandler taskManager = task.getTaskManager();
				// force execution of Systems & PVs with high priority
				if ((taskManager.getPriority().compareTo(overflow_level) >= 0 && taskManager
						.getItem().isPV()) || !taskManager.getItem().isPV()) {
					taskManager.setStatus(EActionStatus.FORCED);
					execute(taskManager);
				}
			}
			scheduledActions.clear();
			this.count_pending = 0;
			Activator.getLogger().log(Level.WARNING, "Work queue cleaned !");
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
		if (count_thread > thread_threshold) {
			Activator.getLogger().log(Level.SEVERE,
					"Too many threads running: " + count_thread + " !");
		}
	}

	private synchronized void decrementPendingActions() {
		count_pending--;
	}

	private synchronized void decrementRunningThreads() {
		count_thread--;
	}

	public boolean isOverflooded() {
		return count_pending >= timer_threshold;
	}

	/** Dump to stdout */
	public void dump() {
		System.out.println("== Work Queue Snapshot ==");
		System.out.println("Work work_queue size:");
		System.out.println(">>>> Pending actions: " + count_pending);
		System.out.println(">>>> Running threads: " + count_thread);
		System.out.println("Pending actions list:");
		for (ScheduledActionTask task : scheduledActions.values()) {
			System.out.println(task.getTaskManager().getScheduledAction());
		}
		System.out.println("== Work Queue Snapshot ==");
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}
