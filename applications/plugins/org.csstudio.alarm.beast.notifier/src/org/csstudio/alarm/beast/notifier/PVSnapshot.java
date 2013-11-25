/*******************************************************************************
* Copyright (c) 2010-2013 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.logging.JMSLogMessage;
import org.epics.util.time.Timestamp;

/**
 * Snapshot of an {@link AlarmTreePV}.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class PVSnapshot {

	/** Parser for received time stamp */
    final protected static SimpleDateFormat date_format = new SimpleDateFormat(JMSLogMessage.DATE_FORMAT);
    
    /** Pattern for important priority */
    final protected static Pattern IMPPattern = Pattern.compile("^\\ *\\*?\\!.*");
	
	final int id;
	final private String name, path, description;
	final private boolean enabled, latching;
	
    final private SeverityLevel current_severity, severity;
    final private String current_message, message, value;
    final private Timestamp timestamp;
	
    /**
     * Create {@link PVSnapshot} from an {@link AlarmTreePV}
     * @param pv
     * @return
     */
	public static PVSnapshot fromPVItem(final AlarmTreePV pv) 
	{
		final int id = pv.getID();
		final String name = pv.getName();
		final String path = pv.getPathName();
		final String description = pv.getDescription();
		final boolean enabled = pv.isEnabled();
		final boolean latching = pv.isLatching();

		final SeverityLevel severity = pv.getSeverity();
		final String status = pv.getMessage();
		final SeverityLevel current_severity = pv.getCurrentSeverity();
		final String current_message = pv.getCurrentMessage();
		final String value = pv.getValue();
		final Timestamp timestamp = pv.getTimestamp();
		
		return new PVSnapshot(id, name, path, description, enabled, latching,
				current_severity, current_message, severity, status, value,
				timestamp);
	}
    
    public PVSnapshot(final int id,
			final String name,
			final String path,
            final String description,
            final boolean enabled,
            final boolean latching,
            final SeverityLevel current_severity,
            final String current_message,
            final SeverityLevel severity, 
            final String message,
            final String value,
            final Timestamp timestamp)
    {
		this.id = id;
		this.name = name;
		this.path = path;
		this.description = description;
		this.enabled = enabled;
		this.latching = latching;
		this.current_severity = current_severity;
		this.current_message = current_message;
		this.severity = severity;
		this.message = message;
		this.value = value;
		this.timestamp = timestamp;
    }
    
    /** Return <code>true</code> if PV has an important priority defined in description */
    public boolean isImportant() {
		if (description == null || "".equals(description))
			return false;
		Matcher IMPMatcher = IMPPattern.matcher(description);
		if (IMPMatcher.matches())
			return true;
		return false;
	}
    
	/** Return <code>true</code> if the alarm severity is NOT OK */
	public boolean isUnderAlarm() {
		return !this.current_severity.equals(SeverityLevel.OK);
	}

	/**
	 * Return <code>true</code> if this snapshot is the result of an acknowledge
	 */
	public boolean isAcknowledge() {
		if (!latching)
			return false;
		return this.severity.name().startsWith(current_severity.name())
				&& (this.severity.name().endsWith("_ACK") || severity.equals(SeverityLevel.OK));
	}

	/** Return <code>true</code> if the alarm has been acknowledged */
	public boolean hasBeenAcknowledged() {
		if (!latching)
			return false;
		return this.severity.name().endsWith("_ACK") || severity.equals(SeverityLevel.OK);
	}

	/** @return PV ID */
	public int getId() {
		return id;
	}

	/** @return PV name */
	public String getName() {
		return name;
	}

	/** @return PV path */
	public String getPath() {
		return path;
	}

	/** @return PV description */
	public String getDescription() {
		return description;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isLatching() {
		return latching;
	}

	/** @return Current PV severity */
	public SeverityLevel getCurrentSeverity() {
		return current_severity;
	}

	/** @return Current PV message */
	public String getCurrentMessage() {
		return current_message;
	}

	/** @return Alarm severity */
	public SeverityLevel getSeverity() {
		return severity;
	}

	/** @return Alarm message */
	public String getMessage() {
		return message;
	}

	/** @return Alarm value */
	public String getValue() {
		return value;
	}

	/** @return Time of alarm */
	public Timestamp getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "PVSnapshot [id=" + id + ", name=" + name + ", path=" + path
				+ ", description=" + description + ", enabled=" + enabled
				+ ", latching=" + latching + ", current_severity="
				+ current_severity + ", severity=" + severity
				+ ", current_message=" + current_message + ", message="
				+ message + ", value=" + value + ", timestamp=" + timestamp
				+ "]";
	}

}
