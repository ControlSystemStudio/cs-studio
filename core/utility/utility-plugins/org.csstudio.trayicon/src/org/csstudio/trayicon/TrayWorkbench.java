package org.csstudio.trayicon;

import java.util.Map;

import org.csstudio.startup.application.OpenDocumentEventProcessor;
import org.csstudio.startup.module.WorkbenchExtPoint;
import org.csstudio.utility.product.Workbench;
import org.eclipse.ui.application.WorkbenchAdvisor;

public class TrayWorkbench extends Workbench implements WorkbenchExtPoint {

    /**
     * Creates a workbench advisor that allows minimising the application
     * to the system tray when closing the last workbench window.
     *
     * @param parameters the parameters that may give hints on how to create the advisor
     * @return a new advisor instance
     */
    @Override
    protected WorkbenchAdvisor createWorkbenchAdvisor(final Map<String, Object> parameters) {
        final OpenDocumentEventProcessor openDocProcessor =
                  (OpenDocumentEventProcessor) parameters.get(
                          OpenDocumentEventProcessor.OPEN_DOC_PROCESSOR);
        return new TrayApplicationWorkbenchAdvisor(openDocProcessor);
    }

}
