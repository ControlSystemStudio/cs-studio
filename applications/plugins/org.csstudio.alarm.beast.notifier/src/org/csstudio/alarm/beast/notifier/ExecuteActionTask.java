/*******************************************************************************
* Copyright (c) 2010-2012 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;

/**
 * Task scheduled by the work queue to execute an automated action.
 * Handle alarm update management.
 * @author Fred Arnaud (Sopra Group)
 * 
 */
public class ExecuteActionTask extends TimerTask {

	/** Unique action identifier */ 
	private final ActionID ID;
	
	/** Information from {@link AlarmTreeItem} providing the automated action */
	private final ItemInfo item;
	
	/** Map underlying PVs with their respective alarm  */
	private Map<String, PVAlarmHandler> pvs;
	
	/** The delay for the action. */
	private final int delay;
	
	private EActionStatus status = EActionStatus.OK;
	private EActionPriority priority= EActionPriority.OK;
	
	protected final WorkQueue workQueue;
	protected final IAutomatedAction scheduledAction;
	
	protected class ExecuteActionThread extends Thread
	{
		private final IAutomatedAction action;
		
		public ExecuteActionThread(final IAutomatedAction action) {
			super("ExecuteActionThread");
			this.action = action;
		}
		
		@Override
		public void run() {
			workQueue.incrementRunningThreads();
			refreshStatus();
			if (status.equals(EActionStatus.OK)) {
				try {
					List<PVSnapshot> snapshots = new ArrayList<PVSnapshot>();
					for (PVAlarmHandler handler : pvs.values()) {
						snapshots.add(handler.getCurrent());
					}
					action.execute(snapshots);
					Activator.getLogger().log(Level.INFO, getInfos() + " => EXECUTED");
				} catch (Exception e) {
					Activator.getLogger().log(Level.SEVERE,
							"ERROR executing " + getInfos() + " => " + e.getMessage());
				}
			} else if (status.equals(EActionStatus.CANCELED)) {
				Activator.getLogger().log(Level.INFO,
						getInfos() + " => INTERRUPTED: Alarm recovered.");
			}
			workQueue.decrementRunningThreads();
		}
	}

	public ExecuteActionTask(WorkQueue workQueue, ActionID id, ItemInfo item,
			IAutomatedAction action, int delay) {
		this.workQueue = workQueue;
		this.scheduledAction = action;
		this.ID = id;
		this.delay = delay;
		this.item = item;
		pvs = new ConcurrentHashMap<String, PVAlarmHandler>();
		if (item.isImportant()) {
			this.priority = EActionPriority.IMPORTANT;
		}
		Activator.getLogger().log(Level.FINE, "Triggered by {0}", item.getPath());
	}
	
	@Override
	public void run() {
		final ExecuteActionThread thread = new ExecuteActionThread(scheduledAction);
		thread.start();
		workQueue.remove(this);
	}
	
	public void execute() {
		final ExecuteActionThread thread = new ExecuteActionThread(scheduledAction);
		thread.start();
	}
	
	@Override
	public boolean cancel() {
		Activator.getLogger().log(Level.INFO, getInfos() + " => CANCELED");
		workQueue.remove(this);
		return super.cancel();
	}

	private void refreshStatus() {
		// If canceled, we do nothing
		if (status.equals(EActionStatus.CANCELED)
				|| status.equals(EActionStatus.FAILED))
			return;
		// Clean PVs
		synchronized (pvs) {
			Map<String, PVAlarmHandler> pvsClone = new HashMap<String, PVAlarmHandler>(pvs);
			for (Entry<String, PVAlarmHandler> entry : pvsClone.entrySet())
				if (entry.getValue().validate() == false)
					pvs.remove(entry.getKey());
			if (pvs.size() == 0)
				status = EActionStatus.CANCELED;
		}
	}

	public void updateAlarms(PVSnapshot pv) {
		// Update alarm handler
		synchronized (pvs) {
			PVAlarmHandler alarmHandler = pvs.get(pv.getName());
			if (alarmHandler == null) {
				alarmHandler = new PVAlarmHandler();
				pvs.put(pv.getName(), alarmHandler);
			}
			alarmHandler.update(pv);
		}
		// Update action priority
		if (pv.isImportant()) {
			this.priority = EActionPriority.IMPORTANT;
		} else if (!this.priority.equals(EActionPriority.IMPORTANT)) {
			switch (pv.getCurrentSeverity()) {
			case OK: this.priority = EActionPriority.OK; break;
			case MINOR: this.priority = EActionPriority.MINOR; break;
			case MAJOR: this.priority = EActionPriority.MAJOR; break;
			case INVALID: this.priority = EActionPriority.MAJOR; break;
			}
		}
		// If PV already in alarm & state come back to NoAlarm => cancel
		refreshStatus();
	}
	
	public String getInfos() {
		return item.getName() + ": " + ID.getAaTitle();
	}

	public EActionStatus getStatus() {
		return status;
	}

	public void setStatus(EActionStatus status) {
		this.status = status;
	}

	public EActionPriority getPriority() {
		return priority;
	}

	public void setPriority(EActionPriority priority) {
		this.priority = priority;
	}

	public ActionID getID() {
		return ID;
	}

	public ItemInfo getItem() {
		return item;
	}

	public int getDelay() {
		return delay;
	}

	public IAutomatedAction getScheduledAction() {
		return scheduledAction;
	}

}
