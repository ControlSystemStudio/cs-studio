package org.csstudio.alarm.beast.notifier.test;

import org.csstudio.alarm.beast.notifier.actions.EmailNotificationAction;

/**
 * Mock for {@link EmailNotificationAction}.
 * Prevent action execution by overwriting execute() method.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class MockEMailNotificationAction extends EmailNotificationAction {

	@Override
	public void execute() {
		fill();
		System.out.println(">> " + toString() + " executed <<");
		dump();
	}
	
}
