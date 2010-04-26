package org.csstudio.trends.databrowser.propsheet;

import org.csstudio.platform.ui.swt.AutoSizeColumn;
import org.csstudio.platform.ui.swt.AutoSizeControlListener;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.model.ArchiveDataSource;
import org.csstudio.trends.databrowser.model.PVItem;
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
public class ArchivesTableHandler  implements ILazyContentProvider
{
    private PVItem  pv_item;
    private TableViewer table_viewer;

    /** Create table columns: Auto-sizable, with label provider and editor
     *  @param archives_table
     */
    public void createColumns(final OperationsManager operations_manager,
            final TableViewer archives_table)
    {
        table_viewer = archives_table;
        TableViewerColumn col;
        // Archive Name Column ----------
        col = AutoSizeColumn.make(archives_table, Messages.ArchiveName, 100, 20);
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
        col = AutoSizeColumn.make(archives_table, Messages.ArchiveKey, 20, 5);
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
        col = AutoSizeColumn.make(archives_table, Messages.URL, 50, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ArchiveDataSource archive = (ArchiveDataSource) cell.getElement();
                cell.setText(archive.getUrl());
            }
        });
        
        new AutoSizeControlListener(archives_table.getTable());
    }

    /** Set input to a Model
     *  @see ILazyContentProvider#inputChanged(Viewer, Object, Object)
     */
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
    public void updateElement(int index)
    {
        table_viewer.replace(pv_item.getArchiveDataSources()[index], index);
    }

    // ILazyContentProvider
    public void dispose()
    {
        // NOP
    }
}
