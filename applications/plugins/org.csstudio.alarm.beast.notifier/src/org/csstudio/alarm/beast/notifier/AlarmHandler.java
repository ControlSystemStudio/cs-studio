/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;

/**
 * Manager of an automated action scheduled task.. Handle alarm update
 * management.
 * 
 * @author Fred Arnaud (Sopra Group)
 * 
 */
public class AlarmHandler {

	/** Unique action identifier */
	private final ActionID ID;

	/** Information from {@link AlarmTreeItem} providing the automated action */
	private final ItemInfo item;

	/** Map underlying PVs with their respective alarm */
	private Map<String, PVAlarmHandler> pvs;

	/** The delay for the action. */
	private final int delay;

	private EActionStatus status = EActionStatus.PENDING;
	private String reason = Messages.Empty;
	private EActionPriority priority = EActionPriority.OK;

	protected final IAutomatedAction scheduledAction;

	public AlarmHandler(ActionID id, ItemInfo item,
			IAutomatedAction action, int delay) {
		this.ID = id;
		this.item = item;
		this.scheduledAction = action;
		this.delay = delay;
		pvs = new ConcurrentHashMap<String, PVAlarmHandler>();
		if (item.isImportant()) {
			this.priority = EActionPriority.IMPORTANT;
		}
	}

	/**
	 * Update current status & priority
	 * 
	 * @param pv, snapshot of the pv on which an alarm event was raised.
	 */
	public void updateAlarms(PVSnapshot pv) {
		// Update action priority
		if (pv.isImportant()) {
			this.priority = EActionPriority.IMPORTANT;
		} else if (!this.priority.equals(EActionPriority.IMPORTANT)) {
			switch (pv.getCurrentSeverity()) {
			case OK: this.priority = EActionPriority.OK; break;
			case MINOR: this.priority = EActionPriority.MINOR; break;
			case MAJOR: this.priority = EActionPriority.MAJOR; break;
			case INVALID: this.priority = EActionPriority.MAJOR; break;
			case UNDEFINED: this.priority = EActionPriority.MAJOR; break;
			default: break;
			}
		}
		// Update alarm handler + action status
		synchronized (pvs) {
			PVAlarmHandler alarmHandler = pvs.get(pv.getName());
			if (alarmHandler == null) {
				alarmHandler = new PVAlarmHandler();
				pvs.put(pv.getName(), alarmHandler);
			}
			alarmHandler.update(pv);
			for (PVAlarmHandler ah : pvs.values()) {
				if (ah.getStatus().equals(EActionStatus.NO_DELAY)) {
					this.status = EActionStatus.NO_DELAY;
					this.reason = Messages.Reason_NoDelay;
				}
			}
			if (!this.status.equals(EActionStatus.NO_DELAY)) {
				boolean allCanceled = true;
				for (PVAlarmHandler ah : pvs.values())
					if (ah.getStatus().equals(EActionStatus.PENDING))
						allCanceled = false;
				if (allCanceled) {
					this.status = EActionStatus.CANCELED;
					if (item.isPV()) this.reason = alarmHandler.getReason();
					else this.reason = Messages.Reason_SubActionsCanceled;
				} else {
					this.status = EActionStatus.PENDING;
					this.reason = Messages.Empty;
				}
			}
		}
	}

	public String getInfos() {
		return scheduledAction.getClass().getSimpleName() + " " + ID.getAaTitle();
	}

	public void setStatus(EActionStatus status) {
		this.status = status;
	}

	public void setPriority(EActionPriority priority) {
		this.priority = priority;
	}

	public EActionStatus getStatus() {
		return status;
	}

	public EActionPriority getPriority() {
		return priority;
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

	public String getReason() {
		return reason;
	}

	public List<PVSnapshot> getCurrentSnapshots() {
		List<PVSnapshot> snapshots = new ArrayList<PVSnapshot>();
		for (PVAlarmHandler handler : pvs.values())
			if (!handler.getStatus().equals(EActionStatus.CANCELED)
					&& handler.getCurrent() != null)
				snapshots.add(handler.getCurrent());
		return snapshots;
	}

}
