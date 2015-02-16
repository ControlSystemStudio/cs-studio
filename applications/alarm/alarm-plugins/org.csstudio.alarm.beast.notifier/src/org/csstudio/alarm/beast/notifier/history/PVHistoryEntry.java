/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.history;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.notifier.PVSnapshot;

/**
 * History entry of an alarm update identified by its {@link PVSnapshot}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 * 
 */
public class PVHistoryEntry {

	private final String name, path;
	private SeverityLevel currentSeverity, severity;
	private boolean acknowledged = false;
	private boolean recovredWithinDelay = false;

	public static PVHistoryEntry fromSnapshot(PVSnapshot snapshot) {
		PVHistoryEntry pvhe = new PVHistoryEntry(snapshot.getName(),
				snapshot.getPath());
		pvhe.update(snapshot);
		return pvhe;
	}

	public PVHistoryEntry(final String name, final String path) {
		this.name = name;
		this.path = path;
	}

	public void update(PVSnapshot snapshot) {
		if (this.severity != null
				&& this.severity.name().endsWith("ACK")
				&& this.severity.name().startsWith(snapshot.getSeverity().name())
				&& !snapshot.getSeverity().name().endsWith("ACK"))
			this.acknowledged = false;
		if (!acknowledged && snapshot.isAcknowledge())
			this.acknowledged = true;
		this.currentSeverity = snapshot.getCurrentSeverity();
		this.severity = snapshot.getSeverity();
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public SeverityLevel getCurrentSeverity() {
		return currentSeverity;
	}

	public SeverityLevel getSeverity() {
		return severity;
	}

	public boolean isAcknowledged() {
		return acknowledged;
	}

	public boolean hasRecovredWithinDelay() {
		return recovredWithinDelay;
	}

	public void setRecovredWithinDelay(boolean recovredWithinDelay) {
		this.recovredWithinDelay = recovredWithinDelay;
	}

	@Override
	public String toString() {
		return "PVHistoryEntry [currentSeverity=" + currentSeverity
				+ ", severity=" + severity + ", acknowledged=" + acknowledged
				+ "]";
	}

}
