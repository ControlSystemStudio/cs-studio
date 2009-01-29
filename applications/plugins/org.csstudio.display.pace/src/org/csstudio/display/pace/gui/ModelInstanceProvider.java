package org.csstudio.display.pace.gui;

import org.csstudio.display.pace.model.Model;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/** A (lazy) content provider for the GUI's table.
 *  <p>
 *  Gets called by the TableViewer with requests for the rows (Instances)
 *  of the table (Model).
 *  
 *  @author Kay Kasemir
 *  
 *     reviewed by Delphy 01/29/09
 */
//TODO Explain requests - "requests for the rows"
public class ModelInstanceProvider implements ILazyContentProvider
{
    private TableViewer table_viewer;
    private Model model;

    /** We happen to know that this is called by the GUI via
     *  TableViewer.setInput(Model).
     *  It's also called with a <code>null</code> input
     *  when the application shuts down.
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        table_viewer = (TableViewer) viewer;
        model = (Model)newInput;
        
        // Setting the item count causes a 'refresh' of the table
        if (model == null)
            table_viewer.setItemCount(0);
        else
            table_viewer.setItemCount(model.getInstanceCount());
    }

    /** Called by viewer to ask for an element (row, instance) of the model */
    //TODO Explain - does more than "ask for an element"
    public void updateElement(final int row)
    {
        table_viewer.replace(model.getInstance(row), row);
    }

    /** {@inheritDoc} */
    public void dispose()
    {
        // Nothing to dispose
    }
}
