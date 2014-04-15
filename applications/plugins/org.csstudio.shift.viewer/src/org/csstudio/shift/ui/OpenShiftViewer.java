package org.csstudio.shift.ui;

import gov.bnl.shiftClient.Shift;
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


public class OpenShiftViewer extends AbstractHandler {

    public final static String ID = "org.csstudio.shift.viewer.OpenShiftViewer";

    public OpenShiftViewer() {
	super();
    }

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		ISelection selection;
		if (HandlerUtil.getActiveMenuSelection(event) != null) {
		    selection = HandlerUtil.getActiveMenuSelection(event);
		} else {
		    selection = window.getActivePage().getSelection(ShiftTableView.ID);
		}
		if (selection instanceof IStructuredSelection) {
            final IStructuredSelection strucSelection = (IStructuredSelection) selection;
		    if (strucSelection.getFirstElement() instanceof Shift) {
		    	ShiftViewer.createInstance(new ShiftViewerModel((Shift) strucSelection.getFirstElement()));
		    }
		} else {
			ShiftViewer.createInstance();
		}
		try {
		    workbench.showPerspective(ShiftViewerPerspective.ID, window);
		    window.getActivePage().showView(ShiftTableView.ID);
		} catch (Exception ex) {
		    ExceptionDetailsErrorDialog.openError(HandlerUtil.getActiveShell(event), "Error executing command...", ex);
		}
		return null;
    }
}
