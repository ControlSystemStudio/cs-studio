package org.csstudio.platform.ui.workbench;

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/** Helper for creating an action that opens a view.
 *  <p>
 *  Meant to be subclassed to provide the view-ID,
 *  and the subclassed Action is then placed in the
 *  'CSS' menu bar so users can open a CSS view
 *  from the menu bar.
 *  
 *  @author Kay Kasemir
 */
public class OpenViewAction implements IWorkbenchWindowActionDelegate
{
    /** ID of the view to open */
    final private String id;
    
    /** @param id ID of the view to open */
    public OpenViewAction(final String id)
    {
        this.id = id;
    }

    public void init(IWorkbenchWindow window)
    {
        // NOP
    }

    public void selectionChanged(IAction action, ISelection selection)
    {
        // NOP
    }

    public void run(final IAction action)
    {
        try
        {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            page.showView(id);
        }
        catch (Exception ex)
        {
            CentralLogger.getInstance().getLogger(this).error(ex);
        }
    }

    public void dispose()
    {
        // NOP
    }
}