package org.csstudio.management.internal.xmpplogindialog;


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
	public static final String PLUGIN_ID = "org.csstudio.management.xmpplogindialog";

	// The shared instance
	private static Activator plugin;

	private GenericServiceTracker<ISessionService> _genericServiceTracker;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
		_genericServiceTracker = new GenericServiceTracker<ISessionService>(context, ISessionService.class);
		_genericServiceTracker.open();
		plugin = this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		_genericServiceTracker.close();
		plugin = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}


	public void addSessionServiceListener(IGenericServiceListener<ISessionService> sessionServiceListener) {
		_genericServiceTracker.addServiceListener(sessionServiceListener);
	}
}
