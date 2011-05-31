/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser.opiwidget;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

/** Base for actions that act on {@link DataBrowserWidgedEditPart}
 *  @author Kay Kasemir
 */
abstract public class DataBrowserWidgetAction implements IObjectActionDelegate
{
    private IWorkbenchPage page = null;
    private DataBrowserWidgedEditPart edit_part = null;

    /** Track the active workbench page */
    @Override
    public void setActivePart(final IAction action, final IWorkbenchPart part)
    {
        page = part.getSite().getPage();
    }

    /** Track the Data Browser configuration filename */
    @Override
    public void selectionChanged(final IAction action, final ISelection selection)
    {
        if (selection instanceof IStructuredSelection)
        {
            final Object o = ((IStructuredSelection)selection).getFirstElement();
            if (o instanceof DataBrowserWidgedEditPart)
            {
                edit_part = (DataBrowserWidgedEditPart)o;
                return;
            }
        }
        edit_part = null;
    }

    /** Open Data Browser */
    @Override
    public void run(final IAction action)
    {
        if (edit_part == null  ||  page == null)
            return;
        doRun(page, edit_part);
    }

    /** Execute action
     *  @param page Currently active workbench page
     *  @param edit_part Currently active edit part
     */
    abstract protected void doRun(IWorkbenchPage page,
            DataBrowserWidgedEditPart edit_part);
}
