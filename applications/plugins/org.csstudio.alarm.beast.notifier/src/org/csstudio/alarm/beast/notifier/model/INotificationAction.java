package org.csstudio.alarm.beast.notifier.model;

import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.notifier.ActionID;
import org.csstudio.alarm.beast.notifier.AlarmNotifier;
import org.csstudio.alarm.beast.notifier.EActionPriority;
import org.csstudio.alarm.beast.notifier.EActionStatus;
import org.csstudio.alarm.beast.notifier.ItemInfo;
import org.csstudio.alarm.beast.notifier.PVSnapshot;
import org.csstudio.alarm.beast.notifier.actions.NotificationActionListener;

/**
 * Interface for automated action.
 * Define standard methods to implement.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public interface INotificationAction extends Runnable {

	/**  Start the action Thread */
	public void start();
	
	/**
	 * Initialize the action.
	 * @param notifier
	 * @param id
	 * @param item
	 * @param delay
	 * @param details
	 */
	public void init(AlarmNotifier notifier, ActionID id, ItemInfo item, int delay, String details);
	public void init(AlarmNotifier notifier, ActionID id, ItemInfo item, int delay, String details, IActionValidator validator);
	
	/**
	 * Add a listener to the action.
	 * @param listener
	 */
	public void addListener(NotificationActionListener listener);
	
	/**  
	 * Method to be implemented with action specific code.
	 * Called by action thread after the delay if {@link EActionStatus} is still OK.
	 */
	public void execute();
	
	/**  Notify listener that the action has finished executing.  */
	public void actionCompleted();
	
	/**
	 * Update the action with a new alarm.
	 * @param pv, {@link PVSnapshot} of the current PV state.
	 */
	public void updateAlarms(PVSnapshot pv);
	
	/**
	 * Update the action current status.
	 * @param status
	 */
	public void updateStatus(EActionStatus status);
	
	/** @return <code>true</code> if the action is sleeping */
	public boolean isSleeping();
	
	/**
	 * Cancel the action by interrupting the associated thread.
	 */
	public void cancel();
	
	/**
	 * Interrupt delay sleep and force action execution.
	 */
	public void forceExec();
	
	/** Dump action to stdout (debug purpose) */
	public void dump();
	
	public ActionID getID();
	public ItemInfo getItem();
	public EActionStatus getActionStatus();
	public EActionPriority getActionPriority();
	
	/** Return <code>true</code> if the action is linked to an {@link AlarmTreePV} */
	public boolean isPV();
}
