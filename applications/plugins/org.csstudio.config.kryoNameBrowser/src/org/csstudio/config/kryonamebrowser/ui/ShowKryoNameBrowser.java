package org.csstudio.config.kryonamebrowser.ui;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ShowKryoNameBrowser extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
	        IWorkbench workbench = PlatformUI.getWorkbench();
	        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	        IWorkbenchPage page = window.getActivePage();
	        try {
				page.showView(MainView.ID);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
	    return null;
	}
}
