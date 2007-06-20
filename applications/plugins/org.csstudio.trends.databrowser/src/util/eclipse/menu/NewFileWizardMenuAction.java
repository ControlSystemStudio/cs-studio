package util.eclipse.menu;

import org.csstudio.util.wizard.NewFileWizard;
import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/** Action to run a wizard based on NewFileWizard from a menu.
 *  <p>
 *  Can be hooked into navigator or workspace explorer context menu
 *  via object contrib to IContainer, or into the file/new menu.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class NewFileWizardMenuAction extends Action
    implements IObjectActionDelegate, // for context menu
               IWorkbenchWindowActionDelegate // for main menu
{
    @SuppressWarnings("unchecked")
    final private Class new_file_wizard;
    
    /** The currently active workbench window */
    private IWorkbenchWindow window = null;

    /** Selection for the container */
    private IStructuredSelection selection = null;
    
    /** Constructor.
     *  @param new_file_wizard Class that implements NewFileWizard
     */
    @SuppressWarnings("unchecked")
    public NewFileWizardMenuAction(Class new_file_wizard)
    {
        this.new_file_wizard = new_file_wizard;
    }

    /** Keep track of active window in case this is a IWorkbenchWindowActionDelegate.
     *  @see IWorkbenchWindowActionDelegate
     */
    public void init(IWorkbenchWindow window)
    {
        this.window = window;
    }
    
    /** Keep track of active window in case this is a IObjectActionDelegate.
     *  @see IObjectActionDelegate
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
        this.window = targetPart.getSite().getWorkbenchWindow();
    }

    /** @see IWorkbenchWindowActionDelegate */
    public void dispose()
    {
        window = null;
        selection = null;
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

    /** Display the new chart editor wizard */
    public void run(IAction action)
    {
        NewFileWizard wizard;
        try
        {
            wizard = (NewFileWizard) new_file_wizard.newInstance();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return;
        }
        // Compare Plugin book p. 439 for how to run a wizard manually
        IStructuredSelection selection_to_use = selection;
        // After peeking into NewWizardAction, it looks like
        // the selection must be non-null....
        if (selection_to_use == null)
            selection_to_use = StructuredSelection.EMPTY;
        wizard.init(window.getWorkbench(), selection_to_use);
        WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
        dialog.open();
    }
}
