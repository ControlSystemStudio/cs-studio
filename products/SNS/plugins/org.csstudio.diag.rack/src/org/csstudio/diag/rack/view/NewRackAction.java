package org.csstudio.diag.rack.view;

import org.csstudio.diag.rack.view.RackView;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/** Action connected to workbench menu action set for opening a new editor.
 *  @author Dave Purcell stolen from Kay Kasemir
 */

public class NewRackAction implements IWorkbenchWindowActionDelegate
{
    public void init(IWorkbenchWindow window)
    { /* NOP */
    }

    public void selectionChanged(IAction action, ISelection selection)
    { /* NOP */
    }

    public void run(IAction action)
    {
        try
        {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            page.showView(RackView.ID);
        }
        catch (Exception ex)
        {
        	CentralLogger.getInstance().getLogger(this).error("Exception", ex);
        }
    }

    public void dispose()
    { /* NOP */
    }
}
