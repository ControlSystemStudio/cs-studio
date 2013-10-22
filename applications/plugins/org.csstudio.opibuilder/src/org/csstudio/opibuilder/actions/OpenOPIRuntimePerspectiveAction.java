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
import org.csstudio.opibuilder.runmode.OPIRunnerPerspective;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**The action that opens the OPI Runtime perspective
 * @author Xihui Chen
 */
public class OpenOPIRuntimePerspectiveAction implements IObjectActionDelegate {

	private IWorkbenchWindow window;

	public void run(IAction action) {
		try {
			if(window == null)
				window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			final IWorkbenchPage page = window.getActivePage();
			final String current = page.getPerspective().getId();
            if (current.equals(OPIRunnerPerspective.ID))
            {
                if (MessageDialog.openQuestion(window.getShell(),
                        "Reset OPI Runtime Perspective?",
                        "You are already in OPI Runtime perspective.\n" +
                        "Do you want to reset it to default arrangement?"))
                    page.resetPerspective();
            }else			
            	PlatformUI.getWorkbench().showPerspective(
            			OPIRunnerPerspective.ID, window);
		} catch (WorkbenchException e) {
			final String message = NLS.bind(
					"Failed to open OPI Runtime perspective. \n{0}", e.getMessage());
			MessageDialog.openError(null, "Error",
						message);
			OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		window=targetPart.getSite().getWorkbenchWindow();
	}
}
