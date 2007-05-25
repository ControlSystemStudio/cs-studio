package org.csstudio.trends.databrowser.plotview;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/** Opens view for the currently selected DataBrowser config file.
 *  @author Kay Kasemir
 *  @author Sven Wende
 */
public class OpenAsPlotViewAction  extends Action implements IObjectActionDelegate
{
    /** The current selection. */
    private IStructuredSelection selection;

    public OpenAsPlotViewAction()
    {}

    /** Open currently selected IFile as View. */
    public void run(final IAction action)
    {
        if (selection == null)
            return;
        Object element = selection.getFirstElement();
        if (element instanceof IFile)
            PlotView.activateWithFile((IFile) element);
    }

    /** @see org.eclipse.ui.IActionDelegate#selectionChanged */
    public void selectionChanged(final IAction action, final ISelection selection)
    {
        if (selection instanceof IStructuredSelection)
            this.selection = (IStructuredSelection) selection;
        else
            this.selection = null;
    }

    /** @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart) */
    public void setActivePart(final IAction action,
                              final IWorkbenchPart targetPart)
    {}
}
