package org.csstudio.diag.interconnectionServer;

import org.csstudio.platform.AbstractCssPlugin;
import org.csstudio.startup.IStartupServiceListener;
import org.csstudio.startup.StartupServiceEnumerator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractCssPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.diag.interconnectionServer";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	@Override
	protected void doStart(BundleContext context) throws Exception {
		for (IStartupServiceListener s : StartupServiceEnumerator.getServices()) {
			s.run();
		}
		
	}

	@Override
	protected void doStop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPluginId() {
		// TODO Auto-generated method stub
		return PLUGIN_ID;
	}

    /** Add informational message to the plugin log. */
    public static void logInfo(String message)
    {
        getDefault().log(IStatus.INFO, message, null);
    }

    /** Add error message to the plugin log. */
    public static void logError(String message)
    {
        getDefault().log(IStatus.ERROR, message, null);
    }

    /** Add an exception to the plugin log. */
    public static void logException(String message, Exception e)
    {
        getDefault().log(IStatus.ERROR, message, e);
    }

    /** Add a message to the log.
     * @param type
     * @param message
     */
    private void log(int type, String message, Exception e)
    {
        getLog().log(new Status(type, PLUGIN_ID, IStatus.OK, message, e));
    }
	
}
