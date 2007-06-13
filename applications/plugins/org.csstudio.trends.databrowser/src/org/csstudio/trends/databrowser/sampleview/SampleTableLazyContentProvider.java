package org.csstudio.trends.databrowser.sampleview;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/** Provide Model data for table with type SWT.VIRTUAL.
 *  @author Kay Kasemir
 */
public class SampleTableLazyContentProvider implements ILazyContentProvider
{
    private SampleView sample_view;
    private TableViewer table_viewer;
    
    public SampleTableLazyContentProvider(SampleView sample_view,
                    TableViewer table_viewer)
    {
        this.sample_view = sample_view;
        this.table_viewer = table_viewer;
    }
    
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {   /* NOP */ }

    /** Called by 'lazy' table, needs to 'replace' entry of given row. */
	public void updateElement(int row)
	{
        TableModel table_model = sample_view.getTableModel();
        TableItem item = table_model.getTableItem(row);
        table_viewer.replace(item, row);
	}

	public void dispose() 
    {   /* NOP */ }
}
