/*******************************************************************************
* Copyright (c) 2010-2012 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;
import org.csstudio.alarm.beast.notifier.util.NotifierUtils;

/**
 * Handle automated action management
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class AutomatedActionInvoker {
	
	/** Queue which handles pending actions */
	final private WorkQueue work_queue;
	
	private boolean maintenance_mode = false;
	
	public AutomatedActionInvoker(final int timer_threshold, final int thread_threshold) {
		work_queue = new WorkQueue(timer_threshold, thread_threshold);
	}
	
	/**
	 * Update a pending automated action.
	 * @param item
	 * @param aa
	 * @param snapshot
	 * @return <code>true</code> if action have been updated, <code>false</code> otherwise
	 */
	public boolean update(AlarmTreeItem item, AADataStructure aa, PVSnapshot snapshot) {
		ExecuteActionTask actionTask = work_queue.findAction(item, aa);
		if (actionTask == null)
			return false;
		// Update only if action is scheduled and not running
		actionTask.updateAlarms(snapshot);
		return true;
	}
	
	/**
	 * Store and schedule an automated action execution.
	 * @param item
	 * @param aa
	 * @param action
	 * @param snapshot
	 */
	public void storeExec(AlarmTreeItem item, AADataStructure aa, IAutomatedAction action, PVSnapshot snapshot) {
		final ItemInfo info = ItemInfo.fromItem(item);
		// No automated action if PV disabled
		if (info.isPV() && !info.isEnabled()) return;
		final ActionID naID = NotifierUtils.getActionID(item, aa);
		final ExecuteActionTask newTask = new ExecuteActionTask(work_queue,
				naID, info, action, aa.getDelay());
		if (!maintenance_mode
				|| (maintenance_mode && newTask.getPriority().equals(
						EActionPriority.IMPORTANT))) {
			newTask.updateAlarms(snapshot);
			work_queue.add(newTask);
		}
	}
	
	public void clean() {
		work_queue.interruptAll();
	}
	
	public void stop() {
		work_queue.flush();
	}

	public boolean isMaintenance_mode() {
		return maintenance_mode;
	}

	public void setMaintenance_mode(boolean maintenance_mode) {
		this.maintenance_mode = maintenance_mode;
		if (maintenance_mode) work_queue.interruptAll();
	}
	
	/** Dump to stdout */
	public void dump() {
		work_queue.dump();
	}
	
}
