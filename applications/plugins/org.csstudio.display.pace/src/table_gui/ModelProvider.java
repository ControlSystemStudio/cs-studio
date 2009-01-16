package table_gui;


import org.csstudio.display.pace.model.old.Cell;
import org.csstudio.display.pace.model.old.Model;
import org.csstudio.display.pace.model.old.Rows;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


/** A (lazy) content provider for the GUI's table.
 *  <p>
 *  Gets called by the TableViewer with requests for the rows (Instances)
 *  of the table (Model).
 *  
 *  @author Kay Kasemir
 */
public class ModelProvider implements ILazyContentProvider
{
    final private TableViewer viewer;
    final private Model model;

    /** Construct provider for a given table viewer and model */
    public ModelProvider(final TableViewer viewer, final Model model)
    {
        this.viewer = viewer;
        this.model = model;
    }

    /** Called by viewer to ask for an element (row, instance) of the model */
    public void updateElement(final int row)
    {
         viewer.replace(model.getRow(row), row);
    }

    /** {@inheritDoc} */
    public void dispose()
    {
        // Nothing to dispose
    }

    /** {@inheritDoc} */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        // Doesn't apply
    }
}
