package org.csstudio.diag.pvmanager.probe;

import org.csstudio.diag.pvmanager.probe.views.PVManagerProbe;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class OpenProbe extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try
	    {
	        IWorkbench workbench = PlatformUI.getWorkbench();
	        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	        IWorkbenchPage page = window.getActivePage();
	        page.showView(PVManagerProbe.ID, PVManagerProbe.createNewInstance(),
                          IWorkbenchPage.VIEW_ACTIVATE);
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
		return null;
	}

}
