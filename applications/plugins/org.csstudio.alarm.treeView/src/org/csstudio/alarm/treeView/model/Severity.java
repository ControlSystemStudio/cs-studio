package org.csstudio.alarm.treeView.model;

/**
 * Represents the severity of an alarm.
 * 
 * @author Joerg Rathlev
 */
public final class Severity implements Comparable<Severity> {
	
	/**
	 * Severity representing no alarm.
	 */
	public static final Severity NO_ALARM = new Severity("NO_ALARM");
	
	/**
	 * The String representation of this severity.
	 */
	private String stringRepresentation;
	
	/**
	 * The integer value of this severity.
	 */
	private int intValue;
	
	
	/**
	 * Creates a new severity object from its string representation.
	 * @param stringRepresentation the string representation. {@code null} is
	 *        interpreted as &quot;NO_ALARM&quot;.
	 */
	public Severity(String stringRepresentation) {
		this.stringRepresentation =
			stringRepresentation == null ? "NO_ALARM" : stringRepresentation;
		this.intValue = stringToIntValue(this.stringRepresentation);
	}
	
	
	/**
	 * Converts a string representation of a severity to its integer value.
	 * @param severityString the severity represented as a string value.
	 * @return the severity represented as an integer value.
	 */
	private static int stringToIntValue(String severityString) {
		if (severityString.equals("MAJOR")) return 7;
		if (severityString.equals("MINOR")) return 4;
		if (severityString.equals("INVALID")) return 2;
		return 0;
	}
	
	
	/**
	 * Returns the integer value of this severity.
	 * @return the integer value of this severity.
	 * @deprecated This should probably not be used directly. Instead of getting
	 * the integer value and comparing that, you can compare the severity
	 * objects directly.
	 */
	@Deprecated
	public int toIntValue() {
		return intValue;
	}
	
	
	/**
	 * Returns the string representation of this severity.
	 * @return the string representation of this severity.
	 */
	@Override
	public String toString() {
		return stringRepresentation;
	}
	
	
	/**
	 * Compares this severity for equality with the specified object. Returns
	 * {@code true} if the specified object is a Severity object representing
	 * the same severity value as this severity.
	 * 
	 * @param o the object to be compared.
	 * @return {@code true} if this severity is equal to {@code o},
	 *         {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Severity) {
			return ((Severity) o).intValue == this.intValue;
		}
		return false;
	}
	
	
	@Override
	public int hashCode() {
		return intValue;
	}

	
	/**
	 * Compares this severity with the specified severity for order. Returns
	 * a negative integer, zero or a positive integer as this severity is less
	 * severe than, equal to, or more severe than the specified severity.
	 * 
	 * @param s the severity to be compared.
	 * @return a negative integer, zero or a positive integer as this severity
	 *         is less severe than, equal to, or more severe than the specified
	 *         severity.
	 */
	public int compareTo(Severity s) {
		return this.intValue - s.intValue;
	}


	/**
	 * Returns {@code true} if this severity is an actual alarm severity,
	 * {@code false} if it represents NO_ALARM severity.
	 */
	public boolean isAlarm() {
		return intValue != 0;
	}

}
