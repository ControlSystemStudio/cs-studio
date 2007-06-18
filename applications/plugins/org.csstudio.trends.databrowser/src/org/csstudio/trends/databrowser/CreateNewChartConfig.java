package org.csstudio.trends.databrowser;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

@SuppressWarnings("nls")
public class CreateNewChartConfig extends Action implements IObjectActionDelegate
{
    /** The currently selected container */
    private IContainer container = null;

    public CreateNewChartConfig()
    {
    }

    /* @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
        // TODO Auto-generated method stub
        
    }

    /* @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action)
    {
        System.out.println("CreateNewChartConfig NEW CHART");
    }

    /* @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection)
    {
        System.out.println("CreateNewChartConfig");
        if (selection instanceof IStructuredSelection)
        {
            IStructuredSelection sel = (IStructuredSelection) selection;
            Object item = sel.getFirstElement();
            if (item instanceof IContainer)
            {
                container = (IContainer) item;
                return;
            }
        }
        // No container selected
        container = null;
   }
}
