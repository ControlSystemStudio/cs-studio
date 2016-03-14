/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable;

import java.text.SimpleDateFormat;

import org.csstudio.alarm.beast.AnnunciationFormatter;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.GDCDataStructure;
import org.csstudio.alarm.beast.ui.SeverityColorProvider;
import org.csstudio.apputil.ui.swt.CheckBoxImages;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;

/** Provider of label/color/... for the alarm table.
 *  @author Kay Kasemir
 *  @author Boris Versic - Alarm Description formatting, row coloring
 */
public class AlarmTableLabelProvider extends CellLabelProvider
{
    /** Mapping of severities to colors */
    final private SeverityColorProvider color_provider;
    final private SeverityColorPairProvider color_pair_provider;
    final private SeverityIconProvider icon_provider;

    /** Whether rows' background should be painted with the alarm's severity color */
    final private boolean background_color_alarm_sensitive = Preferences.isBackgroundColorAlarmSensitive();
    /** Whether recovered (cleared) unacknowledged alarms should have their description drawn using reversed colors */
    final private boolean reverse_colors = Preferences.isColorsReversed();

    /** Column handled by this label provider */
    final private ColumnInfo column;

    private String timeFormat;
    private SimpleDateFormat formatter;

    /** Initialize
     *  @param icon_provider icon provider
     *  @param color_provider Color provider for severity values
     *  @param column Column for which this label provider should give labels etc.
     */
    public AlarmTableLabelProvider(final SeverityIconProvider icon_provider,
            final SeverityColorProvider color_provider, final ColumnInfo column)
    {
        this(icon_provider, color_provider, null, column);
    }

    /** Initialize
     *  @param icon_provider icon provider
     *  @param color_provider Color provider for severity values
     *  @param color_pair_provider Color-pair provider for severity values
     *  @param column Column for which this label provider should give labels etc.
     */
    public AlarmTableLabelProvider(final SeverityIconProvider icon_provider,
            final SeverityColorProvider color_provider, final SeverityColorPairProvider color_pair_provider,
            final ColumnInfo column)
    {
        this.icon_provider = icon_provider;
        this.color_provider = color_provider;
        this.color_pair_provider = color_pair_provider;
        this.column = column;
    }

    /**
     * Sets the time format used for converting the time value to a string
     *
     * @param timeFormat the format string acceptable by {@link SimpleDateFormat}
     */
    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
        if (this.timeFormat == null || this.timeFormat.isEmpty()) {
            formatter = null;
        } else {
            try {
                formatter = new SimpleDateFormat(timeFormat);
            } catch (Exception e) {
                formatter = null;
            }
        }
    }

    /** @return Tooltip text for an alarm */
    @Override
    public String getToolTipText(final Object element)
    {
        final AlarmTreePV alarm = (AlarmTreePV) element;
        // Special handling of 'info' entry that has no parent
        if (alarm.getParent() == null)
            return alarm.getDescription();
        return alarm.getToolTipText();
    }

    /** @return Tooltip shift for the alarm tooltip */
    @Override
    public Point getToolTipShift(Object object) {
        /* Default tooltip position is 10px right and 0px down from the current cursor position.
         * This causes a bug on Linux (GTK) when the tooltip is longer than the available
         * space on the right of the cursor: the tooltip window is shifted left, but is immediately
         * removed (presumably because the mouse now hovers over the tooltip, however it doesn't
         * happen if the tooltip is also automatically moved up and the mouse is smack in the middle
         * of it..).
         *
         * Shifting the tooltip position a bit lower fixed the problem observed on Linux.
         */

        return new Point(10, 2);
    }

    /** Update one cell of the table */
    @Override
    public void update(final ViewerCell cell)
    {
        // AlarmTableProvider should always provide "AlarmTreePV" elements
        final AlarmTreePV alarm = (AlarmTreePV) cell.getElement();

        // Special handling of 'info' entry that has no parent
        switch (column)
        {
        case ACK:
            cell.setImage(alarm.getSeverity().isActive()
                    ? CheckBoxImages.getInstance(cell.getControl()).getImage(false)
                    : CheckBoxImages.getInstance(cell.getControl()).getImage(true));
            break;
        case ICON:
            cell.setImage(icon_provider.getIcon(alarm));
            break;
        case PV:
            cell.setText(alarm.getName());
            break;
        case DESCRIPTION:
            {
            final String annunciation = AnnunciationFormatter.format(alarm.getDescription(),
                    alarm.getSeverity().getDisplayName(), alarm.getValue(), true);
            cell.setText(annunciation);
            }

            break;
        case TIME:
            cell.setText(formatter == null ? alarm.getTimestampText()
                    : (alarm.getTimestamp() == null ? "" : formatter.format(alarm.getTimestamp().toDate())));
            break;
        case CURRENT_SEVERITY:
            if (alarm.getParent() == null)
                return;
            cell.setText(alarm.getCurrentSeverity().getDisplayName());
            cell.setBackground(color_provider.getColor(alarm.getCurrentSeverity()));
            break;
        case CURRENT_STATUS:
            if (alarm.getParent() == null)
                return;
            cell.setText(alarm.getCurrentMessage());
            break;
        case SEVERITY:
            if (alarm.getParent() == null)
                return;
            cell.setText(alarm.getSeverity().getDisplayName());
            cell.setBackground(color_provider.getColor(alarm.getSeverity()));
            break;
        case STATUS:
            if (alarm.getParent() == null)
                return;
            cell.setText(alarm.getMessage());
            break;
        case VALUE:
            if (alarm.getParent() == null)
                return;
            cell.setText(alarm.getValue());
            break;
        case ACTION:
            GDCDataStructure[] guidance = alarm.getGuidance();
            if (guidance.length != 0) {
                cell.setText(guidance[0].getDetails());
            }
            break;
        case ID:
            GDCDataStructure[] guidances = alarm.getGuidance();
            if (guidances.length != 0) {
                cell.setText(guidances[0].getTitle());
            }
            break;
        default:
            break;
        }

        if (column == ColumnInfo.ICON) return;

        // If enabled, the background color will reflect the severity of the alarm (when in alarm state).
        // If reverse_colors is also enabled, the background/text colors for unacknowledged cleared alarms will be reversed.
        if (!background_color_alarm_sensitive) return;

        final SeverityLevel severity = alarm.getSeverity();
        if (severity == SeverityLevel.OK)
        {
            // if OK, use default colors
            cell.setBackground(null);
            cell.setForeground(null);
            return;
        }

        final SeverityLevel current_severity = alarm.getCurrentSeverity();
        final Color severity_color = color_provider.getColor(severity);
        final Color color_pair = color_pair_provider == null ? null : color_pair_provider.getColor(severity);

        Color bg_color = severity_color, fg_color = color_pair;

        if (reverse_colors && current_severity == SeverityLevel.OK)
        {
            // the alarm is currently cleared (recovered), and color reversal is enabled
            bg_color = color_pair;
            fg_color = severity_color;
        }

        cell.setBackground(bg_color);
        cell.setForeground(fg_color);
    }
}
