package org.csstudio.platform.ui.internal.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/** Action that restarts the workspace with its current settings.
 *  @author Kay Kasemir
 */
public class RestartAction implements IWorkbenchWindowActionDelegate
{
    /** Workspace window */
    private IWorkbenchWindow window;
    
    /** Remember the workspace window */
    public void init(IWorkbenchWindow window)
    {
        this.window = window;
    }

    /** Release the workspace window */
    public void dispose()
    {
        window = null;
    }

    /** Restart the workspace */
    public void run(IAction action)
    {
        window.getWorkbench().restart();
    }

    /** {@inheritDoc} */
    public void selectionChanged(IAction action, ISelection selection)
    {
        // ignore
    }
}
