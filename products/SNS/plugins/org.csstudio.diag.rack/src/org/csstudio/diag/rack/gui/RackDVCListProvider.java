package org.csstudio.diag.rack.gui;


import org.csstudio.diag.rack.model.RackModel;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/** Provides the Device for a specific Table row */
public class RackDVCListProvider implements ILazyContentProvider
{
    final private TableViewer rack_list_table;
    final private RackModel rackControl;

    public RackDVCListProvider(TableViewer rack_list_table, RackModel rackControl)
    {
        this.rack_list_table = rack_list_table;
        this.rackControl = rackControl;

    }

    public void updateElement(int index)
    {
        try
        {
            rack_list_table.replace(rackControl.getRackListDVC(index), index);
        }
        catch (Throwable ex)
        {
            // Ignore.
            // When the model changes because of ongoing queries,
            // it's possible to access an invalid element because
            // the table just changed on us.
        }
    }

    public void dispose()
    {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }
}
