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
 package org.csstudio.alarm.service.declaration;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;


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
	NO_ALARM(false, 1),

	/**
	 * Severity value for a minor alarm.
	 */
	MINOR(true, 2),

	/**
	 * Severity value for a major alarm.
	 */
	MAJOR(true, 3),

	/**
	 * Severity representing an invalid alarm state.
	 */
	INVALID(true, 4);


    private static final Severity LOWEST_SEVERITY;

    static {
        int level = Integer.MAX_VALUE;
        Severity lowestSev = null;
        for (final Severity sev : values()) {
            final int sevLevel = sev.getLevel();
            if (sevLevel < level) {
                level = sevLevel;
                lowestSev = sev;
            }
        }
        LOWEST_SEVERITY = lowestSev;
    }

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
	public static Severity parseSeverity(@CheckForNull final String severityString) {
	    if (severityString == null) {
	        return UNKNOWN;
	    }
		try {
		    return valueOf(severityString);
		} catch (final IllegalArgumentException e) {
            return UNKNOWN;
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

    /**
     * Returns the Severity with the lowest level.
     * @return the Severity with the lowest level
     */
	@Nonnull
    public static Severity getLowest() {
        return LOWEST_SEVERITY;
    }
}
