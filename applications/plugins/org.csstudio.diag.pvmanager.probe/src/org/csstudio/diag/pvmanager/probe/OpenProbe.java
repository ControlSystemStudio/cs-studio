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
public class OpenProbe extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
					.getActivePage().getSelection();

			// Open a new probe
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			PVManagerProbe probe = (PVManagerProbe) page.showView(
					PVManagerProbe.ID, PVManagerProbe.createNewInstance(),
					IWorkbenchPage.VIEW_ACTIVATE);

			// If selection contains IProcessVairables, open the first one
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection strucSelection = (IStructuredSelection) selection;
				if (!strucSelection.isEmpty()) {
					IProcessVariable variable = (IProcessVariable) strucSelection
							.iterator().next();
					probe.setPVName(variable.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
