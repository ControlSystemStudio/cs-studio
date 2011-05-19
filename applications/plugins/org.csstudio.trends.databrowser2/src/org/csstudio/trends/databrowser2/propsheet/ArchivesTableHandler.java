/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.ui.TableHelper;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;

/** Helper for an 'Archives' TableViewer that handles the ArchiveDataSources
 *  if a PVItem.
 *  Input to the table is a PVItem, and each 'row' in the table is an
 *  ArchiveDataSource.
 *  @author Kay Kasemir
 */
public class ArchivesTableHandler implements ILazyContentProvider
{
    private PVItem  pv_item;
    private TableViewer table_viewer;

    /** Create table columns: Auto-sizable, with label provider and editor
     *  @param table_layout
     *  @param operations_manager
     *  @param archives_table
     */
    public void createColumns(TableColumnLayout table_layout, final OperationsManager operations_manager,
            final TableViewer archives_table)
    {
        table_viewer = archives_table;
        TableViewerColumn col;
        // Archive Name Column ----------
        col = TableHelper.createColumn(table_layout, archives_table, Messages.ArchiveName, 100, 20);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ArchiveDataSource archive = (ArchiveDataSource) cell.getElement();
                cell.setText(archive.getName());
            }
        });

        // Archive Key Column ----------
        col = TableHelper.createColumn(table_layout, archives_table, Messages.ArchiveKey, 20, 5);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ArchiveDataSource archive = (ArchiveDataSource) cell.getElement();
                cell.setText(Integer.toString(archive.getKey()));
            }
        });

        // Archive Server URL Column ----------
        col = TableHelper.createColumn(table_layout, archives_table, Messages.URL, 50, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ArchiveDataSource archive = (ArchiveDataSource) cell.getElement();
                cell.setText(archive.getUrl());
            }
        });
    }

    /** Set input to a Model
     *  @see ILazyContentProvider#inputChanged(Viewer, Object, Object)
     */
    @Override
    public void inputChanged(final Viewer viewer, final Object old_pv, final Object new_pv)
    {
        pv_item = (PVItem) new_pv;
        if (pv_item == null)
            table_viewer.setItemCount(0);
        else
            table_viewer.setItemCount(pv_item.getArchiveDataSources().length);
    }

    /** Called by ILazyContentProvider to get the ModelItem for a table row
     *  {@inheritDoc}
     */
    @Override
    public void updateElement(int index)
    {
        table_viewer.replace(pv_item.getArchiveDataSources()[index], index);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        // NOP
    }
}
