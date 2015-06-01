/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable;

import org.csstudio.alarm.beast.AnnunciationFormatter;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.GDCDataStructure;
import org.csstudio.alarm.beast.ui.SeverityColorProvider;
import org.csstudio.apputil.ui.swt.CheckBoxImages;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

/** Provider of label/color/... for the alarm table.
 *  @author Kay Kasemir
 */
public class AlarmTableLabelProvider extends CellLabelProvider
{
    /** Mapping of severities to colors */
    final private SeverityColorProvider color_provider;
    
    final private SeverityIconProvider icon_provider;

    /** Column handled by this label provider */
    final private ColumnInfo column;

    /** Initialize
     *  @param icon_provider icon provider provides icons for the ICON column
     *  @param color_provider Color provider for severity values
     *  @param column Column for which this label provider should give labels etc.
     */
    public AlarmTableLabelProvider(final SeverityIconProvider icon_provider,
            final SeverityColorProvider color_provider, final ColumnInfo column)
    {
        this.icon_provider = icon_provider;
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
            cell.setImage(icon_provider.getIcon(alarm));
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
}
