package org.csstudio.webopi;

import javax.servlet.http.HttpServletRequest;

import org.csstudio.opibuilder.runmode.OPIRunnerPerspective;
import org.csstudio.opibuilder.runmode.StandaloneRuntimePerspective;
import org.eclipse.rwt.RWT;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * This workbench advisor creates the window advisor, and specifies
 * the perspective id for the initial window.
 */
public class StandaloneApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new StandaloneApplicationWorkbenchWindowAdvisor(configurer);
    }

	public String getInitialWindowPerspectiveId() {		
			 return StandaloneRuntimePerspective.ID;
	}
}
