package org.csstudio.utility.pvmanager.ui;

import org.eclipse.ui.PlatformUI;
import org.epics.pvmanager.ThreadSwitch;

public class SWTUtil {
	private SWTUtil() {
		// Prevent creation
	}
	
	private static ThreadSwitch SWTThread = new ThreadSwitch() {

        @Override
        public void post(Runnable task) {
            try {
            	PlatformUI.getWorkbench().getDisplay().asyncExec(task);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    };
    
	public static ThreadSwitch onSWTThread() {
		return SWTThread;
	}

}
