package org.csstudio.trends.databrowser.sampleview;

import org.csstudio.trends.databrowser.model.IModelItem;
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
	public void updateElement(final int row)
	{
        final IModelItem model_item = sample_view.getModelItem();
        final TableItem item = new TableItem(model_item, row);
        table_viewer.replace(item, row);
	}

	public void dispose() 
    {   /* NOP */ }
}
