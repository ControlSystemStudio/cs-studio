/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.ui;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.TableColumn;

/** Helper for creating table columns for the TableColumnLayout
 *  @author Kay Kasemir
 */
public class TableHelper
{
    /** Create table column with layout info
     *  @param table_layout
     *  @param table_viewer
     *  @param title
     *  @param width Minimum width
     *  @param weight
     *  @return TableViewerColumn
     */
    public static TableViewerColumn createColumn(final TableColumnLayout table_layout,
            final TableViewer table_viewer, final String title, final int width, final int weight)
    {
        final TableViewerColumn view_col = new TableViewerColumn(table_viewer, 0);
        final TableColumn col = view_col.getColumn();
        col.setText(title);
        table_layout.setColumnData(col, new ColumnWeightData(weight, width));
        col.setMoveable(true);
        return view_col;
    }
}
