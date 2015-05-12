/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.rdbtable.ui;

import org.csstudio.display.rdbtable.Activator;
import org.csstudio.display.rdbtable.Messages;
import org.csstudio.display.rdbtable.model.RDBTableRow;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;

/** Action for deleting a row in the RDBTable
 *  @author Kay Kasemir
 */
public class DeleteRowAction extends Action
{
    final private TableViewer table_viewer;

    /** Initialize
     *  @param table_viewer TableViewer for RDBTableRow entries
     */
    public DeleteRowAction(final TableViewer table_viewer)
    {
        super(Messages.DeleteRow, Activator.getImageDescriptor("html/delete.gif")); //$NON-NLS-1$
        setToolTipText(Messages.DeleteRow_TT);
        this.table_viewer = table_viewer;

        // Enable action whenever one or more not-already-deleted rows are selected
        table_viewer.addSelectionChangedListener(new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                final RDBTableRow rows[] = getSelectedRows(event.getSelection());
                // Anything selected that's not already deleted?
                if (rows == null)
                    setEnabled(false);
                else
                {
                    boolean anything = false;
                    for (RDBTableRow row : rows)
                        if (!row.wasDeleted())
                        {
                            anything = true;
                            break;
                        }
                    setEnabled(anything);
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        final RDBTableRow rows[] = getSelectedRows(table_viewer.getSelection());
        if (rows == null)
            return;
        table_viewer.setSelection(null);
        for (RDBTableRow row : rows)
            row.delete();
    }

    /** @param selection Current selection
     *  @return RDBTableRow items in the selection or <code>null</code>
     */
    private RDBTableRow[] getSelectedRows(final ISelection selection)
    {
        if (selection.isEmpty())
            return null;

        final Object obj[] = ((IStructuredSelection) selection).toArray();
        final RDBTableRow rows[] = new RDBTableRow[obj.length];
        System.arraycopy(obj, 0, rows, 0, rows.length);
        return rows;
    }
}
