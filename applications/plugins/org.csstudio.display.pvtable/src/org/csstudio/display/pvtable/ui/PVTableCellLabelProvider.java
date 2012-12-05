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

/** Cell label provider for PV Table that handles the tool tip
 *  @author Kay Kasemir
 */
abstract public class PVTableCellLabelProvider extends CellLabelProvider
{
    @Override
    public String getToolTipText(final Object element)
    {
        final PVTableItem item = (PVTableItem) element;
        return item.toString();
    }
}
