/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvutil.view;

import org.csstudio.platform.logging.CentralLogger;
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
public class NewPVUtilityAction implements IWorkbenchWindowActionDelegate
{
    public void init(IWorkbenchWindow window)
    { /* NOP */
    }

    public void selectionChanged(IAction action, ISelection selection)
    { /* NOP */
    }

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
        	CentralLogger.getInstance().getLogger(this).error("Exception", ex);
        }
    }

    public void dispose()
    { /* NOP */
    }
}
