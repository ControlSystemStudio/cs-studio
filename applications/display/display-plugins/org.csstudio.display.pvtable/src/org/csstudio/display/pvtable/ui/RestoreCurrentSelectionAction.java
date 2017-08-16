/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import java.util.List;

import org.csstudio.display.pvtable.Messages;
import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;

/** {@link Action} to restore value snapshots
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RestoreCurrentSelectionAction extends PVTableAction
{
    public RestoreCurrentSelectionAction(final TableViewer viewer)
    {
        super(Messages.RestoreSelection, "icons/restore.png", viewer);
        setToolTipText(Messages.RestoreSelection_TT);
    }

    @Override
    public void run()
    {
        final PVTableModel model = (PVTableModel) viewer.getInput();
        @SuppressWarnings("unchecked")
        final List<PVTableItem> items = viewer.getStructuredSelection().toList();
        if (items.size() > 0 && model != null)
            model.restore(items);
    }
}
