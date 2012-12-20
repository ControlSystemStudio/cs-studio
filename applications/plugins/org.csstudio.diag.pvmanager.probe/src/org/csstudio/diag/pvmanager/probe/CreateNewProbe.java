package org.csstudio.diag.pvmanager.probe;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Command handler for opening probe on the current selection.
 * 
 * @author carcassi
 */
public class CreateNewProbe extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			// Create a new instance of the multiple view
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			PVManagerProbe newProbe = (PVManagerProbe) page.showView(
					PVManagerProbe.MULTIPLE_VIEW_ID,
					PVManagerProbe.createNewInstance(),
					IWorkbenchPage.VIEW_ACTIVATE);
			
			// Look at the pv name and set it to the new view
			PVManagerProbe oldProbe = (PVManagerProbe) page
					.showView(PVManagerProbe.SINGLE_VIEW_ID);
			if (oldProbe.getPVName() != null) {
				newProbe.setPVName(oldProbe.getPVName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
