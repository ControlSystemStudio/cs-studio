package org.csstudio.opibuilder.runmode;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OPIShellsChangedHandler extends AbstractHandler {

    private static OPIShellSummary summaryView = null;

    @Override
    public Object execute(ExecutionEvent ee) throws ExecutionException {
        if (summaryView == null || ! PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isPartVisible(summaryView)) {
            try {
                summaryView = (OPIShellSummary) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(OPIShellSummary.ID);
            } catch (PartInitException e) {
                e.printStackTrace();
            }
        }
        summaryView.update();
        return null;
    }

}
