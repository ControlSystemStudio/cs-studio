package org.csstudio.alarm.treeView.model;

/**
 * Represents the severity of an alarm.
 * 
 * @author Joerg Rathlev
 */
public enum Severity {
	
	/**
	 * Severity representing no alarm.
	 */
	NO_ALARM,

	/**
	 * Severity representing an invalid alarm state.
	 */
	INVALID,
	
	/**
	 * Severity value for a minor alarm.
	 */
	MINOR,
	
	/**
	 * Severity value for a major alarm.
	 */
	MAJOR;
	
	
	/**
	 * Converts a string representation of a severity to a severity. Note that
	 * unlike the {@code valueOf(String)} method, this method will never throw
	 * an {@code IllegalArgumentException}. If there is no severity value for
	 * the given string, this method will return {@code NO_ALARM}.
	 * 
	 * @param severityString the severity represented as a string value.
	 * @return the severity represented by the given string.
	 */
	public static Severity parseSeverity(String severityString) {
		if (severityString.equals("MAJOR")) return MAJOR;
		if (severityString.equals("MINOR")) return MINOR;
		if (severityString.equals("INVALID")) return INVALID;
		return NO_ALARM;
	}
	
	
	/**
	 * Returns {@code true} if this severity is an actual alarm severity,
	 * {@code false} if it represents NO_ALARM severity.
	 */
	public boolean isAlarm() {
		return this != NO_ALARM;
	}

}
