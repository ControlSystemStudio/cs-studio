/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import org.diirt.vtype.AlarmSeverity;

/** A severity level as defined in <code>org.csstudio.alarm.beast.SeverityLevel</code>.
 *  <p>
 *  Defined as an enum with known instances OK, MINOR..<br>
 *  Recreated here so that widgets will be able to differentiate between Alarm and AcknowledgedAlarm states.
 *
 *  @author Boris Versic
 */
public enum BeastAlarmSeverityLevel {
    /** OK/NO_ALARM/normal/good */
    OK(AlarmSeverity.NONE, false, "OK"),

    /** Acknowledged minor issue */
    MINOR_ACK(AlarmSeverity.MINOR, false, "minor-ack'ed"),

    /** Acknowledged major issue */
    MAJOR_ACK(AlarmSeverity.MAJOR, false, "major-ack'ed"),

    /** Acknowledged invalid condition */
    INVALID_ACK(AlarmSeverity.INVALID, false, "invalid-ack'ed"),

    /** Acknowledged undefined condition */
    UNDEFINED_ACK(AlarmSeverity.UNDEFINED, false, "undefined-ack'ed"),

    /** Minor issue */
    MINOR(AlarmSeverity.MINOR, true, "MINOR"),

    /** Major issue */
    MAJOR(AlarmSeverity.MAJOR, true, "MAJOR"),

    /** Invalid condition, potentially very bad */
    INVALID(AlarmSeverity.INVALID, true, "INVALID"),

    /** Unknown states, potentially very bad */
    UNDEFINED(AlarmSeverity.UNDEFINED, true, "UNDEFINED");

    /** Underlying alarm severity */
    final private AlarmSeverity severity;

    /** Active alarm (not OK, not acknowledged?) */
    final private boolean active;

    /** End-user display name */
    final private String display_name;


    /** Initialize severity level
     *  @param severity {@link AlarmSeverity}
     *  @param active <code>true</code> for active alarm severity,
     *          <code>false</code> for acknowledged or OK state
     *  @param display_name Name
     */
    BeastAlarmSeverityLevel(final AlarmSeverity severity,
            final boolean active,
            final String display_name)
    {
        this.severity = severity;
        this.active = active;
        this.display_name = display_name;
    }

    /** @return {@link AlarmSeverity} */
    public AlarmSeverity getAlarmSeverity()
    {
        return severity;
    }

    /** @return <code>true</code> if severity indicates an active alarm,
     *          <code>false</code> for acknowledged or OK state
     */
    public boolean isActive()
    {
        return active;
    }

    /** @return Name of the severity level for users.
     */
    public String getDisplayName()
    {
        return display_name;
    }

    /** Parse severity level from string
     *  @param severity String to parse
     *  @return SeverityLevel
     */
    public static BeastAlarmSeverityLevel parse(final String severity)
    {
        // Assume that 'OK' is the most common case, so handle first;
        // empty severity assumed to be OK
        if (severity == null  ||
                severity.length() <= 0  ||
                "NO_ALARM".equalsIgnoreCase(severity)) //$NON-NLS-1$
            return OK;

        // Most other cases
        for (BeastAlarmSeverityLevel level : values())
            if (level.name().equalsIgnoreCase(severity))
                return level;

        // Cover cases where the 'display name' was received
        for (BeastAlarmSeverityLevel level : values())
            if (level.getDisplayName().equalsIgnoreCase(severity))
                return level;

        // Handle all unknown severities as INVALID, i.e. the worst case
        return INVALID;
    }
}
