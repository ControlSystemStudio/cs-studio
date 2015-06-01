/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**The action that opens the Navigator
 * @author Xihui Chen
 */
public class ShowNavigatorAction implements IObjectActionDelegate {

    private IWorkbenchWindow window;

    @Override
    @SuppressWarnings("deprecation")
    public void run(IAction action) {
        try {
            if(window == null)
                window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            final IWorkbenchPage page = window.getActivePage();
            page.showView(IPageLayout.ID_RES_NAV);
        } catch (WorkbenchException e) {
            final String message = NLS.bind(
                    "Failed to open navigator. \n{0}", e.getMessage());
            MessageDialog.openError(null, "Error",
                        message);
            OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        window=targetPart.getSite().getWorkbenchWindow();
    }
}
