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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

/** Command handler for opening PV Tree on the current selection.
 *  Linked from popup menu that is sensitive to {@link ProcessVariable}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class OpenPVTree extends AbstractHandler implements IHandler
{
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        // Open (new) PV Tree view
        final PVTreeView view;
        try
        {
            final IWorkbenchPage page = HandlerUtil.getActiveSite(event).getPage();
            view = (PVTreeView) page.showView(PVTreeView.ID, PVTreeView.newInstance(), IWorkbenchPage.VIEW_ACTIVATE);
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(HandlerUtil.getActiveShell(event),
                        "Cannot open PVTreeView" , ex);
            return null;
        }

        // If there is a currently selected PV
        // (because this was invoked from a context menu),
        // use that PV
        final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        final ProcessVariable[] pvs = AdapterUtil.convert(selection, ProcessVariable.class);
        if (pvs != null  &&  pvs.length > 0)
            view.setPVName(pvs[0].getName());
        return null;
    }
}
