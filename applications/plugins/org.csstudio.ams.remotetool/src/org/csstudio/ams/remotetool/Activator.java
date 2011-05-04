package org.csstudio.ams.remotetool;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.remotercp.common.tracker.GenericServiceTracker;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.ams.remotetool";

	// The shared instance
	private static Activator plugin;
	
	private GenericServiceTracker<ISessionService> _genericServiceTracker;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		_genericServiceTracker = new GenericServiceTracker<ISessionService>(
				context, ISessionService.class);
		_genericServiceTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public void addSessionServiceListener(
			IGenericServiceListener<ISessionService> sessionServiceListener) {
		_genericServiceTracker.addServiceListener(sessionServiceListener);
	}
}
