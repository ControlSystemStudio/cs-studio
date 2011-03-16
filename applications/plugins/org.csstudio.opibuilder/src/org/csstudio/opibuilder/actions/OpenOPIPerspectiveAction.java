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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
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
		try {
			PlatformUI.getWorkbench().showPerspective(
			        OPIEditorPerspective.ID, window);
		} catch (WorkbenchException e) {
			final String message = NLS.bind(
					"Failed to open OPI Editor perspective. \n{0}", e.getMessage());
			MessageDialog.openError(null, "Error",
						message);
			OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	    // NOP
	}
}
