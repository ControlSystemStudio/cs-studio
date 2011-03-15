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
			// Retrieve the selection and the current page
			ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
					.getActivePage().getSelection();
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();

			if (selection instanceof IStructuredSelection) {
				IStructuredSelection strucSelection = (IStructuredSelection) selection;
				
				// If it's a single selection, open it in the single view instance
				if (strucSelection.size() == 1) {
					IProcessVariable variable = (IProcessVariable) strucSelection
							.iterator().next();
					PVManagerProbe probe = (PVManagerProbe) page.showView(
							PVManagerProbe.SINGLE_VIEW_ID);
					probe.setPVName(variable.getName());
					
				// If it's a multiple selection, open a new multiple view instance
				// for each element
				} else if (strucSelection.size() > 1) {
					for (Object item : strucSelection.toList()) {
						if (item instanceof IProcessVariable) {
							PVManagerProbe probe = (PVManagerProbe) page
									.showView(PVManagerProbe.MULTIPLE_VIEW_ID,
											PVManagerProbe.createNewInstance(),
											IWorkbenchPage.VIEW_ACTIVATE);
							probe.setPVName(((IProcessVariable) item).getName());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
