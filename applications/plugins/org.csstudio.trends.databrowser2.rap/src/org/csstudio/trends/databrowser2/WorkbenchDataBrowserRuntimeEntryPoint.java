package org.csstudio.trends.databrowser2;

import org.csstudio.rap.core.DisplayManager;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;

public class WorkbenchDataBrowserRuntimeEntryPoint implements IEntryPoint {

	

	public int createUI() {
		Display display = PlatformUI.createDisplay();
		DisplayManager.getInstance().registerDisplay(display, true);
		WorkbenchAdvisor advisor = new ApplicationWorkbenchAdvisor();
	    int result = PlatformUI.createAndRunWorkbench( display, advisor );
	    display.dispose();
		return result;
		
	}

}
