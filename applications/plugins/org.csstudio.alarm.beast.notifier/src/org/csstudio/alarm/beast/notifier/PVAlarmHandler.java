package org.csstudio.alarm.beast.notifier;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.notifier.model.AbstractNotificationAction;

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
		if (snapshots.size() > 1 && !current_snapshot.isUnderAlarm()) {
			return false;
		}
		if (current_snapshot.getSeverity().equals(SeverityLevel.MINOR_ACK)
				|| current_snapshot.getSeverity().equals(SeverityLevel.MAJOR_ACK)) {
			return false;
		}
		return true;
	}

	
}
