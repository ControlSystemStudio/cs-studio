/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.csstudio.display.pvtable.Messages;
import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

/**
 * {@link Action} to save value snapshots for all items that are currently
 * selected in the table
 *
 * @author Kay Kasemir
 */
public class SnapshotCurrentSelectionAction extends PVTableAction {
    public SnapshotCurrentSelectionAction(final TableViewer viewer) {
        super(Messages.SnapshotSelection, "icons/snapshot.png", viewer); //$NON-NLS-1$
        setToolTipText(Messages.SnapshotSelection_TT);
    }

    @Override
    public void run() {
        final PVTableModel model = (PVTableModel) viewer.getInput();
        final List<PVTableItem> items = new ArrayList<>();
        final Iterator<?> selection = ((IStructuredSelection) viewer.getSelection()).iterator();
        while (selection.hasNext())
            items.add((PVTableItem) selection.next());
        if (items.size() > 0 && model != null)
            model.save(items);
    }
}
