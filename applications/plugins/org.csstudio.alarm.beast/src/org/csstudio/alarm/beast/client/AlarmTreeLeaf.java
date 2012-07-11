/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.client;

import org.csstudio.alarm.beast.Messages;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.apputil.time.SecondsParser;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.ITimestamp.Format;
import org.csstudio.data.values.TimestampFactory;
import org.eclipse.osgi.util.NLS;

/** Alarm tree 'leaf' that has time of alarm, tool tip info,
 *  CSS PV name
 *  @author Kay Kasemir
 */
public class AlarmTreeLeaf extends AlarmTreeItem
{
    /** Description of alarm */
    private String description = ""; //$NON-NLS-1$

    /** Timestamp of last alarm update */
    private ITimestamp timestamp = null;

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
    public synchronized void setDescription(final String description)
    {
        this.description = description == null ? "" : description; //$NON-NLS-1$
    }

    /** @return Alarm description */
    public synchronized String getDescription()
    {
        return description;
    }

    /** @return Verbose, multi-line description of the current alarm
     *          meant for elog entry or usage as drag/drop text
     */
    public synchronized String getVerboseDescription()
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
    public synchronized ITimestamp getTimestamp()
    {
        return timestamp;
    }

    /** @return Duration of current alarm state or empty text */
    public synchronized String getDuration()
    {
        final ITimestamp now = TimestampFactory.now();
        if (timestamp == null  ||  now.isLessThan(timestamp))
            return ""; //$NON-NLS-1$
        return SecondsParser.formatSeconds(now.seconds() - timestamp.seconds());
    }

    /** @return Time stamp of last status/severity update as text */
    public synchronized String getTimestampText()
    {
        if (timestamp == null)
            return ""; //$NON-NLS-1$
        return timestamp.format(Format.DateTimeSeconds);
    }

    /** Update status/message/time stamp and maximize
     *  severities of parent entries.
     *
     *  @param current_severity Current severity of PV
     *  @param severity Alarm severity
     *  @param message Alarm message
     *  @param timestamp Time stamp for this update
     *  @see #getTimestamp()
     *  @return <code>true</code> if alarm state actually changed
     */
    protected synchronized boolean setAlarmState(final SeverityLevel current_severity,
            final SeverityLevel severity, final String message,
            final ITimestamp timestamp)
    {
        if (! setAlarmState(current_severity, severity, message, this))
            return false;
        this.timestamp = timestamp;
        return true;
    }

    /** PV entries have no sub-entries and thus don't maximize their severity;
     *  they receive it from the control system
     */
    @Override
    public void maximizeSeverity()
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
