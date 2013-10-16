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
import org.csstudio.opibuilder.editor.OPIEditorPerspective;
import org.csstudio.opibuilder.util.E4Utils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**The action that opens the OPI Editor perspective
 * @author Xihui Chen
 */
public class OpenOPIPerspectiveAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	public void dispose() {
	    // NOP
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		if(window == null)
			window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final IWorkbenchPage page = window.getActivePage();
		final String current = page.getPerspective().getId();
		if (current.equals(OPIEditorPerspective.ID))
		{
		    if (MessageDialog.openQuestion(window.getShell(),
		            "Reset OPI Editor Perspective?",
		            "You are already in OPI Editor perspective.\n" +
		            "Do you want to reset it to default arrangement?"))
		        page.resetPerspective();
		}else			
			E4Utils.showPerspective(OPIEditorPerspective.ID, window.getActivePage());
	}

	public void selectionChanged(IAction action, ISelection selection) {
	    // NOP
	}
}
