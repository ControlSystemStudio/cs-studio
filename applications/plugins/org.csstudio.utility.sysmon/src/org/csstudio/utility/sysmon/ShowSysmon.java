package org.csstudio.utility.sysmon;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

/** Display the System Monitor */
public class ShowSysmon implements IWorkbenchWindowActionDelegate
{
    private IWorkbenchWindow window;
    
    public void dispose()
    {
        window = null;
    }

    public void init(IWorkbenchWindow window)
    {
        this.window = window;
    }

    public void run(IAction action)
    {
        if (window == null)
            return;
        try
        {
            window.getActivePage().showView(SysMonView.ID);
        }
        catch (PartInitException ex)
        {
            ex.printStackTrace();
        }
    }

    public void selectionChanged(IAction action, ISelection selection)
    {
        // NOP
    }
}
