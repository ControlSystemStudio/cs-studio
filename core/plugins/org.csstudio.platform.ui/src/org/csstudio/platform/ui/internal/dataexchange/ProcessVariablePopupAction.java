package org.csstudio.platform.ui.internal.dataexchange;

import java.util.Iterator;
import java.util.Vector;

import org.csstudio.platform.model.IProcessVariable;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/** Abstract IObjectActionDelegate for IProcessVariableName selections.
 *  <p>
 *  When defining a popupMenu objectContribution, one needs to implement
 *  IObjectActionDelegate.
 *  This class helps with finding all the PV names in the current selection,
 *  so one only needs to implement <code>handlePV</code>.
 *  @author Kay Kasemir
 */
public abstract class ProcessVariablePopupAction implements IObjectActionDelegate
{    
    /** The most recent selection. */
    private ISelection selection;
    
    /** @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart) */
    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {}

    /** Memorize the current selection.
     *  @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection) */
    public void selectionChanged(IAction action, ISelection selection)
    {
        this.selection = selection;
    }
 
    /** This action was activated.
     *  <p>
     *  Find all PV names in the current selection,
     *  invoke <code>handlePV</code> for each one.
     *  <p>
     *  @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction) */
    public void run(IAction action)
    {
        if (! (selection instanceof IStructuredSelection))
            return;
        Iterator iter = ((IStructuredSelection)selection).iterator();
        Vector<IProcessVariable> pv_names = new Vector<IProcessVariable>();
        while (iter.hasNext())
        {
            Object element = iter.next();
            IProcessVariable pv = null;
            if (element instanceof IProcessVariable)
                pv = (IProcessVariable) element;
            else if (element instanceof IAdaptable)
                pv = (IProcessVariable)
                ((IAdaptable)element).getAdapter(IProcessVariable.class);
            if (pv != null)
                pv_names.add(pv);
        }
        if (pv_names.size() > 0)
        {
            IProcessVariable pvs[] = 
                new IProcessVariable[pv_names.size()];
            handlePVs(pv_names.toArray(pvs));
        }
    }
    
    /** Handle the received PV names.
     *  <p>
     *  This method will be invoked with all the received PV names.
     *  Typically, a derived class would open the view or create
     *  an editor, in which that received PVs are then displayed
     *  or analyzed or ...
     *  @param pv_name Array of received PV names.
     */
    abstract public void handlePVs(IProcessVariable pv_names[]);
}
