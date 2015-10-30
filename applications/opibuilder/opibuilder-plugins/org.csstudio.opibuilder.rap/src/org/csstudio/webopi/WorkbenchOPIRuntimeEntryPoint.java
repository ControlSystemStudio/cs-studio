package org.csstudio.webopi;

import org.csstudio.rap.core.DisplayManager;
import org.csstudio.webopi.util.RequestUtil;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;

public class WorkbenchOPIRuntimeEntryPoint implements EntryPoint {

    public int createUI() {
        RequestUtil.initDefaultDatasource();
        Display display = PlatformUI.createDisplay();
        RequestUtil.login(display);
        DisplayManager.getInstance().registerDisplay(display, true);
        WorkbenchAdvisor advisor = new ApplicationWorkbenchAdvisor();
        return PlatformUI.createAndRunWorkbench(display, advisor);

    }

}
