package org.csstudio.sns.product;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/** Configure the workbench window.
 *  @author Kay Kasemir
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{

    public ApplicationWorkbenchWindowAdvisor(
                    IWorkbenchWindowConfigurer configurer)
    {
        super(configurer);
    }
    
    /** Set initial workbench window size and title */
    @Override
    public void preWindowOpen()
    {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(1024, 768));
        configurer.setShowMenuBar(true);
        configurer.setShowPerspectiveBar(true);
        configurer.setShowCoolBar(true);
        configurer.setShowFastViewBars(true);
        configurer.setShowProgressIndicator(true);
        configurer.setShowStatusLine(true);
        configurer.setTitle(Messages.Window_Title);
    }

	@Override
    public ActionBarAdvisor createActionBarAdvisor(
                    IActionBarConfigurer configurer)
    {
        return new ApplicationActionBarAdvisor(configurer);
    }
}
