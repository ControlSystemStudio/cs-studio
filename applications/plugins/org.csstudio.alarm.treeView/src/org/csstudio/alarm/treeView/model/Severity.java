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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Represents the severity of an alarm.
 *
 * @author Joerg Rathlev
 */
public enum Severity {
    /**
     * Uninitialized or otherwise unknown state.
     */
    UNKNOWN(false, 0),
	/**
	 * Severity representing no alarm.
	 */
	NO_ALARM(false, 0),

	/**
	 * Severity value for a minor alarm.
	 */
	MINOR(true, 1),

	/**
	 * Severity value for a major alarm.
	 */
	MAJOR(true, 2),

	/**
	 * Severity representing an invalid alarm state.
	 */
	INVALID(true, 3);

    /**
     * Indicates whether this severity is an alarm.
     */
    private boolean _isAlarm;

    /**
     * The level, the higher the more severe.
     */
    private int _severityLevel;


    /**
     * Constructor.
     */
    private Severity(final boolean isAlarm, final int level) {
        _isAlarm = isAlarm;
        _severityLevel = level;
    }

	/**
	 * Converts a string representation of a severity to a severity. Note that
	 * unlike the {@code valueOf(String)} method, this method will never throw
	 * an {@code IllegalArgumentException}. If there is no severity value for
	 * the given string, this method will return {@code NO_ALARM}.
	 *
	 * @param severityString the severity represented as a string value.
	 * @return the severity represented by the given string.
	 */
    @Nonnull
	public static Severity parseSeverity(@Nullable final String severityString) {
	    if (severityString == null) {
	        // TODO jp : shouldn't be possible
	        return NO_ALARM;
	    }
		try {
		    // TODO jp : really? mapping unknown severity to no alarm? Please check
		    return valueOf(severityString);
		} catch (final IllegalArgumentException e) {
            return NO_ALARM;
        }
	}

    /**
     * Returns the severity level, the higher the more sever
     * @return number representing the severity level
     */
    public int getLevel() {
        return _severityLevel;
    }

	/**
	 * Returns {@code true} if this severity is an actual alarm severity.
	 *
	 * @return whether this alarm is an actual alarm severity.
	 */
	public boolean isAlarm() {
		return _isAlarm;
	}

}
