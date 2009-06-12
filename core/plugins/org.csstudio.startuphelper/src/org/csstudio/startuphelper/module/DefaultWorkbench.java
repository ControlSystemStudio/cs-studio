package org.csstudio.startuphelper.module;

import java.util.Map;

import org.csstudio.platform.ui.workbench.CssWorkbenchAdvisor;
import org.csstudio.startup.module.WorkbenchExtPoint;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * <code>DefaultWorkbench</code> is the default implementation of the
 * {@link WorkbenchExtPoint} which runs the workbench using the 
 * {@link CssWorkbenchAdvisor} and transforms the exit code according to the 
 * system properties.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class DefaultWorkbench implements WorkbenchExtPoint {
	/*
	 * (non-Javadoc)
	 * @see org.csstudio.startup.extensions.RunWorkbenchExtPoint#afterWorkbenchCreation(org.eclipse.swt.widgets.Display, org.eclipse.equinox.app.IApplicationContext, java.util.Map)
	 */
	public Object afterWorkbenchCreation(Display display, IApplicationContext context, Map<String, Object> parameters) throws Exception {
		//do nothing
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.csstudio.startup.extensions.RunWorkbenchExtPoint#beforeWorkbenchCreation(org.eclipse.swt.widgets.Display, org.eclipse.equinox.app.IApplicationContext, java.util.Map)
	 */
	public Object beforeWorkbenchCreation(Display display, IApplicationContext context, Map<String, Object> parameters) throws Exception {
		//do nothing
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.csstudio.startup.extensions.RunWorkbenchExtPoint#runWorkbench(org.eclipse.swt.widgets.Display, org.eclipse.equinox.app.IApplicationContext, java.util.Map)
	 */
	public Object runWorkbench(Display display, IApplicationContext context, Map<String, Object> parameters) throws Exception {
		int returnCode = PlatformUI.createAndRunWorkbench(display, new CssWorkbenchAdvisor());
		if (returnCode == PlatformUI.RETURN_RESTART) {
        	// Something called IWorkbench.restart().
            // Is this supposed to be a RESTART or RELAUNCH?
            final Integer exitCode =
                Integer.getInteger(RelaunchConstants.PROP_EXIT_CODE);
            if (IApplication.EXIT_RELAUNCH.equals(exitCode)) {
            	// RELAUCH with new command line
                return IApplication.EXIT_RELAUNCH;
            }
            // RESTART without changes
            return IApplication.EXIT_RESTART;
        }
        // Plain exit from IWorkbench.close()
        return IApplication.EXIT_OK;
	}	
}
