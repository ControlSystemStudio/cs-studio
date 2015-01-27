/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import org.epics.vtype.AlarmSeverity;

/** A severity level.
 *  <p>
 *  Defined as an enum with known instances OK, MINOR, .. to allow quick
 *  comparisons via "==".
 *  <p>
 *  The enum's <code>ordinal()</code> provides the severity level
 *  for an alarm condition as used by user interface tools to
 *  arrange alarms for display purposes.
 *  Higher ordinal means more severe.
 *  For example, MAJOR is more severe than MINOR.
 *  MAJOR is also more severe than MAJOR_ACK as far as <i>different PVs</i>
 *  are concerned:
 *  If different PVs in a group are in MINOR, MAJOR and MAJOR_ACK states,
 *  the summary for the group would indicate MAJOR to reflect the highest alarm
 *  in the group.
 *  <p>
 *  The 'ordinal' order of severity levels is therefore:
 *  0-OK,
 *  1-MINOR_ACK,
 *  2-MAJOR_ACK,
 *  3-INVALID_ACK,
 *  4-UNDEFINED_ACK,
 *  5-MINOR,
 *  6-MAJOR,
 *  7-INVALID,
 *  8-UNDEFINED.
 *  <p>
 *  Inside the alarm server, a slightly different order is used to handle
 *  alarm updates: If a PV is in MINOR alarm and receives a MAJOR value,
 *  it will of course update to MAJOR alarm state. But if it's in MAJOR_ACK
 *  and now receives new values with MINOR or MAJOR severity, the existing
 *  acknowledgment for MAJOR means that it stays in MAJOR_ACK. So for the
 *  server MAJOR_ACK takes precedence over MAJOR or MINOR.
 *  <p>
 *  The order for updating the alarm level of a PV is therefore:
 *  0-OK,
 *  1-MINOR,
 *  2-MINOR_ACK,
 *  3-MAJOR,
 *  4-MAJOR_ACK,
 *  5-INVALID,
 *  6-INVALID_ACK,
 *  7-UNDEFINED,
 *  8-UNDEFINED_ACK.
 *   
 *  @author Kay Kasemir
 */
public enum SeverityLevel
{    
    /** OK/NO_ALARM/normal/good */
    OK(AlarmSeverity.NONE, false, Messages.SeverityLevel_OK, Preferences.COLOR_OK, 0),

    /** Acknowledged minor issue */
    MINOR_ACK(AlarmSeverity.MINOR, false, Messages.SeverityLevel_MINOR_ACK, Preferences.COLOR_MINOR_ACK, 2),

    /** Acknowledged major issue */
    MAJOR_ACK(AlarmSeverity.MAJOR, false, Messages.SeverityLevel_MAJOR_ACK, Preferences.COLOR_MAJOR_ACK, 4),

    /** Acknowledged invalid condition */
    INVALID_ACK(AlarmSeverity.INVALID, false, Messages.SeverityLevel_INVALID_ACK, Preferences.COLOR_INVALID_ACK, 6),

    /** Acknowledged undefined condition */
    UNDEFINED_ACK(AlarmSeverity.UNDEFINED, false, Messages.SeverityLevel_UNDEFINED_ACK, Preferences.COLOR_INVALID_ACK, 8),
    
    /** Minor issue */
    MINOR(AlarmSeverity.MINOR, true, Messages.SeverityLevel_MINOR, Preferences.COLOR_MINOR, 1),

    /** Major issue */
    MAJOR(AlarmSeverity.MAJOR, true, Messages.SeverityLevel_MAJOR, Preferences.COLOR_MAJOR, 3),

    /** Invalid condition, potentially very bad */
    INVALID(AlarmSeverity.INVALID, true, Messages.SeverityLevel_INVALID, Preferences.COLOR_INVALID, 5),

    /** Unknown states, potentially very bad */
    UNDEFINED(AlarmSeverity.UNDEFINED, true, Messages.SeverityLevel_UNDEFINED, Preferences.COLOR_INVALID, 7);
    
    /** Underlying alarm severity */
    final private AlarmSeverity severity;
    
    /** Active alarm (not OK, not acknowledged?) */
    final private boolean active;

    /** End-user display name */
    final private String display_name;
    
    /** Color.
     *  Would be neat to use SWT RGB here, but don't want to add SWT
     *  dependency to plugin that's otherwise non-GUI.
     */
    final private int rgb[];

    /** Priority used for alarm state updates in the alarm server */
    final private int alarm_update_priority;
    
    
    /** Initialize severity level
     *  @param severity {@link AlarmSeverity}
     *  @param active <code>true</code> for active alarm severity,
     *          <code>false</code> for acknowledged or OK state
     *  @param display_name Name
     *  @param pref Name of preference for RGB values
     *  @param alarm_update_priority Priority used inside the server to
     *         update alarm severity of a PV.
     *  @param level Level (higher = more severe)
     */
    SeverityLevel(final AlarmSeverity severity,
            final boolean active,
            final String display_name,
            final String pref,
            final int alarm_update_priority)
    {
        this.severity = severity;
        this.active = active;
        this.display_name = display_name;
        this.rgb = Preferences.getColor(pref);
        this.alarm_update_priority = alarm_update_priority;
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

    /** @return (Possibly localized) Name of the severity level for users.
     *  @see #name() for the fixed name that API might use
     */
    public String getDisplayName()
    {
        return display_name;
    }

    /** @return Suggested RGB color representation */
    public int getRed()
    {
        return rgb[0];
    }

    /** @return Suggested RGB color representation */
    public int getGreen()
    {
        return rgb[1];
    }

    /** @return Suggested RGB color representation */
    public int getBlue()
    {
        return rgb[2];
    }

    /** @return Priority used inside the server to update alarm severity
     *          of a PV.
     */
    public int getAlarmUpdatePriority()
    {
        return alarm_update_priority;
    }
    
    /** Parse severity level from string
     *  @param severity String to parse
     *  @return SeverityLevel
     */
    public static SeverityLevel parse(final String severity)
    {
        // Assume that 'OK' is the most common case, so handle first;
        // empty severity assumed to be OK
        if (severity == null  ||
            severity.length() <= 0  ||
            "NO_ALARM".equalsIgnoreCase(severity)) //$NON-NLS-1$
            return OK;
        // Most other cases
        for (SeverityLevel level : values())
            if (level.name().equalsIgnoreCase(severity))
                return level;
        // Handle all unknown severities as INVALID, i.e. the worst case
        return INVALID;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "SeverityLevel " +  name() +
            " (" + display_name + ",  " + ordinal() + ")";
    }
}
