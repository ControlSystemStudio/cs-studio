/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.Messages;
import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;

/** {@link Action} to select all table entries
 *  @author Kay Kasemir
 */
public class SelectAllAction extends PVTableAction
{
    public SelectAllAction(final TableViewer viewer)
    {
        super(Messages.SelectAll, "icons/checked.gif", viewer); //$NON-NLS-1$
        setToolTipText(Messages.SelectAll_TT);
    }
    
    public void run()
    {
        final PVTableModel model = (PVTableModel) viewer.getInput();
        if (model == null)
            return;
        final int N = model.getItemCount();
        for (int i=0; i<N; ++i)
        {
            final PVTableItem item = model.getItem(i);
            if (item.isSelected())
            	continue;
            item.setSelected(true);
            viewer.update(item, null);
        }
    }
}
