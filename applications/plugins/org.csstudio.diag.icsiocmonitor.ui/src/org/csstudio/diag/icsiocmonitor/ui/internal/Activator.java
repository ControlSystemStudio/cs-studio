package org.csstudio.diag.icsiocmonitor.ui.internal;

import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractCssUiPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.diag.icsiocmonitor.ui";

	// The shared instance
	private static Activator plugin;
	
	private BundleContext bundleContext;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void doStart(BundleContext context) throws Exception {
		plugin = this;
		bundleContext = context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void doStop(BundleContext context) throws Exception {
		plugin = null;
		bundleContext = null;
	}
	
	public BundleContext getBundleContext() {
		return bundleContext;
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

}
