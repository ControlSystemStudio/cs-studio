/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.history;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.notifier.ActionID;
import org.csstudio.alarm.beast.notifier.AlarmHandler;
import org.csstudio.alarm.beast.notifier.PVSnapshot;

/**
 * Handles history of previously executed automated actions & alarm updates.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 * 
 */
public class AlarmNotifierHistory {

	private static AlarmNotifierHistory instance;

	private final Map<ActionID, ActionHistoryEntry> actions;
	// map PV path (unique) => PVHistoryEntry
	private Map<String, PVHistoryEntry> pvs;

	private AlarmNotifierHistory() {
		actions = new ConcurrentHashMap<ActionID, ActionHistoryEntry>();
		pvs = new ConcurrentHashMap<String, PVHistoryEntry>();
	}

	public static AlarmNotifierHistory getInstance() {
		if (instance == null)
			instance = new AlarmNotifierHistory();
		return instance;
	}

	public void addSnapshot(PVSnapshot s) {
		// For optimization purpose, we do not store the snapshot if alarm severity is OK
		if (s.getSeverity().equals(SeverityLevel.OK)) {
			pvs.remove(s.getPath());
			return;
		}
		if (pvs.get(s.getPath()) != null) {
			pvs.get(s.getPath()).update(s);
		} else {
			pvs.put(s.getPath(), PVHistoryEntry.fromSnapshot(s));
		}
	}

	public void addAction(AlarmHandler task) {
		final ActionHistoryEntry entry = new ActionHistoryEntry(task.getID(), task.getStatus());
		actions.put(task.getID(), entry);
	}

	public PVHistoryEntry getPV(String path) {
		return pvs.get(path);
	}

	public ActionHistoryEntry getAction(ActionID id) {
		return actions.get(id);
	}

	public void clear(PVSnapshot s) {
		pvs.remove(s.getPath());
	}

	public void clearAll() {
		this.actions.clear();
		this.pvs.clear();
	}

}
