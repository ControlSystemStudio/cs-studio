/*******************************************************************************
* Copyright (c) 2010-2013 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.rdb;

import java.util.logging.Level;

import org.csstudio.alarm.beast.TreeItem;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.notifier.Activator;
import org.csstudio.alarm.beast.notifier.AlarmNotifier;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;

/**
 * Wrapper for alarm model.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class AlarmRDBHandler implements IAlarmRDBHandler {

    /** Server for which we communicate */
    private AlarmNotifier notifier;
    
    /** Alarm model */
    final private AlarmClientModel model;
    
    
	public AlarmRDBHandler(final String root) throws Exception {
		model = AlarmClientModel.getInstance();
		model.setConfigurationName(root, this);
		model.addListener(this);
	}

	/** Initialize wrapper with AlarmNotifier */
	public void init(final AlarmNotifier notifier) {
		this.notifier = notifier;
	}

	/** Find item by path */
	public AlarmTreeItem findItem(String path) {
		return model.getConfigTree().getItemByPath(path);
	}

	/** @return Current alarm tree */
	public TreeItem getAlarmTree() {
		return model.getConfigTree();
	}
	
	/** Must be called to release resources */
	public void close() {
		model.release();
	}

	@Override
	public void newAlarmConfiguration(AlarmClientModel model) {
		notifier.handleNewAlarmConfiguration();
	}

	@Override
	public void serverModeUpdate(AlarmClientModel model, boolean maintenance_mode) {
		notifier.handleModeUpdate(maintenance_mode);
	}

	@Override
	public void serverTimeout(AlarmClientModel model) {
		Activator.getLogger().log(Level.SEVERE, "Alarm Server timeout");
	}

	@Override
	public void newAlarmState(AlarmClientModel model, AlarmTreePV pv,
			boolean parent_changed) {
		if (pv == null)
			return;
		notifier.handleAlarmUpdate(pv);
	}
	
}
