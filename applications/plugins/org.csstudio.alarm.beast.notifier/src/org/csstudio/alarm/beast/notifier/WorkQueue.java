/*******************************************************************************
* Copyright (c) 2010-2012 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.notifier.util.NotifierUtils;

/**
 * Automated actions work queue.
 * Each action is ran in a stand-alone thread.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class WorkQueue {
	
	/** Overflow timer_threshold */
	private final int timer_threshold;
	private final int thread_threshold;
	
	/** Number of running actions */
	private int count_pending = 0;
	private int count_thread = 0;
	
	/** Minimum allowed action priority if overflow occurs */
	private final EActionPriority overflow_level = EActionPriority.MAJOR;
	private boolean cleaned = false;
	
	/** Automated actions scheduler */
	private Timer timer;
	
	private Map<ActionID, ExecuteActionTask> scheduledActions;
	
	public WorkQueue(final int timer_threshold, final int thread_threshold) {
		this.timer_threshold = timer_threshold;
		this.thread_threshold = thread_threshold;
		timer = new Timer();
		scheduledActions = new ConcurrentHashMap<ActionID, ExecuteActionTask>();
	}
	
	/** @return Number of currently queued actions on the work queue */
	public int CountPendingActions() {
		return count_pending;
	}
	
	/** @return Number of currently running threads on the work queue */
	public int countRunningThreads() {
		return count_thread;
	}
	
	public synchronized void incrementPendingActions() {
		count_pending++;
	}
	public synchronized void incrementRunningThreads() {
		count_thread++;
		if (count_thread > thread_threshold) {
			Activator.getLogger().log(Level.SEVERE,
					"Too many threads running: " + count_thread + " !");
		}
	}
	public synchronized void decrementPendingActions() {
		count_pending--;
	}
	public synchronized void decrementRunningThreads() {
		count_thread--;
	}
	
	/** @return Currently running automated action for the specified {@link AlarmTreeItem} and {@link AADataStructure} */
	public ExecuteActionTask findAction(AlarmTreeItem item, AADataStructure aa) {
		ActionID naID = NotifierUtils.getActionID(item, aa);
		return scheduledActions.get(naID);
	}
	
	/** Add an automated action to the work queue and runs it */
	public void add(final ExecuteActionTask newTask) {
		if (isOverflooded()) {
			if (!cleaned) {
				Activator.getLogger().log(Level.WARNING,
						"Work queue overflooded, start cleaning !");
				flush();
				cleaned = true;
			}
		} else {
			cleaned = false;
		}
		ActionID naID = newTask.getID();
		if ((isOverflooded()
				&& newTask.getPriority().compareTo(overflow_level) >= 0 
				&& !newTask.getItem().isPV()) 
				|| !isOverflooded()) {
			// TODO: action already scheduled ? => remove
			if (scheduledActions.get(naID) != null) { // replace
				ExecuteActionTask oldTask = scheduledActions.get(naID);
				oldTask.cancel();
				scheduledActions.put(naID, newTask);
			} else {
				scheduledActions.put(naID, newTask);
				incrementPendingActions();
			}
			timer.schedule(newTask, newTask.getDelay() * 1000);
			Activator.getLogger().log(Level.INFO,
					newTask.getInfos() + " => SCHEDULED: " + newTask.getDelay() + "s");
		}
	}

	/** Remove an automated action */
	public void remove(final ExecuteActionTask task) {
		if (scheduledActions.remove(task.getID()) != null) {
			decrementPendingActions();
		}
	}
	
	public boolean isOverflooded() {
		return count_pending >= timer_threshold;
	}
	
	/** Interrupt all automated actions */
	public void interruptAll() {
		synchronized (scheduledActions) {
			for (ExecuteActionTask task : scheduledActions.values()) {
				interrupt(task);
			}
		}
	}
	
	/** Interrupt an automated action */
	public void interrupt(ExecuteActionTask task) {
		task.cancel();
	}

	/** Flush the work queue */
	public void flush() {
		for (ExecuteActionTask task : scheduledActions.values()) {
			interrupt(task);
			// force execution of Components & PV with high priority
			if ((task.getPriority().compareTo(overflow_level) >= 0 && task.getItem().isPV()) 
					|| !task.getItem().isPV()) {
				task.execute();
			}
		}
	}

	/** Dump to stdout */
	public void dump() {
		System.out.println("== Work Queue Snapshot ==");
		System.out.println("Work work_queue size:");
		System.out.println(">>>> Pending actions: " + count_pending);
		System.out.println(">>>> Running threads: " + count_thread);
		System.out.println("Pending actions list:");
		for (ExecuteActionTask scheduledAction : scheduledActions.values()) {
			System.out.println(scheduledAction.getScheduledAction());
		}
		System.out.println("== Work Queue Snapshot ==");
	}
	
}
