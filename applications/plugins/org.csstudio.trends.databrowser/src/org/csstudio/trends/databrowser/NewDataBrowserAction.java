package org.csstudio.trends.databrowser;

import org.csstudio.trends.databrowser.ploteditor.PlotEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/** Action connected to workbench menu action set for opening a new editor.
 *  @author Kay Kasemir
 */
public class NewDataBrowserAction implements IWorkbenchWindowActionDelegate
{
	public void init(IWorkbenchWindow window)
	{ /* NOP */ }

	public void selectionChanged(IAction action, ISelection selection)
    { /* NOP */ }

	public void run(IAction action)
	{
		PlotEditor.createInstance();
		// Try to switch to the DataBrowser perspective
		// (suggested by Helge Rickens)
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        try
        {
            workbench.showPerspective(Perspective.ID, window);
        }
        catch (WorkbenchException ex)
        {
            // Never mind
        }
	}

	public void dispose()
    { /* NOP */ }
}
