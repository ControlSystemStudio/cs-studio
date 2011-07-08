/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtree;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.alarm.beast.ui.SeverityColorProvider;
import org.csstudio.alarm.beast.ui.SeverityIconProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

/** Label provider turns AlarmTreeItem into text, color, image, .. for Tree
 *  @author Kay Kasemir
 */
public class AlarmTreeLabelProvider extends CellLabelProvider
{
    final private SeverityColorProvider color_provider;
    final private SeverityIconProvider icon_provider;

    /** Initialize
     *  @param parent Parent widget (used for dispose listener)
     */
    public AlarmTreeLabelProvider(final Composite parent)
    {
        color_provider = new SeverityColorProvider(parent);
        icon_provider = new SeverityIconProvider(parent);
    }

    @Override
    public String getToolTipText(final Object element)
    {
        // Saw null here when tree content changes while (slow) tool tip decides to show
        final AlarmTreeItem item = (AlarmTreeItem) element;
        if (item == null)
            return ""; //$NON-NLS-1$
        return item.getToolTipText();
    }

    /** Set a cell's text and color from alarm tree item */
    @Override
    public void update(final ViewerCell cell)
    {
        final AlarmTreeItem item = (AlarmTreeItem) cell.getElement();
        // Text
        cell.setText(getText(item));

        // Provide icon that represents the item's severity
        if (item instanceof AlarmTreePV  &&
            !((AlarmTreePV)item).isEnabled())
        {
            cell.setImage(icon_provider.getDisabledIcon());
            cell.setBackground(null);
            cell.setForeground(null);
            return; // done, skip the color coding
        }
        else
            cell.setImage(icon_provider.getIcon(item.getCurrentSeverity(),
                          item.getSeverity()));

        // Color-code AlarmTreePV based on severity.
        final SeverityLevel severity = item.getSeverity();
        //  'OK' severity isn't color-coded.
        if (severity == SeverityLevel.OK)
        {
            cell.setBackground(null);
            cell.setForeground(null);
            return;
        }
        //  Generally, the foreground (text) color is set,
        //  but for PV items with active alarms, the background
        //  color is set to make it stand out even more.
        final Color color = color_provider.getColor(severity);
        if ((item instanceof AlarmTreePV)  &&  severity.isActive())
        {
            cell.setBackground(color);
            cell.setForeground(null);
        }
        else
        {
            cell.setForeground(color);
            cell.setBackground(null);
        }
    }

    /** Provide item's "name (severity/status)"
     *  @param item AlarmTreeItem
     */
    @SuppressWarnings("nls")
    public String getText(final AlarmTreeItem item)
    {
        final StringBuilder buf = new StringBuilder(20);
        AlarmTreePV pv = null;
        switch (item.getPosition())
        {
        case PV:
            // getPosition indicates that it's a PV, but check to please FindBugs
            if (item instanceof AlarmTreePV)
                pv = (AlarmTreePV) item;
            else
                throw new RuntimeException("Not a PV?");
            buf.append(Messages.AlarmPV);
            break;
        case Area:
            buf.append(Messages.AlarmArea);
            break;
        default:
            buf.append(Messages.AlarmComponent);
        }
        buf.append(": ");
        buf.append(item.getName());
        // Suppress alarm info for disabled PVs
        if (pv != null  &&  pv.isEnabled() == false)
        	return buf.toString();
        // Add alarm info unless it's all OK
        if (item.getCurrentSeverity() != SeverityLevel.OK ||
            item.getSeverity() != SeverityLevel.OK)
        {
            buf.append(" (");
            buf.append(item.getSeverity().getDisplayName());
            buf.append("/");
            buf.append(item.getMessage());
            if (pv != null)
            {
                buf.append(",");
                buf.append(pv.getCurrentSeverity().getDisplayName());
                buf.append("/");
                buf.append(pv.getCurrentMessage());
            }
            buf.append(")");
        }
        return buf.toString();
    }
}
