/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable;

import org.csstudio.alarm.beast.AnnunciationFormatter;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.GDCDataStructure;
import org.csstudio.alarm.beast.ui.SeverityColorProvider;
import org.csstudio.apputil.ui.swt.CheckBoxImages;
import org.csstudio.ui.resources.alarms.AlarmIcons;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/** Provider of label/color/... for the alarm table.
 *  @author Kay Kasemir
 */
public class AlarmTableLabelProvider extends CellLabelProvider
{
    /** Mapping of severities to colors */
    final private SeverityColorProvider color_provider;

    /** Column handled by this label provider */
    final private ColumnInfo column;

    /** Initialize
     *  @param parent Parent widget (for dispose listener)
     *  @param color_provider Color provider for severity values
     *  @param column Column for which this label provider should give labels etc.
     */
    public AlarmTableLabelProvider(final Composite parent,
            final SeverityColorProvider color_provider, final ColumnInfo column)
    {
        this.color_provider = color_provider;
        this.column = column;
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
            cell.setImage(getIcon(alarm));
            break;
        case PV:
            cell.setText(alarm.getName());
            break;
        case DESCRIPTION:
            {
            final String annunciation = AnnunciationFormatter.format(alarm.getDescription(),
                    alarm.getSeverity().getDisplayName(), alarm.getValue());
            cell.setText(annunciation);
            }
            break;
        case TIME:
            cell.setText(alarm.getTimestampText());
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
    }

    /** Returns an icon representing the severity/state of the given alarm.
     *  There are 7 different icons: one for disconnected alarms, and two (major/minor) icons
     *  for each of the following: alarm, alarm acknowledged, alarm cleared but not acknowledged.
     *
     *  @param pv the alarm for which the icon should be returned
     *  @return the icon representing the alarm severity
     */
    private static Image getIcon(final AlarmTreePV pv)
    {
        final SeverityLevel severity = pv.getSeverity();
        final SeverityLevel currentSeverity = pv.getCurrentSeverity();
        final AlarmIcons icons = AlarmIcons.getInstance();
        switch(severity)
        {
            case UNDEFINED_ACK:
            case INVALID_ACK:
                return icons.getInvalidAcknowledged();
            case UNDEFINED:
            case INVALID:
                return currentSeverity == SeverityLevel.OK ?
                       icons.getInvalidClearedNotAcknowledged() : icons.getInvalidNotAcknowledged();
            case MAJOR:
                return currentSeverity == SeverityLevel.OK ?
                       icons.getMajorClearedNotAcknowledged() : icons.getMajorNotAcknowledged();
            case MAJOR_ACK:
                return icons.getMajorAcknowledged();
            case MINOR:
                return currentSeverity == SeverityLevel.OK ?
                       icons.getMinorClearedNotAcknowledged() : icons.getMinorNotAcknowledged();
            case MINOR_ACK:
                return icons.getMinorAcknowledged();
            case OK:
            default:
                return null;
        }
    }
}
