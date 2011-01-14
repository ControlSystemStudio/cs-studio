/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globaltable;

import org.csstudio.alarm.beast.ui.globalclientmodel.GlobalAlarm;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

/** Helper for creating a table cell label provider
 *  when table elements are of type {@link GlobalAlarm}
 *  @author Kay Kasemir
 */
abstract public class GlobalAlarmCellLabelProvider  extends CellLabelProvider
{
    /** Show alarm's tool tip */
    @Override
    public String getToolTipText(final Object element)
    {
        // Can element be null when content changes while (slow) tool tip decides to show?
        if (element == null)
            return ""; //$NON-NLS-1$
        final GlobalAlarm alarm = (GlobalAlarm) element;
        return alarm.getToolTipText();
    }

    /** Set cell to alarm's text */
    @Override
    public void update(final ViewerCell cell)
    {
        final GlobalAlarm alarm = (GlobalAlarm) cell.getElement();
        // Actual text provided by derived class
        cell.setText(getCellText(alarm));
    }

    /** To be implemented by derived class
     *  @param alarm Alarm for this cell (table row)
     *  @return Cell text to display
     */
    abstract protected String getCellText(GlobalAlarm alarm);
}
