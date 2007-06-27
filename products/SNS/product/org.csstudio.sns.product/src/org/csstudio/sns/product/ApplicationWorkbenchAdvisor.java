package org.csstudio.sns.product;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/** Tell the workbench how to behave.
 *  @author Kay Kasemir
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor
{
    @Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
                    IWorkbenchWindowConfigurer configurer)
    {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    @Override
    public void initialize(IWorkbenchConfigurer configurer)
    {
        // Per default, state is not preserved (RCP book 5.1.1)
        configurer.setSaveAndRestore(true);
    }

    /** @return ID of initial perspective */
    @Override
    public String getInitialWindowPerspectiveId()
    {
        return CSS_Perspective.ID;
    }
}
