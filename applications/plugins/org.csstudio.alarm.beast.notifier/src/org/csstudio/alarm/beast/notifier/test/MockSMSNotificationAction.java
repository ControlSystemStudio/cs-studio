package org.csstudio.alarm.beast.notifier.test;

import org.csstudio.alarm.beast.notifier.EActionPriority;
import org.csstudio.alarm.beast.notifier.actions.SMSNotificationAction;

/**
 * Mock for {@link SMSNotificationAction}.
 * Prevent action execution by overwriting execute() method.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class MockSMSNotificationAction extends SMSNotificationAction {

	@Override
	public void execute() {
		System.out.println(toString() + " EXECUTED");
	}

	public void setPriority(EActionPriority priority) {
		this.priority = priority;
	}
}
