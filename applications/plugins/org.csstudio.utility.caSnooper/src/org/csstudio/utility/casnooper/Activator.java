package org.csstudio.utility.casnooper;

import org.csstudio.platform.AbstractCssPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.remotercp.common.tracker.GenericServiceTracker;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractCssPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(Activator.class);
    
	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.utility.caSnooper";

	// The shared instance
	private static Activator plugin;
	
	private GenericServiceTracker<ISessionService> _genericServiceTracker;

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
	
		_genericServiceTracker = new GenericServiceTracker<ISessionService>(
				context, ISessionService.class);
		_genericServiceTracker.open();
	}

	@Override
	protected void doStop(BundleContext context) throws Exception {
		LOG.info("CaSnooper stopped"); 
		_genericServiceTracker.close();
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

	public void addSessionServiceListener(
			IGenericServiceListener<ISessionService> sessionServiceListener) {
		_genericServiceTracker.addServiceListener(sessionServiceListener);
	}
}
