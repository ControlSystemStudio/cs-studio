package org.csstudio.dal;

import org.csstudio.platform.AbstractCssPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractCssPlugin {

	/**
	 * The ID of this plugin.
	 */
	public static final String ID = "org.csstudio.platform.libs.dal"; //$NON-NLS-1$

	/**
	 * The shared instance.
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doStart(BundleContext context) throws Exception {
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doStop(BundleContext context) throws Exception {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPluginId() {
		return ID;
	}

}
