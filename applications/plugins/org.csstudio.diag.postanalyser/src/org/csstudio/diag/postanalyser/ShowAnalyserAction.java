package org.csstudio.diag.postanalyser;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/** Show analyzer view in response to CSS/diag/... main menu action */
public class ShowAnalyserAction implements IWorkbenchWindowActionDelegate
{
	public void init(IWorkbenchWindow window)
	{ /* NOP */ }

	public void selectionChanged(IAction action, ISelection selection)
    { /* NOP */ }

	public void run(IAction action)
	{
	    try
	    {
	        IWorkbench workbench = PlatformUI.getWorkbench();
	        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	        IWorkbenchPage page = window.getActivePage();
	        page.showView(View.ID);
	    }
	    catch (Exception ex)
	    {
	        Activator.getLogger().error("Cannot show Analyzer", ex); //$NON-NLS-1$
	    }
	}

	public void dispose()
    { /* NOP */ }
}
