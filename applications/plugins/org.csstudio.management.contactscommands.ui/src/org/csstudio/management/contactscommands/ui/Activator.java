package org.csstudio.management.contactscommands.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.remotercp.common.tracker.GenericServiceTracker;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in COMMAND_ID
	public static final String PLUGIN_ID = "org.csstudio.management.contactscommands.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private GenericServiceTracker<ISessionService> managementCommmandTracker;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		managementCommmandTracker = new GenericServiceTracker<ISessionService>(
				context, ISessionService.class);
		managementCommmandTracker.open();
	}

	public void addSessionServiceListener(
			IGenericServiceListener<ISessionService> listener) {
		managementCommmandTracker.addServiceListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);

		managementCommmandTracker.close();
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
