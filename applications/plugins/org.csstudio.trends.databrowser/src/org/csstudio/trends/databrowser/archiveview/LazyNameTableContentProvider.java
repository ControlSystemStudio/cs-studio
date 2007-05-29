package org.csstudio.trends.databrowser.archiveview;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/** Provide NameTableItem data for table with type SWT.VIRTUAL.
 *  @author Kay Kasemir
 */
public class LazyNameTableContentProvider implements ILazyContentProvider
{
    private TableViewer table_viewer;
    private ArrayList<NameTableItem> name_table_items;
    
    /** Construct content provider for given table viewer. */
    public LazyNameTableContentProvider(TableViewer table_viewer,
                                     ArrayList<NameTableItem> name_table_items)
    {
        this.table_viewer = table_viewer;
        this.name_table_items = name_table_items;
    }
    
    /** Called by 'lazy' table, needs to 'replace' entry of given row. */
    public void updateElement(int row)
    {
        table_viewer.replace(name_table_items.get(row), row);
    }

    public void dispose() 
    { /* NOP */ }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    { /* NOP */ }
}
