package org.csstudio.trends.databrowser.configview;

import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/** Provide Model data for table with stype SWT.VIRTUAL.
 *  @author Kay Kasemir
 */
public class PVTableLazyContentProvider implements ILazyContentProvider
{
	private TableViewer table_viewer = null;
	private Model model = null;
		
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        this.table_viewer = (TableViewer) viewer;
        this.model = (Model) newInput;
    }

    /** Called by 'lazy' table, needs to 'replace' entry of given row. */
	public void updateElement(int row)
	{	// System.out.println("LazyLogContentProvider update row " + row);
		table_viewer.replace(model.getItem(row), row);
	}

	public void dispose() 
    {}
}
