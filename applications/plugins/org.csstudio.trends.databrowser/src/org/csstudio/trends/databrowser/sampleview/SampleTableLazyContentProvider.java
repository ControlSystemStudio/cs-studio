package org.csstudio.trends.databrowser.sampleview;

import org.csstudio.trends.databrowser.model.ModelSamples;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/** Provide Model data for table with stype SWT.VIRTUAL.
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
    {
    }

    /** Called by 'lazy' table, needs to 'replace' entry of given row. */
	public void updateElement(int row)
	{
        ModelSamples samples = sample_view.getSamples();
        synchronized (samples)
        {
            final int N = samples.size();
            if (row >= N)
            {
                table_viewer.replace(null, row);
                return;
            }
            table_viewer.replace(samples.get(row), row);
        }
	}

	public void dispose() 
    {}
}
