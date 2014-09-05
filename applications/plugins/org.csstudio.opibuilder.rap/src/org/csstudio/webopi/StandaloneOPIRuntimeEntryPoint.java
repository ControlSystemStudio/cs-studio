package org.csstudio.webopi;

import org.csstudio.rap.core.DisplayManager;
import org.csstudio.webopi.util.RequestUtil;
import org.eclipse.rap.rwt.application.IEntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;

public class StandaloneOPIRuntimeEntryPoint implements IEntryPoint {

	public int createUI() {
		Display display = PlatformUI.createDisplay();
		RequestUtil.login(display);
		DisplayManager.getInstance().registerDisplay(display, true);
		WorkbenchAdvisor advisor = new StandaloneApplicationWorkbenchAdvisor();
		return PlatformUI.createAndRunWorkbench( display, advisor );
	}

}
