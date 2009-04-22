package org.csstudio.management.internal.remotecommands;

import org.csstudio.platform.AbstractCssPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractCssPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.platform.remotemanagement";

	// The shared instance
	private static Activator plugin;
	
	private BundleContext bundleContext;
	
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/**
	 * {@inheritDoc}
	 */
	public void doStart(BundleContext context) throws Exception {
		bundleContext = context;
	}

	/**
	 * {@inheritDoc}
	 */
	public void doStop(BundleContext context) throws Exception {
		plugin = null;
		bundleContext = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}
	
	/**
	 * Returns the bundle context.
	 * 
	 * @return the bundle context.
	 */
	public BundleContext getBundleContext() {
		return bundleContext;
	}
}
