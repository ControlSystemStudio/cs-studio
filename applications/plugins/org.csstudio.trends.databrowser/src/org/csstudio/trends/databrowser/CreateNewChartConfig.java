package org.csstudio.trends.databrowser;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

/** Run the NewChartEditorWizard.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CreateNewChartConfig extends Action implements IObjectActionDelegate
{
    /** The currently active workbench part */
    private IWorkbenchPart part = null;

    /** Selection for the container */
    private IStructuredSelection selection = null;
    
    /** Keep track of active workbench part.
     *  @see IObjectActionDelegate
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
        this.part = targetPart;
    }

    /** Display the new chart editor wizard */
    public void run(IAction action)
    {
        // Compare Plugin book p. 439 for how to run a wizard manually
        final IWorkbenchWindow window =
            part.getSite().getWorkbenchWindow();
        NewChartEditorWizard wizard = new NewChartEditorWizard();
        wizard.init(window.getWorkbench(), selection);
        WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
        dialog.open();
    }

    /** Keep track of active selection.
     *  @see IObjectActionDelegate
     */
    public void selectionChanged(IAction action, ISelection selection)
    {
        if (selection instanceof IStructuredSelection)
        {
            IStructuredSelection sel = (IStructuredSelection) selection;
            Object item = sel.getFirstElement();
            if (item instanceof IContainer)
            {
                this.selection = sel;
                return;
            }
        }
        // No container selected
        this.selection = null;
   }
}
