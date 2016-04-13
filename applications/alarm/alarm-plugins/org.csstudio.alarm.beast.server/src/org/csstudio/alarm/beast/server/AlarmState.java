/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import java.time.Instant;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.TimestampHelper;

/** Alarm state combines an alarm severity with its message info,
 *  value that triggered this state, time when it happened.
 *  <p>
 *  To be on the safe side, this is a non-mutable class.
 *  For performance improvements, it could be made mutable,
 *  but so far there's no hard evidence that this class matters.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AlarmState
{
    final private SeverityLevel severity;
    final private String message;
    final private String value;
    final private Instant time;

    /** Initialize
     *  @param severity Initial alarm severity
     *  @param message   .. and message
     */
    public AlarmState(final SeverityLevel severity, final String message,
            final String value, final Instant time)
    {
        if (severity == null || time == null)
            throw new Error("Null arguments");
        this.severity = severity;
        this.message = message == null ? "" : message;
        this.value = value == null ? "" : value;
        this.time = time;
    }

    /** Create alarm state that's all OK
     *  @param value Value to use for the 'clear' state
     *  @param time Time to use for the 'OK' state
     *  @return AlarmState
     */
    public static AlarmState createClearState(final String value, final Instant time)
    {
        return new AlarmState(SeverityLevel.OK,
                SeverityLevel.OK.getDisplayName(),
                value, time);
    }

    /** Create alarm state that's all OK with the current time stamp
     *  @return AlarmState
     */
    public static AlarmState createClearState(final String value)
    {
        return createClearState(value, Instant.now());
    }

    /** Create an alarm state similar to current one but with updated severity
     *  @param new_severity Severity to use for created alarm state
     *  @return AlarmState
     */
    public AlarmState createUpdatedState(final SeverityLevel new_severity)
    {
        return new AlarmState(new_severity, message, value, time);
    }

    /** Change 'active' alarm severity into 'acknowledged' type, relaxing
     *  to the severity of the current state in case that's already lower
     *  than the original alarm state.
     *  @param current_state
     */
    public AlarmState createAcknowledged(final AlarmState current_state)
    {
        if (current_state != null  &&
            current_state.getSeverity().ordinal() < severity.ordinal())
        {
            switch (current_state.getSeverity())
            {
            case UNDEFINED:
                return new AlarmState(SeverityLevel.UNDEFINED_ACK, current_state.getMessage(), current_state.getValue(), current_state.getTime());
            case INVALID:
                return new AlarmState(SeverityLevel.INVALID_ACK, current_state.getMessage(), current_state.getValue(), current_state.getTime());
            case MAJOR:
                return new AlarmState(SeverityLevel.MAJOR_ACK, current_state.getMessage(), current_state.getValue(), current_state.getTime());
            case MINOR:
                return new AlarmState(SeverityLevel.MINOR_ACK, current_state.getMessage(), current_state.getValue(), current_state.getTime());
            default:
                // other severities stay as they are
                return current_state;
            }
        }
        // Else: Use the alarm severity as the one to ack'
        switch (severity)
        {
        case UNDEFINED:
            return createUpdatedState(SeverityLevel.UNDEFINED_ACK);
        case INVALID:
            return createUpdatedState(SeverityLevel.INVALID_ACK);
        case MAJOR:
            return createUpdatedState(SeverityLevel.MAJOR_ACK);
        case MINOR:
            return createUpdatedState(SeverityLevel.MINOR_ACK);
        default:
            // other severities stay as they are
            return this;
        }
    }

    /** Change acknowledged alarm severity into active type */
    public AlarmState createUnacknowledged()
    {
        switch (severity)
        {
        case UNDEFINED_ACK:
            return createUpdatedState(SeverityLevel.UNDEFINED);
        case INVALID_ACK:
            return createUpdatedState(SeverityLevel.INVALID);
        case MAJOR_ACK:
            return createUpdatedState(SeverityLevel.MAJOR);
        case MINOR_ACK:
            return createUpdatedState(SeverityLevel.MINOR);
        default:
            // other severities stay as they are
            return this;
        }
    }

    /** @return <code>true</code> if this state has higher alarm update
     *          priority than other state
     * @param other State to compare
     * @see SeverityLevel#getAlarmUpdatePriority()
     */
    public boolean hasHigherUpdatePriority(final AlarmState other)
    {
        return severity.getAlarmUpdatePriority()
                > other.severity.getAlarmUpdatePriority();
    }

    /** @return Severity level of alarm */
    public SeverityLevel getSeverity()
    {
        return severity;
    }

    /** @return Alarm message */
    public String getMessage()
    {
        return message;
    }

    /** @return Value that triggered the alarm state */
    public String getValue()
    {
        return value;
    }

    /** @return Time stamp */
    public Instant getTime()
    {
        return time;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj)
    {
        if (! (obj instanceof AlarmState))
            return false;
        final AlarmState other = (AlarmState) obj;
        return other.severity == severity  &&
               other.message.equals(message) &&
               other.value.equals(value) &&
               other.time.equals(time);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = message.hashCode();
        result = prime * result + severity.hashCode();
        result = prime * result + time.hashCode();
        result = prime * result + value.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append(severity.getDisplayName()).append("/").append(message);
        buf.append("(").append(value).append("), ").append(TimestampHelper.format(time));
        return buf.toString();
    }
}
