
package org.csstudio.nams.configurator.views;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

public class ShowAMS extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	    try {
	        workbench.showPerspective("org.csstudio.nams.newconfigurator.perspective", window);
	    } catch (WorkbenchException e) {
			e.printStackTrace();
		}
	    return null;
	}
}
