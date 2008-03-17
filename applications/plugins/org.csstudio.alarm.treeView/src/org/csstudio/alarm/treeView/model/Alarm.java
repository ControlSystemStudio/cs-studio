/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.treeView.model;


/**
 * Represents an alarm.
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 * 
 * @author Joerg Rathlev
 */
public class Alarm implements Comparable<Alarm> {

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
}
