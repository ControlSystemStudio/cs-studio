package org.csstudio.utility.recordproperty;

import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractCssUiPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.utility.recordProperty";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doStart(BundleContext context) throws Exception {
		plugin = this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doStop(BundleContext context) throws Exception {
		plugin = null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPluginId() {
		return PLUGIN_ID;
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
