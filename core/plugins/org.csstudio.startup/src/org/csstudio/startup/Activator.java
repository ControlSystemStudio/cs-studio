package org.csstudio.startup;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author awill
 * @version $Revision$
 * 
 */
public class Activator extends AbstractUIPlugin {

	/**
	 * The ID of this _plugin.
	 */
	public static final String PLUGIN_ID = "org.csstudio.startup"; //$NON-NLS-1$

	/**
	 * The shared instance of this _plugin activator.
	 */
	private static Activator _plugin;

	/**
	 * Standard constructor.
	 */
	public Activator() {
		_plugin = this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void start(final BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void stop(final BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance of this plugin activator.
	 * 
	 * @return The shared instance of this plugin activator.
	 */
	public static Activator getDefault() {
		return _plugin;
	}
}
