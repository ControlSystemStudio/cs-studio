/*******************************************************************************
* Copyright (c) 2010-2012 ITER Organization.
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
import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.AlarmTreePosition;
import org.csstudio.alarm.beast.notifier.actions.AutomatedActionFactory;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;
import org.csstudio.alarm.beast.notifier.rdb.IAlarmRDBHandler;
import org.csstudio.logging.JMSLogMessage;

/**
 * Main thread for automated actions.
 * 
 * @author Fred Arnaud (Sopra Group)
 * 
 */
public class AlarmNotifier {
	/** Name of alarm tree root element */
	final String root_name = Preferences.getAlarmTreeRoot();

	/** Alarm model handler */
	final private IAlarmRDBHandler rdb;

	/** Automated actions factory */
	final private AutomatedActionFactory factory;
	
	final private AutomatedActionInvoker invoker;

	public AlarmNotifier(final String root_name,
			final IAlarmRDBHandler rdbHandler,
			final AutomatedActionFactory factory, 
			final int timer_threshold, 
			final int thread_threshold)
			throws Exception {
		this.rdb = rdbHandler;
		this.factory = factory;
		this.invoker = new AutomatedActionInvoker(timer_threshold, thread_threshold);
	}

	/** @return Name of configuration root element */
	public String getRootName() {
		return root_name;
	}

	/** Dump to stdout */
	public void dump() {
		System.out.println("== Alarm Notifier Snapshot ==");
		// TODO: improve 
//		System.out.println("Work work_queue size: " + work_queue.size());

		// Log memory usage in MB
		final double free = Runtime.getRuntime().freeMemory() / (1024.0 * 1024.0);
		final double total = Runtime.getRuntime().totalMemory() / (1024.0 * 1024.0);
		final double max = Runtime.getRuntime().maxMemory() / (1024.0 * 1024.0);

		final DateFormat format = new SimpleDateFormat( JMSLogMessage.DATE_FORMAT);
		System.out.format("%s == Alarm Notifer Memory: Max %.2f MB, Free %.2f MB (%.1f %%), total %.2f MB (%.1f %%)\n",
						format.format(new Date()), max, free, 100.0 * free / max, total, 100.0 * total / max);
	}

	/** Connect to JMS */
	public void start() {
		Activator.getLogger().log(Level.INFO, "Alarm Notifier started");
	}

	/** Release all resources */
	public void stop() {
		invoker.stop();
		rdb.close();
		Activator.getLogger().log(Level.INFO, "Alarm Notifier stopped");
	}

	/**
	 * Read info about {@link AlarmTreeItem} from model.
	 * @param path, path of the item
	 * @return ItemInfo
	 */
	public ItemInfo getItemInfo(String path) {
		AlarmTreeItem item = rdb.findItem(path);
		return ItemInfo.fromItem(item);
	}

	/**
	 * Start automated action for the given PV and its parents.
	 * @param pvItem
	 */
	public void handleAlarmUpdate(AlarmTreePV pvItem) {
		final PVSnapshot snapshot = PVSnapshot.fromPVItem(pvItem);
		// Process PV automated actions
		if (pvItem.getAutomatedActions() != null) {
			for (AADataStructure aa : pvItem.getAutomatedActions()) {
				handleAutomatedAction(snapshot, pvItem, aa);
			}
		}
		// Process System automated actions
		AlarmTreeItem item = pvItem.getClientParent();
		while (item != null) {
			for (AADataStructure aa : item.getAutomatedActions()) {
				handleAutomatedAction(snapshot, item, aa);
			}
			item = item.getClientParent();
			if (item.getPosition().equals(AlarmTreePosition.Root))
				break;
		}
	}

	private void handleAutomatedAction(PVSnapshot snapshot,
			AlarmTreeItem aaItem, AADataStructure aa) {
		if (invoker.update(aaItem, aa, snapshot))
			return; // Pending action updated => no need to create a new one
		final IAutomatedAction newAction = factory.getNotificationAction( aaItem, aa);
		if (newAction == null) return;
		invoker.storeExec(aaItem, aa, newAction, snapshot);
	}

	/**
	 * Cancel all current running automated actions when a new configuration is
	 * set.
	 */
	public void handleNewAlarmConfiguration() {
		Activator.getLogger().config("New Alarm Configuration");
		invoker.clean();
	}

	/**
	 * Cancel all current running automated actions when maintenance mode is set
	 * to <code>true</code>
	 */
	public void handleModeUpdate(boolean maintenance_mode) {
		Activator.getLogger().config("Maintenance Mode: " + maintenance_mode);
		invoker.setMaintenance_mode(maintenance_mode);
	}

}
