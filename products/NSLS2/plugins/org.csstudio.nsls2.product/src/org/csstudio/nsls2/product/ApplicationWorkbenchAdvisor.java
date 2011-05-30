package org.csstudio.nsls2.product;

import org.csstudio.nsls2.product.CSSPerspective;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

//	private static final String PERSPECTIVE_ID = "org.csstudio.nsls2.product.perspective";

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }
    
    @Override
    public void initialize(IWorkbenchConfigurer configurer)
    {
        // Per default, state is not preserved (RCP book 5.1.1)
        configurer.setSaveAndRestore(true);
    }

	public String getInitialWindowPerspectiveId() {
		return CSSPerspective.ID;
	}
}
