package org.csstudio.webopi;

import org.csstudio.rap.core.DisplayManager;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;

public class WorkbenchOPIRuntimeEntryPoint implements IEntryPoint {

	

	public int createUI() {
		Display display = PlatformUI.createDisplay();
		DisplayManager.getInstance().registerDisplay(display, true);
		WorkbenchAdvisor advisor = new ApplicationWorkbenchAdvisor();
		return PlatformUI.createAndRunWorkbench( display, advisor );
		
	}

}
