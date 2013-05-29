/**
 * 
 */
package org.csstudio.logbook.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author shroffk
 * 
 */
public class OpenLogViewer extends AbstractHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
	LogViewer part = LogViewer.createInstance();
	try {
	    final IWorkbench workbench = PlatformUI.getWorkbench();
	    final IWorkbenchWindow window = workbench
		    .getActiveWorkbenchWindow();
	    workbench.showPerspective(LogViewerPerspective.ID, window);
	} catch (Exception ex) {
	    // never mind
	}
	return null;
    }

}
