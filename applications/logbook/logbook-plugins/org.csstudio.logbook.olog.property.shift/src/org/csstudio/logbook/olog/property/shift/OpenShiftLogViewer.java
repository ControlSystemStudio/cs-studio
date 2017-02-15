/**
 *
 */
package org.csstudio.logbook.olog.property.shift;

import gov.bnl.shiftClient.Shift;

import org.csstudio.logbook.ui.LogTreeView;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author shroffk
 *
 */
public class OpenShiftLogViewer extends AbstractHandler {

    public final static String ID = "org.csstudio.logbook.viewer.OpenShiftLogViewer";
    private String searchString = "";

    public OpenShiftLogViewer() {
        super();
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        try {
            IWorkbenchPage page = window.getActivePage();
            LogTreeView logTreeView = (LogTreeView) page.showView(LogTreeView.ID);

            ISelection selection = HandlerUtil.getCurrentSelection(event);
            if (selection instanceof IStructuredSelection) {
                IStructuredSelection strucSelection = (IStructuredSelection) selection;
                if (strucSelection.getFirstElement() instanceof Shift) {
                    searchString = "* " + ShiftPropertyWidget.propertyName + "." + ShiftPropertyWidget.attrIdName + ":"
                            + ((Shift) strucSelection.getFirstElement()).getId();
                } else {

                }
            } else {
                // TODO invalid selection
            }
            if (logTreeView != null) {
                logTreeView.setSearchString(searchString);
            }
        } catch (Exception ex) {
            ExceptionDetailsErrorDialog.openError(HandlerUtil.getActiveShell(event), "Error executing command...", ex);
        }
        return null;
    }
}
