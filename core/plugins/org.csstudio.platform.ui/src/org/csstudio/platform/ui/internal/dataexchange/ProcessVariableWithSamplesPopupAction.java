/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
