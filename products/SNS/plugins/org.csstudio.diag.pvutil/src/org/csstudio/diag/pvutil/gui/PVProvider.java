package org.csstudio.diag.pvutil.gui;


import org.csstudio.diag.pvutil.model.PVUtilModel;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/** Provides the Device for a specific Table row */
public class PVProvider implements ILazyContentProvider
{
    final private TableViewer pv_table;
	final private PVUtilModel control;

    public PVProvider(TableViewer pv_table, PVUtilModel control)
    {
        this.pv_table = pv_table;
        this.control = control;
    }

    public void updateElement(int row)
    {
        try
        {
            pv_table.replace(control.getPV(row), row);
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
