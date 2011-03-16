/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.domain.desy.epics.alarm;

import gov.aps.jca.dbr.Severity;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Severities in EPICS as from epics version 3.14.12.rc1 in
 * dbStatic/alarm.h
 *
 * Corresponds to goc.aps.jca.dbr.Severity (which is a sort of java enum pre-build based on
 * int value fields and hashmaps).
 *
 * @author bknerr
 * @since 19.11.2010
 */
public enum EpicsAlarmSeverity {
    /**
     * Uninitialized or otherwise unknown state.
     * Added deliberately to allow for handling changes during epics evolution
     */
    UNKNOWN(0),

    /**
     * Severity representing no alarm.
     */
    NO_ALARM(1),

    /**
     * Severity value for a minor alarm.
     */
    MINOR(3),

    /**
     * Severity value for a major alarm.
     */
    MAJOR(4),

    /**
     * Severity representing an invalid alarm state.
     */
    INVALID(5);


    private static final EpicsAlarmSeverity LOWEST_SEVERITY;

    static {
        // init
        EpicsAlarmSeverity lowestSev = EpicsAlarmSeverity.values()[0];
        // run through
        for (final EpicsAlarmSeverity sev : values()) {
            if (sev.compareSeverityTo(lowestSev) < 0) {
                lowestSev = sev;
            }
        }
        LOWEST_SEVERITY = lowestSev;
    }

    private final int _severity;


    /**
     * Constructor.
     */
    private EpicsAlarmSeverity(final int sev) {
        _severity = sev;
    }

    /**
     * Converts a string representation of a severity to an EpicsAlarmSeverity. Note that
     * unlike the {@link EpicsAlarmSeverity#valueOf(String)} method, this method will never throw
     * an {@link IllegalArgumentException}. If there is no severity value for
     * the given string, this method will return {@link EpicsAlarmSeverity#UNKNOWN}.
     *
     * @param alarmString the severity represented as a string value.
     * @return the severity represented by the given string.
     */
    @Nonnull
    public static EpicsAlarmSeverity parseSeverity(@CheckForNull final String alarmString) {
        if (alarmString == null) {
            return UNKNOWN;
        }
        try {
            return valueOf(alarmString);
        } catch (final IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    /**
     * Returns the Severity with the lowest level.
     * @return the Severity with the lowest level
     */
    @Nonnull
    public static EpicsAlarmSeverity getLowest() {
        return LOWEST_SEVERITY;
    }


    /**
     * Severities are comparable to each other.
     * @param other the severity to compare to
     * @return 0 if equal, -1 on this being smaller, 1 on this being greater
     */
    public int compareSeverityTo(@Nonnull final EpicsAlarmSeverity other) {

        return _severity < other._severity ? -1
                                           : _severity > other._severity ? 1
                                                                         : 0;
    }

    /**
     * Converts an jca severity into a CSS epics alarm severity.
     * @param severity the incoming severity
     * @return the outgoing severity
     */
    @Nonnull
    public EpicsAlarmSeverity valueOf(@Nonnull final Severity severity) {
        return parseSeverity(severity.getName());
    }
}
