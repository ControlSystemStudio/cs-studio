/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.AdapterUtil;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

/** Command handler for opening PV Tree on the current selection.
 *  Linked from popup menu that is sensitive to {@link ProcessVariable}
 *  @author Kay Kasemir
 */
public class OpenPVTree extends AbstractHandler implements IHandler
{
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        try
        {
            PVTreeView view = openView(event);

            // If there is a currently selected PV
            // (because this was invoked from a context menu),
            // use that PV
            final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
            final ProcessVariable[] pvs = AdapterUtil.convert(selection, ProcessVariable.class);
            if (pvs == null)
                return null;
            
            if (pvs.length > 5 &&
                ! MessageDialog.openConfirm(
                        HandlerUtil.getActiveShell(event),
                        Messages.ManyPVs,
                        NLS.bind(Messages.ManyPVConfirmFmt, pvs.length)))
                return null;
                    
            // Set PV name(s)
            for (int i=0; i<pvs.length; ++i)
            {
                view.setPVName(pvs[i].getName());
                // Open new tree for each additional PV
                if (i < pvs.length-1)
                    view = openView(event);
            }
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(HandlerUtil.getActiveShell(event),
                        "Cannot open PVTreeView" , ex); //$NON-NLS-1$
        }
        return null;
    }
    
    /** Open (new) PV Tree view
     *  @param event
     *  @return {@link PVTreeView}
     *  @throws Exception
     */
    private PVTreeView openView(final ExecutionEvent event) throws Exception
    {
        final IWorkbenchPage page = HandlerUtil.getActiveSite(event).getPage();
        return (PVTreeView) page.showView(PVTreeView.ID, PVTreeView.newInstance(), IWorkbenchPage.VIEW_ACTIVATE);
    }
}
