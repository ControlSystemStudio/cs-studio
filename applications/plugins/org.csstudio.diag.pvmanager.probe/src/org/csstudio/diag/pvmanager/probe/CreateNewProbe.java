package org.csstudio.diag.pvmanager.probe;

import org.csstudio.diag.pvmanager.probe.views.PVManagerProbe;
import org.csstudio.platform.model.IProcessVariable;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Command handler for opening probe on the current selection.
 * 
 * @author carcassi
 */
public class CreateNewProbe extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			// Retrieve the selection and the current page
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			page.showView(PVManagerProbe.MULTIPLE_VIEW_ID,
					PVManagerProbe.createNewInstance(),
					IWorkbenchPage.VIEW_ACTIVATE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
