package org.csstudio.alarm.beast.ui;

import org.csstudio.rap.core.DisplayManager;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;

public class WorkbenchWebAlarmEntryPoint implements EntryPoint {



    public int createUI() {
        Display display = PlatformUI.createDisplay();
        DisplayManager.getInstance().registerDisplay(display, true);
        WorkbenchAdvisor advisor = new ApplicationWorkbenchAdvisor();
        int result = PlatformUI.createAndRunWorkbench( display, advisor );
        display.dispose();
        return result;

    }

}
