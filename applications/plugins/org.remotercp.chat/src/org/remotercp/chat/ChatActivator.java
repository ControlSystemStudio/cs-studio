package org.remotercp.chat;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * The activator class controls the plug-in life cycle
 */
public class ChatActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.remotercp.chat";

	// The shared instance
	private static ChatActivator plugin;

	private BundleContext context;

	/**
	 * The constructor
	 */
	public ChatActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		this.context = context;

	}

	@SuppressWarnings("unchecked")
	public <T> T getService(Class<T> service) {
		ServiceReference serviceReference = this.context
				.getServiceReference(service.getName());
		if (serviceReference != null) {
			T foundService = (T) context.getService(serviceReference);
			return foundService;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
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
	public static ChatActivator getDefault() {
		return plugin;
	}

}
