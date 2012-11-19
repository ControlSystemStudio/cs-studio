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
import org.csstudio.alarm.beast.notifier.actions.NotificationActionFactory;
import org.csstudio.alarm.beast.notifier.actions.NotificationActionListener;
import org.csstudio.alarm.beast.notifier.model.INotificationAction;
import org.csstudio.alarm.beast.notifier.rdb.IAlarmRDBHandler;
import org.csstudio.logging.JMSLogMessage;

/**
 * Main thread for automated actions.
 * 
 * @author Fred Arnaud (Sopra Group)
 * 
 */
public class AlarmNotifier implements NotificationActionListener {
	/** Name of alarm tree root element */
	final String root_name = Preferences.getAlarmTreeRoot();

	/** Alarm model handler */
	final private IAlarmRDBHandler rdb;

	/** Messenger to communicate with clients */
	final private NotifierCommunicator messenger;

	/** Automated actions factory */
	final private NotificationActionFactory factory;

	/** Queue which handles pending actions */
	final private WorkQueue work_queue;

	private boolean maintenance_mode = false;

	public AlarmNotifier(final String root_name,
			final IAlarmRDBHandler rdbHandler,
			final NotificationActionFactory factory, final int threshold)
			throws Exception {
		this.rdb = rdbHandler;
		this.factory = factory;
		messenger = new NotifierCommunicator(this, root_name);
		work_queue = new WorkQueue(threshold);
	}

	/** @return Name of configuration root element */
	public String getRootName() {
		return root_name;
	}

	/** Dump to stdout */
	public void dump() {
		System.out.println("== Alarm Notifier Snapshot ==");
		System.out.println("Work work_queue size: " + work_queue.size());

		// Log memory usage in MB
		final double free = Runtime.getRuntime().freeMemory()
				/ (1024.0 * 1024.0);
		final double total = Runtime.getRuntime().totalMemory()
				/ (1024.0 * 1024.0);
		final double max = Runtime.getRuntime().maxMemory() / (1024.0 * 1024.0);

		final DateFormat format = new SimpleDateFormat(
				JMSLogMessage.DATE_FORMAT);
		System.out
				.format("%s == Alarm Notifer Memory: Max %.2f MB, Free %.2f MB (%.1f %%), total %.2f MB (%.1f %%)\n",
						format.format(new Date()), max, free, 100.0 * free
								/ max, total, 100.0 * total / max);
	}

	/** Connect to JMS */
	public void start() {
		messenger.start();
		Activator.getLogger().log(Level.INFO, "Alarm Notifier started");
	}

	/** Release all resources */
	public void stop() {
		work_queue.flush();
		messenger.stop();
		rdb.close();
		Activator.getLogger().log(Level.INFO, "Alarm Notifier stopped");
	}

	/** @return Action WorkQueue */
	public WorkQueue getWork_queue() {
		return work_queue;
	}

	/**
	 * Read info about {@link AlarmTreeItem} from model.
	 * 
	 * @param path
	 *            , path of the item
	 * @return ItemInfo
	 */
	public ItemInfo getItemInfo(String path) {
		AlarmTreeItem item = rdb.findItem(path);
		return ItemInfo.fromItem(item);
	}

	/**
	 * Handle manual execution of automated actions.
	 * 
	 * @param manualInfo
	 *            , information from JMS message.
	 */
	public void handleManualExecution(GUIExecInfo manualInfo) {
		AlarmTreeItem item = rdb.findItem(manualInfo.getItem_path());
		final ItemInfo info = ItemInfo.fromItem(item);
		if (item.getAutomatedActions() != null) {
			for (AADataStructure aa : item.getAutomatedActions()) {
				if (aa.getTitle().equals(manualInfo.getAa_title())) {
					final ActionID naID = WorkQueue.getActionID(item, aa);
					final INotificationAction newAction = factory
							.getNotificationAction(this, naID, info,
									aa.getDelay(), aa.getDetails());
					work_queue.add(newAction);
				}
			}
		}
	}

	/**
	 * Start automated action for the given PV and its parents.
	 * 
	 * @param pvItem
	 */
	public void handleAlarmUpdate(AlarmTreePV pvItem) {
		PVSnapshot snapshot = PVSnapshot.fromPVItem(pvItem);

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
		INotificationAction action = work_queue.findAction(aaItem, aa);
		if (action != null && action.isSleeping()) {
			// Actions are updated only within the sleeping delay.
			action.updateAlarms(snapshot);
		} else {
			// TODO: AA = one/multiple actions ?? separator ?
			final ItemInfo info = ItemInfo.fromItem(aaItem);
			final ActionID naID = WorkQueue.getActionID(aaItem, aa);
			final INotificationAction newAction = factory
					.getNotificationAction(this, naID, info, aa.getDelay(),
							aa.getDetails());
			if (newAction == null)
				return;
			if (!maintenance_mode
					|| (maintenance_mode && newAction.getActionPriority()
							.equals(EActionPriority.IMPORTANT)))
				work_queue.add(newAction);
			// Update only after adding new action in the queue and running it
			newAction.updateAlarms(snapshot);
		}
	}

	/**
	 * Remove completed actions from {@link WorkQueue}
	 */
	public void actionCompleted(INotificationAction action) {
		work_queue.remove(action);
	}

	/**
	 * Cancel all current running automated actions when a new configuration is
	 * set.
	 */
	public void handleNewAlarmConfiguration() {
		work_queue.interruptAll();
		Activator.getLogger().config("New Alarm Configuration");
	}

	/**
	 * Cancel all current running automated actions when maintenance mode is set
	 * to <code>true</code>
	 */
	public void handleModeUpdate(boolean maintenance_mode) {
		this.maintenance_mode = maintenance_mode;
		if (maintenance_mode)
			work_queue.interruptAll();
		Activator.getLogger().config("Maintenance Mode: " + maintenance_mode);
	}

}
