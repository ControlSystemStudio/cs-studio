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
