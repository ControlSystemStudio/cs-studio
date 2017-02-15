/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.client;

import java.time.Duration;
import java.time.Instant;

import org.csstudio.alarm.beast.Messages;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.TimestampHelper;
import org.csstudio.apputil.time.SecondsParser;
import org.eclipse.osgi.util.NLS;
import org.diirt.util.time.TimeDuration;

/** Alarm tree 'leaf' that has time of alarm, tool tip info,
 *  CSS PV name
 *  @author Kay Kasemir
 */
public class AlarmTreeLeaf extends AlarmTreeItem
{
    private static final long serialVersionUID = -2107556902460644540L;

    /** Description of alarm */
    private volatile String description = ""; //$NON-NLS-1$

    /** Timestamp of last alarm update */
    private volatile transient Instant timestamp = null;

    /** Initialize
     *  @param parent Parent item
     *  @param name Name of element
     *  @param id RDB ID
     *  @return Item
     */
    public AlarmTreeLeaf(final AlarmTreeItem parent, final String name, final int id)
    {
        super(parent, name, id);
    }

    /** @param description New description */
    public void setDescription(final String description)
    {
        this.description = description == null ? "" : description; //$NON-NLS-1$
    }

    /** @return Alarm description */
    public String getDescription()
    {
        return description;
    }

    /** @return Verbose, multi-line description of the current alarm
     *          meant for elog entry or usage as drag/drop text
     */
    public String getVerboseDescription()
    {
        return NLS.bind(Messages.VerboseAlarmDescriptionFmt,
                new Object[]
                {
                    getDescription(),
                    getName(),
                    getTimestampText(),
                    getDuration(),
                    getSeverity().getDisplayName(),
                    getMessage()
                });
    }

    /** @return Time stamp of last status/severity update */
    public Instant getTimestamp()
    {
        return timestamp;
    }

    /** @return Duration of current alarm state or empty text */
    public String getDuration()
    {
        final Instant safe_copy = timestamp;
        if (safe_copy == null)
            return ""; //$NON-NLS-1$
        final Duration duration = Duration.between(safe_copy, Instant.now());
        if (duration.isNegative())
            return ""; //$NON-NLS-1$
        return SecondsParser.formatSeconds(TimeDuration.toSecondsDouble(duration));
    }

    /** @return Time stamp of last status/severity update as text */
    public String getTimestampText()
    {
        final Instant safe_copy = timestamp;
        if (safe_copy == null)
            return ""; //$NON-NLS-1$
        return TimestampHelper.format(safe_copy);
    }

    /** Update status/message/time stamp and maximize
     *  severities of parent entries.
     *
     *  Ends up maximizing severity of parent chain,
     *  so caller must lock root.
     *
     *  @param current_severity Current severity of PV
     *  @param severity Alarm severity
     *  @param message Alarm message
     *  @param timestamp Instant for this update
     *  @see #getTimestamp()
     *  @return NONE if this item already has the same severities and message, PV if only this item has changed,
     *              or PV_AND_PARENT if both this item and its parent have changed
     */
    protected ChangeLevel setAlarmState(final SeverityLevel current_severity,
            final SeverityLevel severity, final String message,
            final Instant timestamp)
    {
        ChangeLevel level = setAlarmState(current_severity, severity, message, this);
        if (level == ChangeLevel.NONE)
            return level;
        this.timestamp = timestamp;
        return level;
    }

    /** PV entries have no sub-entries and thus don't maximize their severity;
     *  they receive it from the control system
     */
    @Override
    public boolean maximizeSeverity()
    {
        throw new IllegalStateException("Cannot maximize severity on leaf item"); //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return getVerboseDescription() + "\n";
    }
}
