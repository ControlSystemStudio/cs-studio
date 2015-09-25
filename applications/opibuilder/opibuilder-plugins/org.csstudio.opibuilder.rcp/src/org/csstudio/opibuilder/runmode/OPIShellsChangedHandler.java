package org.csstudio.opibuilder.runmode;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Handler to catch the OPIShellsChanged command and tell the
 * OPIShellSummary view to update.
 *
 * @author Will Rogers
 *
 */
public class OPIShellsChangedHandler extends AbstractHandler {

    private static Logger log = OPIBuilderPlugin.getLogger();

    private static OPIShellSummary summaryView = null;

    @Override
    public Object execute(ExecutionEvent ee) throws ExecutionException {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (summaryView == null || ! page.isPartVisible(summaryView)) {
            try {
                summaryView = (OPIShellSummary) page.showView(OPIShellSummary.ID);
            } catch (PartInitException e) {
                log.log(Level.WARNING, "Failed to open OPI Shell Summary view", e);
            }
        }
        if (summaryView != null) {
            summaryView.update();
        }
        return null;
    }

}
