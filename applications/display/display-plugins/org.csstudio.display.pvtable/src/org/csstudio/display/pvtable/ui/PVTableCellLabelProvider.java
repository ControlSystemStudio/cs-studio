/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.model.PVTableItem;
import org.eclipse.jface.viewers.CellLabelProvider;

/**
 * Cell label provider for PV Table that handles the tool tip
 *
 * @author Kay Kasemir
 */
abstract public class PVTableCellLabelProvider extends CellLabelProvider {
    private static final int TOOL_TIP_MAX = 100;

    public static String getTableItemTooltip(final PVTableItem item) {
        if (item == PVTableModelContentProvider.NEW_ITEM) {
            return "Add new PV to table by adding its name";
        }
        final String text = item.toString();
        // Limit size of tool tip (in case of array data)
        if (text.length() > TOOL_TIP_MAX) {
            return text.substring(0, TOOL_TIP_MAX) + "...";
        }
        return text;
    }

    @Override
    public String getToolTipText(final Object element) {
        return getTableItemTooltip((PVTableItem) element);
    }
}
