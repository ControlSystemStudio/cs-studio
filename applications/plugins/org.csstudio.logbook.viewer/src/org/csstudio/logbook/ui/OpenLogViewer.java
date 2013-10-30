/**
 * 
 */
package org.csstudio.logbook.ui;

import org.csstudio.logbook.LogEntry;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author shroffk
 * 
 */
public class OpenLogViewer extends AbstractHandler {

    public final static String ID = "org.csstudio.logbook.viewer.OpenLogViewer";

    public OpenLogViewer() {
	super();
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
	final IWorkbench workbench = PlatformUI.getWorkbench();
	final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	ISelection selection = HandlerUtil.getCurrentSelection(event);
	if (selection instanceof IStructuredSelection) {
	    IStructuredSelection strucSelection = (IStructuredSelection) selection;
	    if (strucSelection.getFirstElement() instanceof LogEntry) {
		LogViewer.createInstance(new LogViewerModel(
			(LogEntry) strucSelection.getFirstElement()));
	    } else {
		LogViewer.createInstance();
	    }
	} else {
	    LogViewer.createInstance();
	}
	try {
	    workbench.showPerspective(LogViewerPerspective.ID, window);
	} catch (Exception ex) {
	    ExceptionDetailsErrorDialog.openError(
		    HandlerUtil.getActiveShell(event),
		    "Error executing command...", ex);
	}
	return null;
    }
}
