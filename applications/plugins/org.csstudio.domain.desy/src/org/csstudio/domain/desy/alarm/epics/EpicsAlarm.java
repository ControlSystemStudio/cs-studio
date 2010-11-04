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
package org.csstudio.domain.desy.alarm.epics;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.alarm.IAlarm;


/**
 * Represents the severity of an alarm.
 *
 * @author Bastian Knerr
 */
public enum EpicsAlarm implements IAlarm {

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


    private static final EpicsAlarm ALARM_WITH_LOWEST_SEVERITY;

    static {
        // init
        EpicsAlarm lowestAlarm = EpicsAlarm.values()[0];
        EpicsAlarmSeverity lowestSev = lowestAlarm.getSeverity();
        // run through
        for (final EpicsAlarm alarm : values()) {
            final EpicsAlarmSeverity severity = alarm.getSeverity();
            if (severity.compareTo(lowestSev) < 0) {
                lowestSev = severity;
                lowestAlarm = alarm;
            }
        }
        ALARM_WITH_LOWEST_SEVERITY = lowestAlarm;
    }

    /**
     * Indicates whether this alarm is a real alarm.
     */
    private boolean _isAlarm;

    /**
     * The severity.
     */
    private EpicsAlarmSeverity _severity;

    /**
     * Constructor.
     */
    private EpicsAlarm(final boolean isAlarm, final int level) {
        _isAlarm = isAlarm;
        _severity = new EpicsAlarmSeverity(level);
    }

    /**
     * Converts a string representation of a severity to a severity. Note that
     * unlike the {@code valueOf(String)} method, this method will never throw
     * an {@code IllegalArgumentException}. If there is no severity value for
     * the given string, this method will return {@code NO_ALARM}.
     *
     * @param alarmString the severity represented as a string value.
     * @return the severity represented by the given string.
     */
    @Nonnull
    public static EpicsAlarm parseAlarm(@CheckForNull final String alarmString) {
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
    public static EpicsAlarm getLowest() {
        return ALARM_WITH_LOWEST_SEVERITY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EpicsAlarmSeverity getSeverity() {
        return _severity;
    }

}
