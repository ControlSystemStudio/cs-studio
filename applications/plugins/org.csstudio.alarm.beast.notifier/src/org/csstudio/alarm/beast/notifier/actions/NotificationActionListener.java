package org.csstudio.alarm.beast.notifier.actions;

import org.csstudio.alarm.beast.notifier.model.INotificationAction;


/** 
 *  Listener for {@link INotificationAction} changes.
 *  @author Fred Arnaud, Sopra Group.
 */
public interface NotificationActionListener {

	/** Fire when an automated action has finished running */
	public void actionCompleted(INotificationAction action);
}
