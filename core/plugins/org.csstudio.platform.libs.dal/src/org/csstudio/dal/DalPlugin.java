package org.csstudio.dal;

import org.csstudio.platform.AbstractCssPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DalPlugin extends AbstractCssPlugin {
	/**
	 * The ID of this plugin.
	 */
	public static final String ID = "org.csstudio.platform.libs.dal"; //$NON-NLS-1$

	/**
	 * The ID of the <code>plugs</code> extension point.
	 */
	public static final String EXTPOINT_PLUGS = ID + ".plugs"; //$NON-NLS-1$

	/**
	 * The shared instance.
	 */
	private static DalPlugin plugin;

	/**
	 * The constructor
	 */
	public DalPlugin() {
		plugin = this;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static DalPlugin getDefault() {
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
	@Override
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
