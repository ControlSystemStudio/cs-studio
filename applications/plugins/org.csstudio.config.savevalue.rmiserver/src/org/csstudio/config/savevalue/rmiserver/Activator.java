package org.csstudio.config.savevalue.rmiserver;

import org.csstudio.platform.AbstractCssPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Activator extends AbstractCssPlugin {

	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "org.csstudio.config.savevalue.rmiserver";

	/**
	 * The shared instance.
	 */
	private static Activator _plugin;
	
	/**
	 * The constructor.
	 */
	public Activator() {
	}

	/**
	 * {@inheritDoc}
	 */
	public final void doStart(final BundleContext context) throws Exception {
		_plugin = this;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void doStop(final BundleContext context) throws Exception {
		_plugin = null;
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return _plugin;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPluginId() {
		return PLUGIN_ID;
	}

}
