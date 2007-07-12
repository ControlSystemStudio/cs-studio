package org.csstudio.platform.ui.internal.actions;

import org.csstudio.platform.security.SecurityFacade;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Action to initiate user login via JAAS.
 * 
 * @author Joerg Rathlev
 */
public class LoginAction implements IWorkbenchWindowActionDelegate {

	/**
	 * Performs user login.
	 */
	public void run(IAction action) {
		SecurityFacade.getInstance().authenticateApplicationUser();
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
