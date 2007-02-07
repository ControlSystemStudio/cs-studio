package org.csstudio.diag.rmtcontrol.ui;

import org.csstudio.diag.rmtcontrol.Activator;
import org.csstudio.diag.rmtcontrol.Messages;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ShowRMTControl implements IWorkbenchWindowActionDelegate {

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

	public void run(IAction action) {
			IWorkbench workbench = PlatformUI.getWorkbench();
	        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	        IWorkbenchPage page = window.getActivePage();
	        try {
				page.showView(Activator.PLUGIN_ID);
			} catch (PartInitException e) {
				Activator.logException(Messages.getString("ShowRMTControl.ExceptionPartInit"), e); //$NON-NLS-1$
			}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}


}
