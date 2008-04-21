package org.csstudio.utility.eliza;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/** Menu action to open Eliza View
 *  @author Kay Kasemir
 */
public class ShowElizaAction implements IWorkbenchWindowActionDelegate
{
    public void init(IWorkbenchWindow window)
    {
        // NOP
    }

    public void selectionChanged(IAction action, ISelection selection)
    {
        // NOP
    }

    public void run(IAction action)
    {
        try
        {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            page.showView(ElizaView.ID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void dispose()
    {
        // NOP
    }
}
