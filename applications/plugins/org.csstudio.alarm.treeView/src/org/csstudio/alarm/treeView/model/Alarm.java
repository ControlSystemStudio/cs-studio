package org.csstudio.alarm.treeView.model;


/**
 * Represents an alarm.
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 * 
 * @author Joerg Rathlev
 */
public class Alarm implements Comparable<Alarm> {

	private boolean acknowledged;
	private String objectName;
	private Severity severity;


	/**
	 * Creates a new alarm with the given severity.
	 * @param objectName the name of the object on which this alarm occured.
	 * @param severity the severity of the alarm.
	 */
	public Alarm(String objectName, Severity severity) {
		this.objectName = objectName;
		this.severity = severity;

		// by default, assume this alarm is not acknowledged
		this.acknowledged = false;
	}
	
	
	/**
	 * Returns the severity of this alarm.
	 * @return the severity of this alarm.
	 */
	public Severity getSeverity() {
		return severity;
	}

	
	/**
	 * Compares this alarm to another alarm for ordering by severity.
	 * <p>
	 * Note that the order imposed by this method is inconsistent with equals;
	 * two alarms can have the same severity without being equal.
	 * 
	 * @param other the alarm to be compared.
	 * @return a negative integer, zero or a positive integer as this alarm is
	 *         less severe than, equally severe as or more severe than the
	 *         other alarm.
	 */
	public int compareTo(Alarm other) {
		return severity.compareTo(other.severity);
	}
	
	
	/**
	 * Returns the name of the object on which this alarm occured.
	 * @return the name of the object on which this alarm occured.
	 */
	public String getObjectName() {
		return objectName;
	}
	
	
	/**
	 * Returns a string representation of this alarm.
	 * @return a string representation of this alarm.
	 */
	public String toString() {
		return "Alarm[" + objectName + "," + severity + "]";
	}
	
	
	/**
	 * Acknowledges this alarm.
	 */
	public void acknowledge() {
		acknowledged = true;
	}
	
	
	/**
	 * Returns whether this alarm was acknowledged.
	 * @return {@code true} if this alarm was acknowledged, {@code false}
	 *         otherwise.
	 */
	public boolean isAcknowledged() {
		return acknowledged;
	}
}
