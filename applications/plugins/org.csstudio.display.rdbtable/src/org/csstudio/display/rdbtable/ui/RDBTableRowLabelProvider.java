/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.rdbtable.ui;

import org.csstudio.display.rdbtable.Messages;
import org.csstudio.display.rdbtable.model.RDBTableRow;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/** Label provider for TableViewer.
 *  
 *  For each row of the table, the RDBTableModelContentProvider gives an
 *  RDBTableRow to the TableViewer, which then asks this class to
 *  determine how to display the information in the row,
 *  i.e. the columns, in the table.
 *  
 *  @author Kay Kasemir
 */
public class RDBTableRowLabelProvider extends CellLabelProvider
{
    /** @param cell Cell in table that we need to update with info from
     *              the RDBTableRow
     */
    @Override
    public void update(final ViewerCell cell)
    {
        final RDBTableRow row = (RDBTableRow) cell.getElement();
        // Display text of corresponding RDBTable column
        cell.setText(row.getColumn(cell.getColumnIndex()));
        // Highlight changed or deleted cells
        final Display display = cell.getControl().getDisplay();
        if (row.wasDeleted())
            cell.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
        else if (row.wasModified())
            cell.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
        else
            cell.setBackground(null);
    }

    /** @param element  RDBTableRow
     *  @return Tool tip for RDBTableRow
     */
    @Override
    public String getToolTipText(final Object element)
    {
        final RDBTableRow row = (RDBTableRow) element;
        if (row.wasDeleted())
            return Messages.RowDeleted_TT;
        else if (row.wasModified())
            return Messages.RowModified_TT;
        else
            return null;
    }
}
