package org.csstudio.platform.ui.internal.dataexchange;

import java.util.Iterator;
import java.util.Vector;

import org.csstudio.platform.model.IProcessVariableWithSamples;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/** Abstract IObjectActionDelegate for IProcessVariableWithSamples selections.
 *  (Using the design of the datatype implementations from Kay Kasemir)
 *  <p>
 *  When defining a popupMenu objectContribution, one needs to implement
 *  IObjectActionDelegate.
 *  This class helps with finding all the PV names in the current _selection,
 *  so one only needs to implement <code>handlePV</code>.
 *  @author Kay Kasemir
 */
public abstract class ProcessVariableWithSamplesPopupAction implements IObjectActionDelegate
{    
    /** The most recent _selection. */
    private ISelection _selection;
    
    /** @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart) */
    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {}

    /** Memorize the current _selection.
     *  @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection) */
    public void selectionChanged(IAction action, ISelection selection)
    {
        this._selection = selection;
    }
 
    /** This action was activated.
     *  <p>
     *  Find all PV names in the current _selection,
     *  invoke <code>handlePV</code> for each one.
     *  <p>
     *  @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction) */
    public void run(IAction action)
    {
        if (! (_selection instanceof IStructuredSelection))
            return;
        Iterator iter = ((IStructuredSelection)_selection).iterator();
        Vector<IProcessVariableWithSamples> pv_names = new Vector<IProcessVariableWithSamples>();
        while (iter.hasNext())
        {
            Object element = iter.next();
            IProcessVariableWithSamples pv = null;
            if (element instanceof IProcessVariableWithSamples)
                pv = (IProcessVariableWithSamples) element;
            else if (element instanceof IAdaptable)
                pv = (IProcessVariableWithSamples)
                ((IAdaptable)element).getAdapter(IProcessVariableWithSamples.class);
            if (pv != null)
                pv_names.add(pv);
        }
        if (pv_names.size() > 0)
        {
            IProcessVariableWithSamples pvs[] = 
                new IProcessVariableWithSamples[pv_names.size()];
            handlePVs(pv_names.toArray(pvs));
        }
    }
    
    /** Handle the received PVWithSamples.
     *  <p>
     *  This method will be invoked with all the received PV names.
     *  Typically, a derived class would open the view or create
     *  an editor, in which that received PVs are then displayed
     *  or analyzed or ...
     *  @param pv_name Array of received PVWithSamples.
     */
    abstract public void handlePVs(IProcessVariableWithSamples pv_names[]);
}
