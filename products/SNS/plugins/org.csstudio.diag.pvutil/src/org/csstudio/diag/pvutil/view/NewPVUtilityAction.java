/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvutil.view;

import java.util.logging.Level;

import org.csstudio.diag.pvutil.Activator;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/** Action connected to workbench menu action set for opening a new editor.
 *  @author Dave Purcell stolen from Kay Kasemir
 */
@SuppressWarnings("nls")
public class NewPVUtilityAction implements IWorkbenchWindowActionDelegate
{
    @Override
    public void init(IWorkbenchWindow window)
    { /* NOP */
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection)
    { /* NOP */
    }

    @Override
    public void run(IAction action)
    {
        try
        {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            page.showView(PVUtilView.ID);
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.SEVERE, "PVUtil activation error", ex);
        }
    }

    @Override
    public void dispose()
    { /* NOP */
    }
}
