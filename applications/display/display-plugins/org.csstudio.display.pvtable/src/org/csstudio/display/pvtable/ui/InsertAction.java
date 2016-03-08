/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import java.util.Iterator;

import org.csstudio.display.pvtable.Messages;
import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

/**
 * {@link Action} to insert new row into table
 *
 * @author Kay Kasemir
 */
public class InsertAction extends PVTableAction {
    public InsertAction(final TableViewer viewer) {
        super(Messages.Insert, "icons/add.gif", viewer); //$NON-NLS-1$
        setToolTipText(Messages.Insert_TT);
    }

    @Override
    public void run() {
        final PVTableModel model = (PVTableModel) viewer.getInput();
        if (model == null)
            return;
        final IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
        if (sel == null)
            return;

        final Iterator<?> iterator = sel.iterator();
        while (iterator.hasNext()) {
            final PVTableItem item = (PVTableItem) iterator.next();
            model.addItemAbove(item, "# ");
        }
        viewer.setSelection(null);
        viewer.setItemCount(model.getItemCount() + 1);
        viewer.refresh();
    }
}
