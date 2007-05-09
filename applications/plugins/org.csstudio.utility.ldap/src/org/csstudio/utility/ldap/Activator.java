package org.csstudio.utility.ldap;
//
import org.csstudio.platform.AbstractCssPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractCssPlugin {
//public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.utility.ldap";

	//	 The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.platform.AbstractCssPlugin#doStart(org.osgi.framework.BundleContext)
	 */
	@Override
	protected void doStart(BundleContext context) throws Exception {	}

	/* (non-Javadoc)
	 * @see org.csstudio.platform.AbstractCssPlugin#doStop(org.osgi.framework.BundleContext)
	 */
	@Override
	protected void doStop(BundleContext context) throws Exception {	}

	/* (non-Javadoc)
	 * @see org.csstudio.platform.AbstractCssPlugin#getPluginId()
	 */
	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}

	public static Activator getDefault() {
		return plugin;
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
