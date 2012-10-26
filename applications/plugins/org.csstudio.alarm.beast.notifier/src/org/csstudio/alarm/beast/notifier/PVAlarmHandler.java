/*******************************************************************************
* Copyright (c) 2010-2012 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Handler for alarm updates.
 * Receive alarm updates and store information.
 * Used {@link AbstractNotificationAction} to validate if an automated action 
 * can be executed or not regarding to last alarm updates.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class PVAlarmHandler {

	private List<PVSnapshot> snapshots;
	
    public PVAlarmHandler() {
		this.snapshots = new LinkedList<PVSnapshot>();
    }
    
	public void update(PVSnapshot snapshot) {
		snapshots.add(snapshot);
    }
	
	public PVSnapshot getCurrent() {
		return snapshots.get(snapshots.size() - 1);
	}
	
	/** Return <code>true</code> if the automated action has to be executed */
	public boolean validate() {
		PVSnapshot current_snapshot = getCurrent();
		// Alarm recover within the delay => cancel
		if (snapshots.size() > 1 && !current_snapshot.isUnderAlarm()) {
			return false;
		}
		// Do not send mail for alarm-ack
//		if ((current_snapshot.getSeverity().equals(SeverityLevel.MINOR_ACK) 
//						&& current_snapshot.getCurrentSeverity().equals(SeverityLevel.MINOR))
//				|| (current_snapshot.getSeverity().equals(SeverityLevel.MAJOR_ACK) 
//						&& current_snapshot.getCurrentSeverity().equals(SeverityLevel.MAJOR))
//				|| (current_snapshot.getSeverity().equals(SeverityLevel.INVALID_ACK) 
//						&& current_snapshot.getCurrentSeverity().equals(SeverityLevel.INVALID))) {
//			return false;
//		}
		return true;
	}

	
}
