package org.csstudio.trends.databrowser;

import org.csstudio.trends.databrowser.editor.DataBrowserEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/** Action connected to workbench menu action set for opening a new editor.
 *  @author Kay Kasemir
 */
public class NewDataBrowserAction implements IWorkbenchWindowActionDelegate
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
        DataBrowserEditor.createInstance();
    }

    public void dispose()
    {
        // NOP
    }
}
