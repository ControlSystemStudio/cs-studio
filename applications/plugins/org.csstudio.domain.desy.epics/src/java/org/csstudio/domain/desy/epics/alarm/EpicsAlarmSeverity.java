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

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

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
    MINOR(2),

    /**
     * Severity value for a major alarm.
     */
    MAJOR(3),

    /**
     * Severity representing an invalid alarm state.
     */
    INVALID(4);


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

    /**
     * List of severities by integer code, the index in the list corresponds to the JCA code.
     * @see Severity
     */
    private static List<EpicsAlarmSeverity> SEVS_BY_CODE =
        Lists.newArrayList(NO_ALARM, MINOR, MAJOR, INVALID);

    /**
     * The level of the severity for comparison.
     * The higher the value, the more terrible the alarm.
     * (their values are equal the 'jca code' just by conincidence).
     */
    private final int _sevLevel;


    /**
     * Constructor.
     */
    private EpicsAlarmSeverity(final int sevLevel) {
        _sevLevel = sevLevel;
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

        return _sevLevel < other._sevLevel ? -1
                                           : _sevLevel > other._sevLevel ? 1
                                                                         : 0;
    }

    /**
     * Returns the DESY severity according to code taken from {@link Severity}.
     *
     * If the code is not known (not in {0,1,2,3}) {@link EpicsAlarmSeverity#UNKNOWN} is
     * returned.
     *
     * @param code the JCA integer code
     * @return the DESY alarm severity
     */
    @Nonnull
    public static EpicsAlarmSeverity valueOf(@Nonnull final Severity severity) {
        return byJCACode(severity.getValue());
    }

    @Nonnull
    private static EpicsAlarmSeverity byJCACode(final int code) {
        if (code < 0 || code >= SEVS_BY_CODE.size()) {
            return UNKNOWN;
        }
        return SEVS_BY_CODE.get(code);
    }
}
