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
	{
        // Rows beyond model are used to allow entry of new PV names!
        if (row >= model.getNumItems())
            table_viewer.replace(PVTableHelper.empty_row, row);
        else // Return actual model item.
            table_viewer.replace(model.getItem(row), row);
	}

	public void dispose() 
    {}
}
