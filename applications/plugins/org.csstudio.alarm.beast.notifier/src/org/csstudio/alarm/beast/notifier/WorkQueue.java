package org.csstudio.alarm.beast.notifier;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.notifier.model.INotificationAction;

/**
 * Automated actions work queue.
 * Each action is ran in a stand-alone thread.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class WorkQueue {
	
	/** Hash of all Item in config_tree that maps Action ID to INotificationAction[] */
	private HashMap<String, HashMap<ActionID, INotificationAction>> pending_actions = new HashMap<String, HashMap<ActionID, INotificationAction>>();
	
	/** Overflow threshold */
	private final int threshold;
	
	/** Number of running actions */
	private int count = 0;
	
	/** Minimum allowed action priority if overflow occurs */
	private final EActionPriority overflow_level = EActionPriority.MAJOR;
	private boolean cleaned = false;
	
	public WorkQueue(int threshold) {
		this.threshold = threshold;
	}
	
	/** @return Number of currently queued actions on the work queue */
	public int size() {
		return count;
	}
	
	/** @return {@link ActionID} for the specified {@link AlarmTreeItem} and {@link AADataStructure} */
	public static ActionID getActionID(AlarmTreeItem item, AADataStructure aa) {
		return new ActionID(item.getPathName(), aa.getTitle());
	}
	
	/** @return Currently running automated action for the specified {@link AlarmTreeItem} and {@link AADataStructure} */
	public INotificationAction findAction(AlarmTreeItem item, AADataStructure aa) {
		synchronized (pending_actions) 
		{
			HashMap<ActionID, INotificationAction> actions = pending_actions.get(item.getName());
			if (actions == null) return null;
			ActionID naID = getActionID(item, aa);
			return actions.get(naID);
		}
	}
	
	/** Add an automated action to the work queue and runs it */
	public void add(final INotificationAction action) {
		ItemInfo item = action.getItem();
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
		synchronized (pending_actions) 
		{
			HashMap<ActionID, INotificationAction> actions = pending_actions.get(item.getName());
			if (actions == null) {
				actions = new HashMap<ActionID, INotificationAction>();
				pending_actions.put(item.getName(), actions);
			}
			ActionID naID = action.getID();
			if ((isOverflooded()
					&& action.getActionPriority().compareTo(overflow_level) >= 0 && !action
						.isPV()) || !isOverflooded()) {
				if (actions.put(naID, action) == null) {
					count++;
				}
				action.start();
			}
		}
	}

	/** Remove an automated action */
	public void remove(final INotificationAction action) {
		String name = action.getItem().getName();
		synchronized (pending_actions) {
			if (pending_actions.get(name) == null) return;
			if (pending_actions.get(name).remove(action.getID()) != null) {
				count--;
			}
		}
	}
	
	public boolean isOverflooded() {
		return count >= threshold;
	}
	
	/** Interrupt all automated actions */
	public void interruptAll() {
		synchronized (pending_actions) {
			for (Map<ActionID, INotificationAction> actions : pending_actions.values()) {
				for (INotificationAction action : actions.values()) {
					interrupt(action);
				}
			}
		}
	}

	/** Interrupt an automated action */
	public void interrupt(INotificationAction action) {
		action.cancel();
	}

	/** Flush the work queue */
	public void flush() {
		synchronized (pending_actions) {
			for (Map<ActionID, INotificationAction> actions : pending_actions.values()) {
				for (INotificationAction action : actions.values()) {
					// cancel all non-system & low priority actions, force others.
					if (action.getActionPriority().compareTo(overflow_level) < 0 || action.isPV()) {
						interrupt(action);
					} else {
						action.forceExec();
					}
				}
			}
		}
	}

	/** Dump to stdout */
	public void dump() {
		System.out.println("== Work Queue Snapshot ==");
		System.out.println("Work work_queue size: " + size());
		for (Map<ActionID, INotificationAction> actions : pending_actions.values()) {
			for (INotificationAction action : actions.values()) {
				System.out.println(action);
			}
		}
		System.out.println("== Work Queue Snapshot ==");
	}
	
}
