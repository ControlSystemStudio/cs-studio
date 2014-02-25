/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.test;

import java.util.HashMap;
import java.util.logging.Level;

import org.csstudio.alarm.beast.Activator;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.TreeItem;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.AlarmTreePosition;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.alarm.beast.notifier.AlarmNotifier;
import org.csstudio.alarm.beast.notifier.rdb.IAlarmRDBHandler;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;

/**
 * Fake {@link AlarmClientModel} for test purpose. Instantiate a basic AlarmTree
 * and fake model behavior.
 * 
 * @author Fred Arnaud (Sopra Group)
 * 
 */
public class MockAlarmRDBHandler implements IAlarmRDBHandler {

	/** Server for which we communicate */
	private AlarmNotifier notifier;

	/**
	 * Hierarchical alarm configuration <B>NOTE: Access to tree, PV list and map
	 * must synchronize on 'this'</B>
	 */
	private AlarmTreeRoot alarm_tree;

	/** Hash of all PVs in config_tree that maps PV name to PV */
	private HashMap<String, AlarmTreePV> pvs = new HashMap<String, AlarmTreePV>();

	public MockAlarmRDBHandler(boolean configureSystem) {
		alarm_tree = UnitTestUtils.buildBasicTree(configureSystem);
		listPVs(alarm_tree);
	}

	public void init(final AlarmNotifier notifier) {
		this.notifier = notifier;
	}

	public AlarmTreeItem findItem(String path) {
		return alarm_tree.getItemByPath(path);
	}

	public TreeItem getAlarmTree() {
		return alarm_tree;
	}

	/** Must be called to release resources */
	public void close() {
	}

	@Override
	public void newAlarmConfiguration(AlarmClientModel model) {
	}

	@Override
	public void serverModeUpdate(AlarmClientModel model,
			boolean maintenance_mode) {
	}

	@Override
	public void serverTimeout(AlarmClientModel model) {
	}

	@Override
	public void newAlarmState(AlarmClientModel model, AlarmTreePV pv,
			boolean parent_changed) {
		notifier.handleAlarmUpdate(pv);
	}
	
	public void updatePV(final String name,
			final SeverityLevel current_severity,
			final SeverityLevel alarm_severity) {
		updatePV(name, current_severity, "", alarm_severity, "", "0");
	}

	public void updatePV(final String name,
			final SeverityLevel current_severity, final String current_message,
			final SeverityLevel alarm_severity, final String alarm_message,
			final String value) {
		final AlarmTreePV pv = findPV(name);
		if (pv != null) {
			pv.setAlarmState(current_severity, current_message, alarm_severity,
					alarm_message, value, null);
			// Fake notify listeners
			newAlarmState(null, pv, true);
			return;
		}
		Activator.getLogger().log(Level.WARNING,
				"Received update for unknown PV {0}", name);
	}

	/**
	 * Locate PV by name
	 * 
	 * @param name Name of PV to locate. May be <code>null</code>.
	 * @return PV or <code>null</code> when not found
	 */
	public synchronized AlarmTreePV findPV(final String name) {
		return pvs.get(name);
	}

	private void listPVs(AlarmTreeItem root) {
		if (root == null)
			return;
		for (int i = 0; i < root.getChildCount(); i++) {
			final AlarmTreeItem child = root.getChild(i);
			if (child.getPosition().equals(AlarmTreePosition.PV)
					&& child instanceof AlarmTreePV)
				pvs.put(child.getName(), (AlarmTreePV) child);
			else
				listPVs(child);
		}
	}

}
